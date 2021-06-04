package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.lucene.MailingListIndexer;
import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.util.ApacheMailingListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailingListService {
    @Autowired
    private final MailingListRepository mailingListRepository;

    @Autowired
    private EmailRepository emailRepository;

    private final MailingListIndexer mailingListIndexer = new MailingListIndexer();

    private final ApacheMailingListParser apacheMailingListParser = new ApacheMailingListParser();

    public MailingListService(MailingListRepository mailingListRepository) {
        this.mailingListRepository = mailingListRepository;
    }

    public List<MailingList> getAll() {
        return mailingListRepository.findAll();
    }

    public MailingList addFromApacheArchive(MailingList mailinglist) {
        mailinglist = mailingListRepository.save(mailinglist);
        List<Email> emails = apacheMailingListParser.getMailFromMailingList(mailinglist);
        emailRepository.saveAll(emails);
        mailingListIndexer.index(mailinglist, emails);
        return mailinglist;
    }

    public ResponseEntity<?> delete(Long id) {
        return mailingListRepository.findById(id).map(mailingList -> {
            mailingListRepository.delete(mailingList);
            mailingListIndexer.deleteIndex(mailingList);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
