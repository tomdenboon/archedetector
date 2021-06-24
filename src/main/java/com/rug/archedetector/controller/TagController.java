package com.rug.archedetector.controller;

import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.model.Tag;
import com.rug.archedetector.service.MailingListService;
import com.rug.archedetector.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class TagController {
    @Autowired
    private TagService tagService;


    @GetMapping("/tags")
    public List<Tag> getTags() {
        return tagService.getAll();
    }

    @PostMapping("/tags")
    public Tag addTag(@RequestBody Tag tag) {
        return tagService.save(tag);
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable long id) {
        return tagService.delete(id);
    }
}
