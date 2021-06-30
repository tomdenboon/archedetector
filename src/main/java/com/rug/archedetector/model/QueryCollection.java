package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "query_collection")
public class QueryCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(targetEntity = MailingList.class)
    @JoinTable(name = "query_collection_mailing_list",
            joinColumns = @JoinColumn(name = "query_collection_id"),
            inverseJoinColumns = @JoinColumn(name = "mailing_list_id")
    )
    private Set<MailingList> mailingLists;

    @ManyToMany(targetEntity = IssueList.class)
    @JoinTable(name = "query_collection_issue_list",
            joinColumns = @JoinColumn(name = "query_collection_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_list_id")
    )
    private Set<IssueList> issueLists;

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

    public Set<MailingList> getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(Set<MailingList> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public Set<IssueList> getIssueLists() {
        return issueLists;
    }

    public void setIssueLists(Set<IssueList> issueLists) {
        this.issueLists = issueLists;
    }
}