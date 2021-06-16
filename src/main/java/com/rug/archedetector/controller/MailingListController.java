package com.rug.archedetector.controller;

import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.service.MailingListService;
import org.apache.james.mime4j.MimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
public class MailingListController {
    @Autowired
    private MailingListService mailingListService;


    @GetMapping("/mailing-list")
    public List<MailingList> getAllMailingLists() {
        return mailingListService.getAll();
    }

    @PostMapping("/mailing-list/add-from-apache-archive")
    public MailingList addMailingListFromApache(@RequestBody MailingList mailingListRequest, @RequestParam(defaultValue = "") String[] filters) {
        return mailingListService.addFromApacheArchiveWithFilters(mailingListRequest, filters);
    }

    @DeleteMapping("/mailing-list/{id}")
    public ResponseEntity<?> deleteMailingList(@PathVariable long id) {
        return mailingListService.delete(id);
    }


}
