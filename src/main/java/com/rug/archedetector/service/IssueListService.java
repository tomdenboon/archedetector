package com.rug.archedetector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sisyphsu.dateparser.DateParser;
import com.rug.archedetector.dao.CommentRepository;
import com.rug.archedetector.dao.IssueListRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.lucene.IssueListIndexer;
import com.rug.archedetector.model.Comment;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueListService {
    private static final String APACHE_JIRA_API_URL = "https://issues.apache.org/jira/rest/api/2/search";

    private final IssueListRepository issueListRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final JdbcTemplate jdbcTemplate;

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
    public IssueList createIssueList(IssueList issueList) {
        return this.issueListRepository.save(issueList);
    }

    @Transactional
    @Async
    public void addIssuesToList(IssueList issueList, List<String> usernameBlacklist) {
        try {
            addFromKey(issueList, usernameBlacklist, 0, 200);
        } catch(Exception e){
            e.printStackTrace();
        }
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
                    .thenAccept(obj -> this.parseAndSaveIssues(obj, issueList, filterUsers, step)).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
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
        var uri = UriBuilder.buildParams(APACHE_JIRA_API_URL, Map.of(
                "jql", "project = " + projectKey,
                "startAt", startAt,
                "maxResults", step,
                "fields", "description,comment,created,summary"
        ));
        var request = HttpRequest.newBuilder(uri)
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
            issue.setKey(issueObj.get("key").asText());

            var fields = issueObj.get("fields");
            if(!fields.get("description").isNull()){
                issue.setDescription(fields.get("description").asText());
            }else{
                issue.setDescription("");
            }
            issue.setDate(parser.parseOffsetDateTime(fields.get("created").asText()).toZonedDateTime());
            issue.setSummary(fields.get("summary").asText());
            var commentsInfo = fields.get("comment");
            if (commentsInfo == null || commentsInfo.isNull()) continue;
            ArrayNode commentsJson = commentsInfo.withArray("comments");
            for (var commentObj : commentsJson) {
                var authorJson = commentObj.get("author");
                String author = authorJson.get("displayName").asText();
                if(!filterUsers.contains(author)) {
                    String body = commentObj.get("body").asText();
                    OffsetDateTime date = parser.parseOffsetDateTime(commentObj.get("created").asText());
                    comments.add(new Comment(
                            issue,
                            author,
                            date.toZonedDateTime(),
                            body
                    ));
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
    @Transactional
    public ResponseEntity<?> delete(Long id) {
        return this.issueListRepository.findById(id).map(issueList -> {
            issueList.prepareForDelete();
            // We use plain SQL since it is much faster for large operations.
            this.jdbcTemplate.update(
                    "DELETE FROM comment c WHERE c.issue_id IN (SELECT id FROM issue WHERE issue_list_id = ?)",
                    id
            );
            this.jdbcTemplate.update("DELETE FROM issue WHERE issue_list_id = ?", id);
            this.issueListRepository.delete(issueList);
            this.issueListIndexer.deleteIndex(issueList);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
