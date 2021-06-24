package com.rug.archedetector.service;

import com.rug.archedetector.dao.MailingListCollectionRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.dao.TagRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.MailingListCollection;
import com.rug.archedetector.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailingListCollectionService {
    @Autowired
    private final MailingListCollectionRepository mailingListCollectionRepository;

    public MailingListCollectionService(MailingListCollectionRepository mailingListCollectionRepository) {
        this.mailingListCollectionRepository = mailingListCollectionRepository;
    }

    public List<MailingListCollection> getAll(){
        return mailingListCollectionRepository.findAll();
    }

    public MailingListCollection save(MailingListCollection mailingListCollection){
        return mailingListCollectionRepository.save(mailingListCollection);
    }

    public ResponseEntity<?> delete(Long id) {
        return mailingListCollectionRepository.findById(id).map(mailingListCollection -> {
            mailingListCollectionRepository.delete(mailingListCollection);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
