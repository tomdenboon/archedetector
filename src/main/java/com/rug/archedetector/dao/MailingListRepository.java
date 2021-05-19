package com.rug.archedetector.dao;

import com.rug.archedetector.model.MailingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailingListRepository extends JpaRepository<MailingList, Long> {
}
