package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.EmailThreadRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.lucene.IssueListIndexer;
import com.rug.archedetector.lucene.LuceneSearcher;
import com.rug.archedetector.lucene.MailingListIndexer;
import com.rug.archedetector.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// this service needs refactoring to remove duplicated code
@Service
public class SearchService {
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private EmailThreadRepository emailThreadRepository;

    private final LuceneSearcher searcher = new LuceneSearcher();

    /**
     *
     * @param query a string in the format of a lucene query.
     * @param mailingListIds a list of the mailing lists the function needs to search in.
     * @param pageable determines the returned page parameters.
     * @return a page of result email as found by the lucene search engine.
     */
    public Page<Email> queryEmail(String query, List<Long> mailingListIds, Pageable pageable){
        List<Long> emailIds = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        try{
            emailIds = searcher.searchInMultipleIndices(
                    LuceneSearcher.emailQueryParser,
                    MailingListIndexer.mailIndexDir,
                    query,
                    mailingListIds,
                    pageable.getPageNumber() * pageable.getPageSize(),
                    (pageable.getPageNumber() + 1) * pageable.getPageSize());
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

    /**
     * @param query a string in the format of a lucene query.
     * @param mailingListIds a list of the mailing lists the function needs to search in.
     * @return a list of result mail. Only important information of the mail is used so the function is faster.
     */
    public List<EmailMessageIdAndTags> exportEmailQuery(String query, List<Long> mailingListIds){
        List<Long> emailIds = new ArrayList<>();
        List<EmailMessageIdAndTags> emails = new ArrayList<>();
        try{
            emailIds = searcher.searchInMultipleIndices(
                    LuceneSearcher.emailQueryParser,
                    MailingListIndexer.mailIndexDir,
                    query,
                    mailingListIds,
                    0,
                    Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : emailIds){
            EmailMessageIdAndTags m = emailRepository.findEmailById(id);
            emails.add(m);
        }
        return emails;
    }

    /**
     * @param query a string in the format of a lucene query.
     * @param mailingListIds a list of the mailing lists the function needs to search in.
     * @param pageable determines the returned page parameters.
     * @return a page of result threads as found by the lucene search engine.
     */
    public Page<EmailThread> queryThreads(String query, List<Long> mailingListIds, Pageable pageable){
        List<Long> threadIds = new ArrayList<>();
        List<EmailThread> emailThreads = new ArrayList<>();
        try{

            threadIds = searcher.searchInMultipleIndices(
                    LuceneSearcher.threadQueryParser,
                    MailingListIndexer.threadIndexDir,
                    query,
                    mailingListIds,
                    pageable.getPageNumber() * pageable.getPageSize(),
                    (pageable.getPageNumber() + 1) * pageable.getPageSize());
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : threadIds){
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

    /**
     * @param query a string in the format of a lucene query.
     * @param mailingListIds a list of the mailing lists the function needs to search in.
     * @return a list of result threads.
     */
    public List<EmailThread> exportThreadQuery(String query, List<Long> mailingListIds){
        List<Long> threadIds = new ArrayList<>();
        List<EmailThread> threads = new ArrayList<>();
        try{
            threadIds = searcher.searchInMultipleIndices(
                    LuceneSearcher.threadQueryParser,
                    MailingListIndexer.threadIndexDir,
                    query,
                    mailingListIds,
                    0,
                    Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : threadIds){
            EmailThread m = emailThreadRepository.findById(id).orElseThrow();
            threads.add(m);
        }
        return threads;
    }

    /**
     *
     * @param query a string in the format of a lucene query.
     * @param issueListIds a list of the issue lists the function needs to search in.
     * @param pageable determines the returned page parameters.
     * @return a page of result issues as found by the lucene search engine.
     */
    public Page<Issue> queryIssueLists(String query, List<Long> issueListIds, Pageable pageable){
        List<Long> issueIds = new ArrayList<>();
        List<Issue> issues = new ArrayList<>();
        try{

            issueIds = searcher.searchInMultipleIndices(
                    LuceneSearcher.issueQueryParser,
                    IssueListIndexer.ISSUES_LIST_INDEX_DIR.toString(),
                    query,
                    issueListIds,
                    pageable.getPageNumber() * pageable.getPageSize(),
                    (pageable.getPageNumber() + 1) * pageable.getPageSize());
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

    /**
     * @param query a string in the format of a lucene query.
     * @param issueListIds a list of the issue lists the function needs to search in.
     * @return a list of result issues. Only important information of the issue is used so the function is faster.
     */
    public List<IssueKeyAndTags> exportIssueQuery(String query, List<Long> issueListIds){
        List<Long> issueIds = new ArrayList<>();
        List<IssueKeyAndTags> issues = new ArrayList<>();
        try{
            issueIds = searcher.searchInMultipleIndices(
                    LuceneSearcher.issueQueryParser,
                    IssueListIndexer.ISSUES_LIST_INDEX_DIR.toString(),
                    query,
                    issueListIds,
                    0,
                    Integer.MAX_VALUE);
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
