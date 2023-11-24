package com.rug.archedetector.dao;

import com.rug.archedetector.model.IssueList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueListRepository extends JpaRepository<IssueList, Long> {
}
