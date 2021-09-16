package com.rug.archedetector.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "email_thread")
public class EmailThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(targetEntity = MailingList.class)
    @JoinColumn(name = "mailing_list_id", nullable = false)
    private MailingList mailingList;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "subject")
    @Lob
    private String subject;

    @Column(name = "size")
    private int size;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(name = "email_thread_tag",
            joinColumns = @JoinColumn(name = "email_thread_id"),
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

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
