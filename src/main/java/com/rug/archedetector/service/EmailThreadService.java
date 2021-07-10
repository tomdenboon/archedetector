package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.EmailThreadRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.dao.QueryCollectionRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailThreadService {
    @Autowired
    private EmailThreadRepository emailThreadRepository;

    @Autowired
    private QueryCollectionRepository queryCollectionRepository;

    public Page<EmailThread> getThreadsByMailingListId(Long id, Pageable pageable) {
        return emailThreadRepository.findByMailingListId(id, pageable);
    }

    public EmailThread saveMail(EmailThread emailThread) {
        return emailThreadRepository.save(emailThread);
    }

    public Page<EmailThread> getThreadsByQueryCollectionId(Long queryCollectionId, Pageable pageable) {
        return queryCollectionRepository.findById(queryCollectionId).map(queryCollection -> {
            List<Long> mailingListIds = new ArrayList<>();
            for(MailingList mailingList : queryCollection.getMailingLists()){
                mailingListIds.add(mailingList.getId());
            }
            return emailThreadRepository.findByMailingListIdIn(mailingListIds, pageable);
        }).orElseThrow();
    }
}
