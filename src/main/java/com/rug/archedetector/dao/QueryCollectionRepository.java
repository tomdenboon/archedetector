package com.rug.archedetector.dao;

import com.rug.archedetector.model.QueryCollection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryCollectionRepository extends JpaRepository<QueryCollection, Long> {
}
