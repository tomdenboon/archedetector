package com.rug.archedetector.controller;

import com.rug.archedetector.model.Mail;
import com.rug.archedetector.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MailController {
    @Autowired
    MailService mailService;

    @GetMapping("/mailing-list/{mailingListId}/mail")
    public Page<Mail> getAllMail(@PathVariable (value = "mailingListId") Long mailingListId, Pageable pageable){
        return mailService.getAllMailByMailingListId(mailingListId, pageable);
    }

    @PostMapping("/mailing-list/{mailingListId}/mail")
    public Mail saveMail(@PathVariable (value="mailingListId") Long mailingListId, @RequestBody Mail mail){
        return mailService.saveMail(mailingListId, mail);
    }

    @PostMapping("/mailing-list/{mailingListId}/mail/batch_add")
    public List<Mail> saveMail(@PathVariable (value="mailingListId") Long mailingListId, @RequestBody List<Mail> mail){
        return mailService.batchSaveMail(mailingListId, mail);
    }

    @DeleteMapping("/mailing-list/{mailingListId}/mail/{mailId}")
    public ResponseEntity<?> deleteMail(@PathVariable (value="mailingListId") Long mailingListId,
                                        @PathVariable (value="mailId") Long mailId){
        return mailService.deleteByIdAndMailingId(mailId, mailingListId);
    }
}
