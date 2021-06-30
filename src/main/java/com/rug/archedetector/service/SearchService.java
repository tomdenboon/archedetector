package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.lucene.IssueListSearcher;
import com.rug.archedetector.lucene.MailingListSearcher;
import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.MailingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private IssueRepository issueRepository;

    public Page<Email> queryMailingLists(String query, List<Long> mailingListIds, Pageable pageable){
        MailingListSearcher mailingListSearcher = new MailingListSearcher();
        List<Long> emailIds = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        try{
            emailIds = mailingListSearcher.searchInMultiple(query, mailingListIds, pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : emailIds){
            Email m = emailRepository.findById(id).orElseThrow();
            emails.add(m);
        }
        int size = pageable.getPageSize() * (pageable.getPageNumber()+1);
        if(emails.size() < pageable.getPageSize()){
            size = emails.size();
        } else {
            size += 1;
        }
        return new PageImpl<>(emails, pageable, size);
    }

    public Page<Issue> queryIssueLists(String query, List<Long> issueListIds, Pageable pageable){
        IssueListSearcher issueListSearcher = new IssueListSearcher();
        List<Long> issueIds = new ArrayList<>();
        List<Issue> issues = new ArrayList<>();
        try{
            issueIds = issueListSearcher.searchInMultiple(query, issueListIds, pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : issueIds){
            Issue i = issueRepository.findById(id).orElseThrow();
            issues.add(i);
        }

        int size = pageable.getPageSize() * (pageable.getPageNumber()+1);
        if(issues.size() < pageable.getPageSize()){
            size = issues.size();
        } else {
            size += 1;
        }
        return new PageImpl<>(issues, pageable, size);
    }
}
