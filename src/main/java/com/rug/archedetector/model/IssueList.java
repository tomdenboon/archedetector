package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "issue_list")
public class IssueList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "issue_list_key", nullable = false)
    private String key;

    @JsonIgnore
    @ManyToMany(mappedBy = "issueLists")
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
            queryCollection.getIssueLists().remove(this);
            iterator.remove();
        }
    }
}
