package com.rug.archedetector.controller;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.EmailThread;
import com.rug.archedetector.service.EmailService;
import com.rug.archedetector.service.EmailThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailThreadController {
    @Autowired
    EmailThreadService emailThreadService;

    @PostMapping("/email-thread")
    public EmailThread saveMail(@RequestBody EmailThread mail){
        return emailThreadService.saveMail(mail);
    }

    @GetMapping("/mailing-list/{mailingListId}/email-thread")
    public Page<EmailThread> getThreadsByMailingListId
            (@PathVariable(value = "mailingListId") Long mailingListId, Pageable pageable) {
        return emailThreadService.getThreadsByMailingListId(mailingListId, pageable);
    }

    @GetMapping("/query-collection/{queryCollectionId}/email-thread")
    public Page<EmailThread> getThreadsByQueryCollectionId
            (@PathVariable(value = "queryCollectionId") Long queryCollectionId, Pageable pageable) {
        return emailThreadService.getThreadsByQueryCollectionId(queryCollectionId, pageable);
    }
}