package com.rug.archedetector.controller;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmailController {
    @Autowired
    EmailService emailService;

    @GetMapping("/mailing-list/{mailingListId}/email")
    public Page<Email> getAllMail(@PathVariable(value = "mailingListId") Long mailingListId, Pageable pageable) {
        return emailService.getAllMailByMailingListId(mailingListId, pageable);
    }

    @PostMapping("/mailing-list/{mailingListId}/email")
    public Email saveMail(@PathVariable(value = "mailingListId") Long mailingListId, @RequestBody Email email) {
        return emailService.saveMail(mailingListId, email);
    }

    @PostMapping("/mailing-list/{mailingListId}/email/batch_add")
    public List<Email> saveAllMail(@PathVariable(value = "mailingListId") Long mailingListId, @RequestBody List<Email> emails) {
        return emailService.batchSaveMail(mailingListId, emails);
    }

    @DeleteMapping("/mailing-list/{mailingListId}/email/{mailId}")
    public ResponseEntity<?> deleteMail(@PathVariable(value = "mailingListId") Long mailingListId,
                                        @PathVariable(value = "mailId") Long emailId) {
        return emailService.deleteByIdAndMailingId(emailId, mailingListId);
    }
}
