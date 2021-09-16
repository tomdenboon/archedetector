package com.rug.archedetector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sisyphsu.dateparser.DateParser;
import com.rug.archedetector.dao.CommentRepository;
import com.rug.archedetector.dao.IssueListRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.lucene.IssueListIndexer;
import com.rug.archedetector.model.Comment;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.util.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class IssueListService {
    private static final String APACHE_JIRA_API_URL = "https://issues.apache.org/jira/rest/api/2/search";

    @Autowired
    private IssueListRepository issueListRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Value("${apache-issues.username}")
    private String apacheIssuesUsername;

    @Value("${apache-issues.password}")
    private String apacheIssuesPassword;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final IssueListIndexer issueListIndexer = new IssueListIndexer();

    public List<IssueList> getAll() {
        return issueListRepository.findAll();
    }

    @Transactional
    public IssueList addIssueListFromApacheIssues(IssueList issueList, List<String> filterUsers){
        issueListRepository.save(issueList);
        try {
            addFromKey(issueList, filterUsers, 0, 200);
        } catch(Exception e){
            e.printStackTrace();
        }
        return issueList;
    }

    /**
     * This function will use the jira api from apache to get all the issues into the database.
     * It will find all the issues from the key of a jira issue list.
     * Subsequently it will index all the found issues and comments for lucene.
     * This function works recursively, but should be refactored to a for loop for more clarity
     *
     * @param issueList an issue list that contains a key to one of apache Jira's projects
     * @param filterUsers if a comment contained an exact username from the list eh comment will not be added to the
     *                    database
     * @param startAt used for recursion
     * @param step how many issues it queries per api call
     */
    @Transactional
    public void addFromKey(IssueList issueList, List<String> filterUsers, int startAt, int step) {
        try {
            this.sendRequest(issueList.getKey(), startAt, step)
                    .thenAccept(obj -> this.parseAndSaveIssues(obj, issueList, filterUsers, step));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests a set of issues from the Jira API.
     * @param projectKey The identifier for the project to fetch issues from.
     * @param startAt The offset for search pagination.
     * @param step The number of items to fetch.
     * @return A future that, when complete, returns the JSON object that
     * contains the response from the API.
     * @throws IOException If an error occurs while sending the request.
     */
    private CompletableFuture<ObjectNode> sendRequest(String projectKey, int startAt, int step) throws IOException {
        final String authHeader = "Basic " + Base64.getEncoder().encodeToString((apacheIssuesUsername + ":" + apacheIssuesPassword).getBytes());
        var request = HttpRequest.newBuilder(UriBuilder.buildParams(APACHE_JIRA_API_URL, Map.of(
                        "jql", "project = " + projectKey,
                        "startAt", startAt,
                        "maxResults", step,
                        "fields", "description,comment,created,summary"
                )))
                .header("Authorization", authHeader)
                .header("Accept", "application/json")
                .GET().build();
        return this.httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(in -> {
                    try {
                        return this.mapper.readValue(in.body(), ObjectNode.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    private void parseAndSaveIssues(ObjectNode obj, IssueList issueList, List<String> filterUsers, int step) {
        List<Issue> issues = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        DateParser parser = DateParser.newBuilder().build();
        int total = obj.get("total").asInt();
        int offset = obj.get("startAt").asInt();
        ArrayNode issuesArray = obj.withArray("issues");
        for (var issueObj : issuesArray) {
            Issue issue = new Issue();
            issue.setIssueList(issueList);
            String key = issueObj.get("key").asText();
            issue.setKey(key);

            var fields = issueObj.get("fields");
            if(!fields.get("description").isNull()){
                issue.setDescription(fields.get("description").asText());
            }else{
                issue.setDescription("");
            }
            issue.setDate(parser.parseOffsetDateTime(fields.get("created").asText()).toZonedDateTime());
            issue.setSummary(fields.get("summary").asText());
            var commentsInfo = fields.get("comment");
            ArrayNode commentsJson = commentsInfo.withArray("comments");
            for (var commentObj : commentsJson) {
                Comment comment = new Comment();
                var authorJson = commentObj.get("author");
                String author = authorJson.get("displayName").asText();
                if(!filterUsers.contains(author)) {
                    String body = commentObj.get("body").asText();
                    OffsetDateTime date = parser.parseOffsetDateTime(commentObj.get("created").asText());
                    comment.setIssue(issue);
                    comment.setAuthor(author);
                    comment.setDate(date.toZonedDateTime());
                    comment.setBody(body);
                    comments.add(comment);
                }
            }
            issues.add(issue);
        }
        System.out.println(offset+step + "/" + total);
        issueRepository.saveAll(issues);
        commentRepository.saveAll(comments);
        issueListIndexer.index(issueList, issues, comments);
        if(offset+step<total){
            addFromKey(issueList, filterUsers, offset+step, step);
        }
    }

    /**
     * This function Deletes a issue list and its related objects from the database. First it checks if the
     * issue list exists. Then deletes all relations to the other tables and after that it deletes itself.
     */
    public ResponseEntity<?> delete(Long id) {
        return issueListRepository.findById(id).map(issueList -> {
            issueList.prepareForDelete();
            System.out.println("fetching issues");
            List<Issue> issues = issueRepository.findByIssueListId(issueList.getId());
            System.out.println("fetching comments");
            List<Comment> comments = commentRepository.findCommentByIssueIn(issues);
            System.out.println("deleting comments");
            commentRepository.deleteAll(comments);
            System.out.println("deleting issues");
            issueRepository.deleteAll(issues);
            System.out.println("deleting list");
            issueListRepository.delete(issueList);
            issueListIndexer.deleteIndex(issueList);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
