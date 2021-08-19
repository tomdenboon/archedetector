package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;

import java.util.ArrayList;
import java.util.List;

public class EmailFilter {
    /**
     * This function will filter the emails
     *
     * @param mails a list of email objects
     * @param filters list of strings of the filters the function has to perform
     * @return the filtered email
     */
    public List<Email> filterMail(List<Email> mails, List<String> filters){
        if(filters.contains("jira")){
            mails = jiraFilter(mails);
        }
        if(filters.contains("git")){
            mails = gitFilter(mails);
        }
        return mails;
    }

    /**
     * Filters all the jira emails from the apache mail. We do this by checking
     * who sent the mail.
     *
     * @param mails a list of mail
     * @return returns the non-jira emails
     */
    private List<Email> jiraFilter(List<Email> mails){
        List<Email> resultMails = new ArrayList<>();
        for(Email mail : mails){
            if(!mail.getSentFrom().contains("jira@apache.org")){
                resultMails.add(mail);
            }
        }
        return resultMails;
    }

    /**
     * Filters all the github emails from the apache mail. We do this by checking
     * who sent the mail.
     *
     * @param mails a list of mail
     * @return returns the non-github emails
     */
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
