package com.rug.archedetector.controller;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
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


    @GetMapping("/email")
    public Page<Email> getAllMailFromMultipleLists(@RequestParam(defaultValue = "") Long[] mailingListIds, Pageable pageable) {
        return emailService.getAllMailByMailingListIds(mailingListIds, pageable);
    }

    @PostMapping("/email")
    public Email saveMail(@RequestBody Email mail){
        return emailService.saveMail(mail);
    }

    @GetMapping("/email-thread/{emailThreadId}/email")
    public List<Email> getMailByEmailThreadId(@PathVariable(value = "emailThreadId") Long emailThreadId) {
        return emailService.getMailByEmailThreadId(emailThreadId);
    }

    @GetMapping("/mailing-list/{mailingListId}/email")
    public Page<Email> getAllMail(@PathVariable(value = "mailingListId") Long mailingListId, Pageable pageable) {
        return emailService.getAllMailByMailingListId(mailingListId, pageable);
    }

    @GetMapping("/query-collection/{queryCollectionId}/email")
    public Page<Email> getAllMailByQueryCollectionId
            (@PathVariable(value = "queryCollectionId") Long queryCollectionId, Pageable pageable) {
        return emailService.getMailByQueryCollectionId(queryCollectionId, pageable);
    }
}
