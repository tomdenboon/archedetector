package com.rug.archedetector.controller;


import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.service.IssueListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IssueListController {
    private final IssueListService issueListService;

    @GetMapping("/issue-list")
    public List<IssueList> getAllIssueLists() {
        return issueListService.getAll();
    }

    @PostMapping("/issue-list/add-from-apache-issues")
    public IssueList addIssueListFromApache(@RequestBody IssueList issueList, @RequestParam(defaultValue = "") List<String> filterUsers) {
        issueList = this.issueListService.createIssueList(issueList);
        this.issueListService.addIssuesToList(issueList, filterUsers);
        return issueList;
    }

    @DeleteMapping("/issue-list/{id}")
    public ResponseEntity<?> deleteIssueList(@PathVariable long id) {
        return issueListService.delete(id);
    }
}
