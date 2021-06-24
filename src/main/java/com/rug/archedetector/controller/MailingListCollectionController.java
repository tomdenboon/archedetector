package com.rug.archedetector.controller;

import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.model.MailingListCollection;
import com.rug.archedetector.model.Tag;
import com.rug.archedetector.service.MailingListCollectionService;
import com.rug.archedetector.service.MailingListService;
import com.rug.archedetector.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class MailingListCollectionController {
    @Autowired
    private MailingListCollectionService mailingListCollectionService;

    @GetMapping("/mailing-list-collection")
    public List<MailingListCollection> getTags() {
        return mailingListCollectionService.getAll();
    }

    @PostMapping("/mailing-list-collection")
    public MailingListCollection addTag(@RequestBody MailingListCollection mailingListCollection) {
        return mailingListCollectionService.save(mailingListCollection);
    }

    @DeleteMapping("/mailing-list-collection/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable long id) {
        return mailingListCollectionService.delete(id);
    }
}
