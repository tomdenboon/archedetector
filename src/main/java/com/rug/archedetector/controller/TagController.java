package com.rug.archedetector.controller;

import com.rug.archedetector.model.Tag;
import com.rug.archedetector.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/tag")
    public List<Tag> getTags() {
        return tagService.getAll();
    }

    @PostMapping("/tag")
    public Tag addTag(@RequestBody Tag tag) {
        return tagService.save(tag);
    }

    @DeleteMapping("/tag/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable long id) {
        return tagService.delete(id);
    }
}
