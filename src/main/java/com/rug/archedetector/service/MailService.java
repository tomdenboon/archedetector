package com.rug.archedetector.service;

import com.rug.archedetector.dao.MailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Mail;
import com.rug.archedetector.model.MailingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
public class MailService {
    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailingListRepository mailingListRepository;

    public MailService(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    public Page<Mail> getAllMailByMailingListId(Long id, Pageable pageable){
        return mailRepository.findByMailingListId(id, pageable);
    }

    public Mail saveMail(Long mailingListId, Mail mail){
        return mailingListRepository.findById(mailingListId).map(mailingList -> {
            mail.setMailingList(mailingList);
            return mailRepository.save(mail);
        }).orElseThrow(() -> new ResourceNotFoundException(""));
    }

    public List<Mail> batchSaveMail(Long mailingListId, List<Mail> mail){
        return mailingListRepository.findById(mailingListId).map(mailingList -> {
            for (Mail m : mail) {
                m.setMailingList(mailingList);
            }
            return mailRepository.saveAll(mail);
        }).orElseThrow(() -> new ResourceNotFoundException(""));
    }

    public ResponseEntity<?> deleteByIdAndMailingId(Long mailId, Long mailingListId) {
        return mailRepository.findByIdAndMailingListId(mailId, mailingListId).map(comment -> {
            mailRepository.delete(comment);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + mailId + " and postId " + mailingListId));
    }
}
