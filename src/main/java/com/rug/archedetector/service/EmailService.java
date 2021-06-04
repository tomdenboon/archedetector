package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    @Autowired
    private final EmailRepository emailRepository;

    @Autowired
    private MailingListRepository mailingListRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public Page<Email> getAllMailByMailingListId(Long id, Pageable pageable) {
        return emailRepository.findByMailingListId(id, pageable);
    }

    public Email saveMail(Long mailingListId, Email email) {
        return mailingListRepository.findById(mailingListId).map(mailingList -> {
            email.setMailingList(mailingList);
            return emailRepository.save(email);
        }).orElseThrow(() -> new ResourceNotFoundException(""));
    }

    public List<Email> batchSaveMail(Long mailingListId, List<Email> emails) {
        return mailingListRepository.findById(mailingListId).map(mailingList -> {
            for (Email m : emails) {
                m.setMailingList(mailingList);
            }
            return emailRepository.saveAll(emails);
        }).orElseThrow(() -> new ResourceNotFoundException(""));
    }

    public ResponseEntity<?> deleteByIdAndMailingId(Long mailId, Long mailingListId) {
        return emailRepository.findByIdAndMailingListId(mailId, mailingListId).map(mail -> {
            emailRepository.delete(mail);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + mailId + " and postId " + mailingListId));
    }
}
