package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "issue_list")
@Getter
public class IssueList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    @Setter
    private String name;

    @Column(name = "issue_list_key", nullable = false)
    @Setter
    private String key;

    @JsonIgnore
    @ManyToMany(mappedBy = "issueLists")
    @Setter
    private Set<QueryCollection> queryCollections = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "issueList", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Issue> issues;

    public void prepareForDelete(){
        for (Iterator<QueryCollection> iterator = queryCollections.iterator(); iterator.hasNext();) {
            QueryCollection queryCollection = iterator.next();
            queryCollection.getIssueLists().remove(this);
            iterator.remove();
        }
    }
}
