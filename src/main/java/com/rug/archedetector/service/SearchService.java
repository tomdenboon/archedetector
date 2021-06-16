package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.lucene.MailingListSearcher;
import com.rug.archedetector.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    private final MailingListRepository mailingListRepository;

    @Autowired
    private EmailRepository emailRepository;

    private final MailingListSearcher mailingListSearcher = new MailingListSearcher();


    public SearchService(MailingListRepository mailingListRepository) {
        this.mailingListRepository = mailingListRepository;
    }

    public Page<Email> searchKeyword(String query, Long mailingListId, Pageable pageable){
        List<Long> emailIds = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        try{
            emailIds = mailingListSearcher.keyWordSearch(query, mailingListId, pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : emailIds){
            Email m = emailRepository.findByIdAndMailingListId(id, mailingListId).orElseThrow();
            emails.add(m);
        }
        int size = pageable.getPageSize() * (pageable.getPageNumber()+1);
        if(emails.size() < pageable.getPageSize()){
            size = emails.size();
        } else {
            size += 1;
        }
        System.out.println(size);
        return new PageImpl<>(emails, pageable, size);
    }
}
