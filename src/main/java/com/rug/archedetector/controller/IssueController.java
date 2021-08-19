package com.rug.archedetector.controller;

import com.rug.archedetector.model.Comment;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IssueController {
    @Autowired
    IssueService issueService;

    @PostMapping("/issue")
    public Issue saveIssue(@RequestBody Issue issue){
        return issueService.saveIssue(issue);
    }

    @GetMapping("/issue-list/{issueListId}/issue")
    public Page<Issue> getIssuesByIssueListId(@PathVariable(value = "issueListId") Long issueListId, Pageable pageable) {
        return issueService.getIssueByIssueListId(issueListId, pageable);
    }

    @GetMapping("/issue/{issueId}/comment")
    public List<Comment> getCommentsByIssueId(@PathVariable(value = "issueId") Long issueId, Sort sort) {
        return issueService.getCommentsByIssueId(issueId, sort);
    }

    @GetMapping("/query-collection/{queryCollectionId}/issue")
    public Page<Issue> getAlLIssuesByQueryCollectionId
            (@PathVariable(value = "queryCollectionId") Long queryCollectionId, Pageable pageable) {
        return issueService.getIssueByQueryCollectionId(queryCollectionId, pageable);
    }
}
