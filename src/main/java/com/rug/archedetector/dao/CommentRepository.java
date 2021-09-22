package com.rug.archedetector.dao;

import com.rug.archedetector.model.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentByIssueId(Long issueId, Sort sort);
}
