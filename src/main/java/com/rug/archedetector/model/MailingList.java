package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mailing_list")
public class MailingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url", nullable = false)
    private String url;

    @JsonIgnore
    @ManyToMany(mappedBy = "mailingLists")
    private Set<MailingListCollection> mailingListCollections = new HashSet<>();

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<MailingListCollection> getMailingListCollections() {
        return mailingListCollections;
    }

    public void setMailingListCollections(Set<MailingListCollection> mailingListCollections) {
        this.mailingListCollections = mailingListCollections;
    }
}