package com.rug.archedetector.service;

import com.rug.archedetector.dao.*;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Comment;
import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QueryCollectionRepository queryCollectionRepository;

    public Page<Issue> getIssueByIssueListId(Long id, Pageable pageable) {
        return issueRepository.findByIssueListId(id, pageable);
    }

    public List<Comment> getCommentsByIssueId(Long id) {
        return commentRepository.findCommentByIssueId(id);
    }

    public Page<Issue> getIssueByQueryCollectionId(Long queryCollectionId, Pageable pageable) {
        return queryCollectionRepository.findById(queryCollectionId).map(queryCollection -> {
            List<Long> listIssueId = new ArrayList<>();
            for(IssueList issueList : queryCollection.getIssueLists()){
                listIssueId.add(issueList.getId());
            }
            return issueRepository.findByIssueListIdIn(listIssueId, pageable);
        }).orElseThrow();
    }

    public Issue saveIssue(Issue issue) {
        return issueRepository.save(issue);
    }

}
