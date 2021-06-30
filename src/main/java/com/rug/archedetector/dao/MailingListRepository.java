package com.rug.archedetector.dao;

import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.model.QueryCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailingListRepository extends JpaRepository<MailingList, Long> {
}
