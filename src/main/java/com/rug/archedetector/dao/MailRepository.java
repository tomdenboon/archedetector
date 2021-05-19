package com.rug.archedetector.dao;

import com.rug.archedetector.model.Mail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MailRepository extends JpaRepository<Mail, Integer> {
    Page<Mail> findByMailingListId(Long mailingListId, Pageable pageable);

    Optional<Mail> findByIdAndMailingListId(Long id, Long mailingListId);
}
