package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.lucene.MailingListIndexer;
import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.model.QueryCollection;
import com.rug.archedetector.util.ApacheMailingListParser;
import com.rug.archedetector.util.EmailFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
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

    public MailingList addFromApacheArchiveWithFilters(MailingList mailinglist, String[] filters) {
        mailinglist = mailingListRepository.save(mailinglist);
        List<Email> emails = apacheMailingListParser.getMailFromMailingList(mailinglist);
        EmailFilter filter = new EmailFilter();
        emails = filter.filterMail(emails, Arrays.asList(filters));
        emailRepository.saveAll(emails);
        mailingListIndexer.index(mailinglist, emails);
        return mailinglist;
    }

    public ResponseEntity<?> delete(Long id) {
        return mailingListRepository.findById(id).map(mailingList -> {
            mailingList.prepareForDelete();
            List<Email> emails = emailRepository.findByMailingListId(id);
            emailRepository.deleteAll(emails);
            mailingListRepository.delete(mailingList);
            mailingListIndexer.deleteIndex(mailingList);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
