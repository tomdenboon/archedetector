package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
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
    private Set<QueryCollection> queryCollections = new HashSet<>();

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


    public Set<QueryCollection> getQueryCollections() {
        return queryCollections;
    }

    public void setQueryCollections(Set<QueryCollection> queryCollections) {
        this.queryCollections = queryCollections;
    }

    public void prepareForDelete(){
        for (Iterator<QueryCollection> iterator = queryCollections.iterator(); iterator.hasNext();) {
            QueryCollection queryCollection = iterator.next();
            queryCollection.getMailingLists().remove(this);
            iterator.remove();
        }
    }

}