package com.rug.archedetector.dao;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.EmailMessageIdAndTags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface EmailRepository extends JpaRepository<Email, Long> {
    Page<Email> findByMailingListId(Long mailingListId, Pageable pageable);
    List<Email> findByMailingListId(Long mailingListId);
    List<Email> findByEmailThreadId(Long emailThreadId, Sort sort);
    EmailMessageIdAndTags findEmailById(Long id);

    Page<Email> findByMailingListIdIn(List<Long> mailingListIds, Pageable pageable);

}
