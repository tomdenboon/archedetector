package com.rug.archedetector.dao;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findByIssueListId(Long issueListId, Pageable pageable);
    List<Issue> findByIssueListId(Long issueListId);

    Page<Issue> findByIssueListIdIn(List<Long> mailingListIds, Pageable pageable);
}
