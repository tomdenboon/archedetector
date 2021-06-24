package com.rug.archedetector.dao;

import com.rug.archedetector.model.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface EmailRepository extends JpaRepository<Email, Long> {
    Page<Email> findByMailingListId(Long mailingListId, Pageable pageable);

    Page<Email> findByMailingListIdIn(List<Long> mailingListIds, Pageable pageable);

    Optional<Email> findByIdAndMailingListId(Long id, Long mailingListId);
}
