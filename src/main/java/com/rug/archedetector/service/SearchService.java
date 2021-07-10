package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.EmailThreadRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.lucene.IssueListSearcher;
import com.rug.archedetector.lucene.MailingListSearcher;
import com.rug.archedetector.model.*;
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

    @Autowired
    private EmailThreadRepository emailThreadRepository;

    public Page<Email> queryEmail(String query, List<Long> mailingListIds, Pageable pageable){
        MailingListSearcher mailingListSearcher = new MailingListSearcher();
        List<Long> emailIds = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        try{
            emailIds = mailingListSearcher.searchEmail(query, mailingListIds,
                    pageable.getPageNumber() * pageable.getPageSize(), (pageable.getPageNumber() + 1) * pageable.getPageSize());
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

    public List<EmailMessageIdAndTags> exportEmailQuery(String query, List<Long> mailingListIds){
        MailingListSearcher mailingListSearcher = new MailingListSearcher();
        List<Long> emailIds = new ArrayList<>();
        List<EmailMessageIdAndTags> emails = new ArrayList<>();
        try{
            emailIds = mailingListSearcher.searchEmail(query, mailingListIds, 0, Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : emailIds){
            EmailMessageIdAndTags m = emailRepository.findEmailById(id);
            emails.add(m);
        }
        return emails;
    }

    public Page<EmailThread> queryThreads(String query, List<Long> mailingListIds, Pageable pageable){
        MailingListSearcher mailingListSearcher = new MailingListSearcher();
        List<Long> threatIds = new ArrayList<>();
        List<EmailThread> emailThreads = new ArrayList<>();
        try{
            threatIds = mailingListSearcher.searchThreads(query, mailingListIds,
                    pageable.getPageNumber() * pageable.getPageSize(), (pageable.getPageNumber() + 1) * pageable.getPageSize());
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : threatIds){
            EmailThread emailThread = emailThreadRepository.findById(id).orElseThrow();
            emailThreads.add(emailThread);
        }
        int size = pageable.getPageSize() * (pageable.getPageNumber()+1);
        if(emailThreads.size() < pageable.getPageSize()){
            size = emailThreads.size();
        } else {
            size += 1;
        }
        return new PageImpl<>(emailThreads, pageable, size);
    }

    public List<EmailThread> exportThreadQuery(String query, List<Long> mailingListIds){
        MailingListSearcher mailingListSearcher = new MailingListSearcher();
        List<Long> threadIds = new ArrayList<>();
        List<EmailThread> threads = new ArrayList<>();
        try{
            threadIds = mailingListSearcher.searchThreads(query, mailingListIds, 0, Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : threadIds){
            EmailThread m = emailThreadRepository.findById(id).orElseThrow();
            threads.add(m);
        }
        return threads;
    }

    public Page<Issue> queryIssueLists(String query, List<Long> issueListIds, Pageable pageable){
        IssueListSearcher issueListSearcher = new IssueListSearcher();
        List<Long> issueIds = new ArrayList<>();
        List<Issue> issues = new ArrayList<>();
        try{
            issueIds = issueListSearcher.searchInMultiple(query, issueListIds,
                    pageable.getPageNumber() * pageable.getPageSize(),(pageable.getPageNumber() + 1) * pageable.getPageSize());
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

    public List<IssueKeyAndTags> exportIssueQuery(String query, List<Long> issueListIds){
        IssueListSearcher issueListSearcher = new IssueListSearcher();
        List<Long> issueIds = new ArrayList<>();
        List<IssueKeyAndTags> issues = new ArrayList<>();
        try{
            issueIds = issueListSearcher.searchInMultiple(query, issueListIds, 0, Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : issueIds){
            IssueKeyAndTags i = issueRepository.findIssueById(id);
            issues.add(i);
        }
        return issues;
    }


}
