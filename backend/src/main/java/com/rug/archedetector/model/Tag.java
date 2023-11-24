package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Email> emails = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Issue> issues = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<EmailThread> emailThreads = new HashSet<>();

    public Tag() {
    }

    public Set<EmailThread> getEmailThreads() {
        return emailThreads;
    }

    public void setEmailThreads(Set<EmailThread> emailThreads) {
        this.emailThreads = emailThreads;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public void setIssues(Set<Issue> issues) {
        this.issues = issues;
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void prepareForDelete(){
        for (Iterator<Email> iterator = emails.iterator(); iterator.hasNext();) {
            Email email = iterator.next();
            email.getTags().remove(this);
            iterator.remove();
        }
        for (Iterator<Issue> iterator = issues.iterator(); iterator.hasNext();) {
            Issue issue = iterator.next();
            issue.getTags().remove(this);
            iterator.remove();
        }
        for (Iterator<EmailThread> iterator = emailThreads.iterator(); iterator.hasNext();) {
            EmailThread emailThread = iterator.next();
            emailThread.getTags().remove(this);
            iterator.remove();
        }
    }
}
