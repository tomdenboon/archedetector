package com.rug.archedetector.service;

import com.rug.archedetector.dao.TagRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getAll(){
        return tagRepository.findAll();
    }

    public Tag save(Tag tag){
        return tagRepository.save(tag);
    }

    /**
     * This function Deletes a tag from the database. First it checks if the tag exists.
     * Then deletes all relations to the other tables and after deletes itself.
     *
     * @param id a tag id
     * @return a response
     */
    public ResponseEntity<?> delete(Long id) {
        return tagRepository.findById(id).map(tag -> {
            tag.prepareForDelete();
            tagRepository.delete(tag);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
