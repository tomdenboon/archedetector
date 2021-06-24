package com.rug.archedetector.service;

import com.rug.archedetector.dao.TagRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TagService {
    @Autowired
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAll(){
        return tagRepository.findAll();
    }

    public Tag save(Tag tag){
        return tagRepository.save(tag);
    }

    public ResponseEntity<?> delete(Long id) {
        return tagRepository.findById(id).map(tag -> {
            tagRepository.delete(tag);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
