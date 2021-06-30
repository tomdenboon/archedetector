package com.rug.archedetector.controller;


import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.service.IssueListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IssueListController {
    @Autowired
    private IssueListService issueListService;

    @GetMapping("/issue-list")
    public List<IssueList> getAllIssueLists() {
        return issueListService.getAll();
    }

    @PostMapping("/issue-list/add-from-apache-issues")
    public IssueList addIssueListFromApache(@RequestBody IssueList issueList) {
        System.out.println(issueList.getKey() + " " + issueList.getName());
        return issueListService.addIssueListFromApacheIssues(issueList);
    }

    @DeleteMapping("/issue-list/{id}")
    public ResponseEntity<?> deleteIssueList(@PathVariable long id) {
        return issueListService.delete(id);
    }
}
