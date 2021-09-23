package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.EmailThreadRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.dao.QueryCollectionRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailThreadService {
    private final EmailThreadRepository emailThreadRepository;
    private final QueryCollectionRepository queryCollectionRepository;

    @Transactional(readOnly = true)
    public Page<EmailThread> getThreadsByMailingListId(Long id, Pageable pageable) {
        return emailThreadRepository.findByMailingListId(id, pageable);
    }

    @Transactional
    public EmailThread saveThread(EmailThread emailThread) {
        return emailThreadRepository.save(emailThread);
    }

    @Transactional(readOnly = true)
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
