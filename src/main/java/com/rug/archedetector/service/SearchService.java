package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.lucene.MailingListSearcher;
import com.rug.archedetector.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Email> searchKeyword(String query, Long mailingListId){
        List<Long> emailIds = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        try{
            emailIds = mailingListSearcher.keyWordSearch(query, mailingListId);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Long id : emailIds){
            Email m = emailRepository.findByIdAndMailingListId(id, mailingListId).orElseThrow();
            emails.add(m);
        }
        return emails;
    }
}
