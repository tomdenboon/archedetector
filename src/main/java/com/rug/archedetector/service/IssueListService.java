package com.rug.archedetector.service;

import com.rug.archedetector.dao.IssueListRepository;
import com.rug.archedetector.lucene.IssueListIndexer;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.service.issue.ApacheJiraIssueFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueListService {
    private final IssueListRepository issueListRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ApacheJiraIssueFetcher apacheJiraIssueFetcher;
    private final IssueListIndexer issueListIndexer = new IssueListIndexer();

    @Transactional(readOnly = true)
    public List<IssueList> getAll() {
        return issueListRepository.findAll();
    }

    @Transactional
    public IssueList createIssueList(IssueList issueList) {
        return this.issueListRepository.save(issueList);
    }

    @Transactional
    @Async
    public void addIssuesToList(IssueList issueList, List<String> usernameBlacklist) {
        this.apacheJiraIssueFetcher.fetchIssues(issueList);
    }

    /**
     * This function Deletes a issue list and its related objects from the database. First it checks if the
     * issue list exists. Then deletes all relations to the other tables and after that it deletes itself.
     */
    @Transactional
    public ResponseEntity<?> delete(Long id) {
        return this.issueListRepository.findById(id).map(issueList -> {
            issueList.prepareForDelete();
            // We use plain SQL since it is much faster for large operations.
            this.jdbcTemplate.update(
                    "DELETE FROM comment c WHERE c.issue_id IN (SELECT id FROM issue WHERE issue_list_id = ?)",
                    id
            );
            this.jdbcTemplate.update("DELETE FROM issue WHERE issue_list_id = ?", id);
            this.issueListRepository.delete(issueList);
            this.issueListIndexer.deleteIndex(issueList);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
