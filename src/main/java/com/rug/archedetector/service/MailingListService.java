package com.rug.archedetector.service;

import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.EmailThreadRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.lucene.MailingListIndexer;
import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.EmailThread;
import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.model.QueryCollection;
import com.rug.archedetector.util.ApacheMailingListParser;
import com.rug.archedetector.util.EmailFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class MailingListService {
    @Autowired
    private MailingListRepository mailingListRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailThreadRepository emailThreadRepository;

    private final MailingListIndexer mailingListIndexer = new MailingListIndexer();

    private final ApacheMailingListParser apacheMailingListParser = new ApacheMailingListParser();

    public List<MailingList> getAll() {
        return mailingListRepository.findAll();
    }

    public MailingList addFromApacheArchiveWithFilters(MailingList mailinglist, String[] filters) {
        mailinglist = mailingListRepository.save(mailinglist);
        List<Email> emails = apacheMailingListParser.getMailFromMailingList(mailinglist);
        EmailFilter filter = new EmailFilter();
        emails = filter.filterMail(emails, Arrays.asList(filters));
        Set<Email> uniqueEmails = new HashSet<>(emails);
        Map<String, Integer> messageIdThreadId = new HashMap<>();
        Map<Integer, List<Email>> ThreadIdListEmail = new HashMap<>();
        int cnt = 0;
        for (Email email : uniqueEmails) {
            List<Email> single = new ArrayList<>();
            single.add(email);
            ThreadIdListEmail.put(cnt, single);
            messageIdThreadId.put(email.getMessageId(), cnt);
            cnt++;
        }

        System.out.println(ThreadIdListEmail.size());
        for (Email email : uniqueEmails) {
            if (messageIdThreadId.containsKey(email.getInReplyTo())){
                int threadId1 = messageIdThreadId.get(email.getMessageId());
                int threadId2 = messageIdThreadId.get(email.getInReplyTo());
                List<Email> emails1 = ThreadIdListEmail.get(threadId1);
                ThreadIdListEmail.get(threadId2).addAll(emails1);
                for(Email e : emails1){
                    messageIdThreadId.put(e.getMessageId(), threadId2);
                }
                ThreadIdListEmail.remove(threadId1);
            }
        }
        System.out.println(ThreadIdListEmail.size());

        List<Email> resultEmails = new ArrayList<>();
        List<EmailThread> threads = new ArrayList<>();
        for(Map.Entry<Integer, List<Email>> entry : ThreadIdListEmail.entrySet()) {
            EmailThread emailThread = new EmailThread();
            emailThread.setMailingList(mailinglist);
            ZonedDateTime date = null;
            ZonedDateTime first = null;
            String firstSubject = "";
            for(Email email : entry.getValue()){
                email.setEmailThread(emailThread);
                if(date == null || email.getDate().isAfter(date)){
                    date = email.getDate();
                }
                if(first == null || email.getDate().isBefore(date)){
                    first = email.getDate();
                    firstSubject = email.getSubject();
                }
                resultEmails.add(email);
            }
            emailThread.setDate(date);
            emailThread.setSubject(firstSubject);
            emailThread.setSize(entry.getValue().size());
            threads.add(emailThread);
        }

        emailThreadRepository.saveAll(threads);
        emailRepository.saveAll(resultEmails);
        mailingListIndexer.index(mailinglist, threads, resultEmails);
        return mailinglist;
    }

    public ResponseEntity<?> delete(Long id) {
        return mailingListRepository.findById(id).map(mailingList -> {
            mailingList.prepareForDelete();
            List<Email> emails = emailRepository.findByMailingListId(id);
            List<EmailThread> threads = emailThreadRepository.findByMailingListId(id);
            emailRepository.deleteAll(emails);
            emailThreadRepository.deleteAll(threads);
            mailingListRepository.delete(mailingList);
            mailingListIndexer.deleteIndex(mailingList);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }
}
