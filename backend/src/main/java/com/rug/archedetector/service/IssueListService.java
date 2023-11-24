package com.rug.archedetector.service;

import com.github.sisyphsu.dateparser.DateParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.rug.archedetector.dao.CommentRepository;
import com.rug.archedetector.dao.IssueListRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.lucene.IssueListIndexer;
import com.rug.archedetector.lucene.MailingListIndexer;
import com.rug.archedetector.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class IssueListService {
    @Autowired
    private IssueListRepository issueListRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final IssueListIndexer issueListIndexer = new IssueListIndexer();

    public List<IssueList> getAll() {
        return issueListRepository.findAll();
    }

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
    public void addFromKey(IssueList issueList, List<String> filterUsers, int startAt, int step) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://issues.apache.org/jira/rest/api/2/search")
                .basicAuth("tomdenboon", "SY@UD48hDCX$Uf*")
                .header("Accept", "application/json")
                .queryString("jql", "project = " + issueList.getKey())
                .queryString("startAt", startAt)
                .queryString("maxResults", step)
                .queryString("fields", "description,comment,created,summary")
                .asJson();
        JSONObject jsonObject = response.getBody().getObject();
        List<Issue> issues = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        DateParser parser = DateParser.newBuilder().build();
        int total = jsonObject.getInt("total");
        int offset = jsonObject.getInt("startAt");
        JSONArray issuesJson = jsonObject.getJSONArray("issues");
        for (int i = 0; i < issuesJson.length(); i++) {
            Issue issue = new Issue();
            issue.setIssueList(issueList);
            JSONObject issueJson = issuesJson.getJSONObject(i);
            String key = issueJson.getString("key");
            issue.setKey(key);

            JSONObject fields = issueJson.getJSONObject("fields");
            if(!fields.isNull("description")){
                issue.setDescription(fields.getString("description"));
            }else{
                issue.setDescription("");
            }
            issue.setDate(parser.parseOffsetDateTime(fields.getString("created")).toZonedDateTime());
            String summary = fields.getString("summary");
            issue.setSummary(summary);
            JSONObject commentsInfo = fields.getJSONObject("comment");
            JSONArray commentsJson = commentsInfo.getJSONArray("comments");
            for(int j = 0; j < commentsJson.length(); j++){
                Comment comment = new Comment();
                JSONObject commentJson = commentsJson.getJSONObject(j);
                JSONObject authorJson = commentJson.getJSONObject("author");
                String author = authorJson.getString("displayName");
                if(!filterUsers.contains(author)) {
                    String body = commentJson.getString("body");
                    OffsetDateTime date = parser.parseOffsetDateTime(commentJson.getString("created"));
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
