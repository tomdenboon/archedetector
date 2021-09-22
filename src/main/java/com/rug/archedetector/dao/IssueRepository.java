package com.rug.archedetector.dao;

import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueKeyAndTags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findByIssueListId(Long issueListId, Pageable pageable);

    IssueKeyAndTags findIssueById(Long id);

    Page<Issue> findByIssueListIdIn(List<Long> mailingListIds, Pageable pageable);

    @Modifying
    void deleteAllByIssueListId(long issueListId);
}
