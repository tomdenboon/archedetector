package com.rug.archedetector.dao;


import com.rug.archedetector.model.EmailThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailThreadRepository extends JpaRepository<EmailThread, Long> {
    List<EmailThread> findByMailingListId(Long id);
    Page<EmailThread> findByMailingListId(Long mailingListId, Pageable pageable);
    Page<EmailThread> findByMailingListIdIn(List<Long> mailingListIds, Pageable pageable);
}
