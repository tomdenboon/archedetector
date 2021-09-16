package com.rug.archedetector.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "issue")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "andrewKey", nullable = false)
    @Lob
    private String key;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "summary", nullable = false)
    @Lob
    private String summary;

    @Column(name = "description")
    @Lob
    private String description;

    @ManyToOne(targetEntity = IssueList.class)
    @JoinColumn(name = "issue_list_id", nullable = false)
    private IssueList issueList;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(name = "issue_tag",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    private int tagCount;

    @PreUpdate
    @PrePersist
    public void setTagCount() {
        this.tagCount = tags.size();
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueList getIssueList() {
        return issueList;
    }

    public void setIssueList(IssueList issueList) {
        this.issueList = issueList;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
