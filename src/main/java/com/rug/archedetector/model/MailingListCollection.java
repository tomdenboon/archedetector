package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mailing_list_collection")
public class MailingListCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany()
    @JoinTable(name = "mailing_list_collection_mailing_list",
            joinColumns = @JoinColumn(name = "mailing_list_collection_id"),
            inverseJoinColumns = @JoinColumn(name = "mailing_list_id")
    )
    private Set<MailingList> mailingLists;

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
}