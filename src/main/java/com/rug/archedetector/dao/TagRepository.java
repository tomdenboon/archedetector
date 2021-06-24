package com.rug.archedetector.dao;


import com.rug.archedetector.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
