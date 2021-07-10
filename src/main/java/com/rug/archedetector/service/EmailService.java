package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.dao.QueryCollectionRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.model.MailingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailService {
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private QueryCollectionRepository queryCollectionRepository;

    public Page<Email> getAllMailByMailingListId(Long id, Pageable pageable) {
        return emailRepository.findByMailingListId(id, pageable);
    }

    public List<Email> getMailByEmailThreadId(Long id, Sort sort){
        return emailRepository.findByEmailThreadId(id, sort);
    }
    public Page<Email> getAllMailByMailingListIds(Long[] mailingListIds, Pageable pageable) {
        return emailRepository.findByMailingListIdIn(Arrays.stream(mailingListIds).toList(), pageable);
    }

    public Email saveMail(Email email) {
        return emailRepository.save(email);
    }

    public Page<Email> getMailByQueryCollectionId(Long queryCollectionId, Pageable pageable) {
        return queryCollectionRepository.findById(queryCollectionId).map(queryCollection -> {
            List<Long> mailingIds = new ArrayList<>();
            for(MailingList mailingList : queryCollection.getMailingLists()){
                mailingIds.add(mailingList.getId());
            }
            return emailRepository.findByMailingListIdIn(mailingIds, pageable);
        }).orElseThrow();
    }
}
