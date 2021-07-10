package com.rug.archedetector.controller;

import com.rug.archedetector.model.*;
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
    public Page<Email> searchEmails(@RequestParam String q, @RequestParam List<Long> mailingListIds, Pageable pageable) {
        return searchService.queryEmail(q, mailingListIds, pageable);
    }

    @GetMapping("/email/export")
    public List<EmailMessageIdAndTags> exportSearchEmails(@RequestParam String q, @RequestParam List<Long> mailingListIds) {
        return searchService.exportEmailQuery(q, mailingListIds);
    }

    @GetMapping("/email-thread/search")
    public Page<EmailThread> searchThreads(@RequestParam String q, @RequestParam List<Long> mailingListIds, Pageable pageable) {
        return searchService.queryThreads(q, mailingListIds, pageable);
    }

    @GetMapping("/email-thread/export")
    public List<EmailThread> exportSearchThreads(@RequestParam String q, @RequestParam List<Long> mailingListIds, Pageable pageable) {
        return searchService.exportThreadQuery(q, mailingListIds);
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
