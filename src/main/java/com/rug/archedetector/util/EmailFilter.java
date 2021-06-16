package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;

public class EmailFilter {
    public List<Email> filterMail(List<Email> mails, List<String> filters){
        if(filters.contains("jira")){
            mails = jiraFilter(mails);
        }
        if(filters.contains("git")){
            mails = gitFilter(mails);
        }
        return mails;
    }

    private List<Email> jiraFilter(List<Email> mails){
        List<Email> resultMails = new ArrayList<>();
        for(Email mail : mails){
            if(!mail.getSentFrom().contains("jira@apache.org")){
                resultMails.add(mail);
            }
        }
        return resultMails;
    }

    private List<Email> gitFilter(List<Email> mails){
        List<Email> resultMails = new ArrayList<>();
        for(Email mail : mails){
            if(!mail.getSentFrom().contains("git@git.apache.org") && !mail.getSentFrom().contains("git@apache.org")){
                resultMails.add(mail);
            }
        }
        return resultMails;
    }
}
