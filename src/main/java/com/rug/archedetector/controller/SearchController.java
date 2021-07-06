package com.rug.archedetector.controller;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.EmailMessageIdAndTags;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueKeyAndTags;
import com.rug.archedetector.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/email/search")
    public Page<Email> searchMailingLists(@RequestParam String q, @RequestParam List<Long> mailingListIds, Pageable pageable) {
        return searchService.queryMailingLists(q, mailingListIds, pageable);
    }

    @GetMapping("/email/export")
    public List<EmailMessageIdAndTags> exportSearchMailingLists(@RequestParam String q, @RequestParam List<Long> mailingListIds) {
        return searchService.exportMailQuery(q, mailingListIds);
    }


    @GetMapping("/issue/search")
    public Page<Issue> searchIssueLists(@RequestParam String q, @RequestParam List<Long> issueListIds, Pageable pageable) {
        return searchService.queryIssueLists(q, issueListIds, pageable);
    }

    @GetMapping("/issue/export")
    public List<IssueKeyAndTags> exportSearchIssueLists(@RequestParam String q, @RequestParam List<Long> issueListIds) {
        return searchService.exportIssueQuery(q, issueListIds);
    }
}
