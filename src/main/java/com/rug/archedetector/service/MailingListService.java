package com.rug.archedetector.service;

import com.rug.archedetector.dao.MailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Mail;
import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.util.ApacheMailingListParser;
import com.rug.archedetector.util.MboxParser;
import org.apache.james.mime4j.MimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MailingListService {
    @Autowired
    private final MailingListRepository mailingListRepository;

    @Autowired
    private MailRepository mailRepository;

    private final ApacheMailingListParser apacheMailingListParser = new ApacheMailingListParser();

    public MailingListService(MailingListRepository mailingListRepository) {
        this.mailingListRepository = mailingListRepository;
    }

    public List<MailingList> getAll(){
        return mailingListRepository.findAll();
    }

    public MailingList addFromApacheArchive(MailingList mailinglist){
        mailingListRepository.save(mailinglist);
        mailRepository.saveAll(apacheMailingListParser.getMailFromMailingList(mailinglist));
        return mailinglist;
    }

   public ResponseEntity<?> delete(Long id){
        return mailingListRepository.findById(id).map(mailingList -> {
            mailingListRepository.delete(mailingList);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
   }
}
