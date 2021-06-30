package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "email")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(targetEntity = MailingList.class)
    @JoinColumn(name = "mailing_list_id", nullable = false)
    private MailingList mailingList;

    @Basic
    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Basic
    @Column(name = "in_reply_to")
    private String inReplyTo;

    @Basic
    @Column(name = "sent_from")
    private String sentFrom;

    @Basic
    @Column(name = "subject", columnDefinition = "TEXT")
    private String subject;


    @Column(name = "date")
    private ZonedDateTime date;

    @Basic
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Basic
    @Column(name = "raw", columnDefinition = "TEXT")
    private String raw;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(name = "email_tag",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getEmails().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getEmails().remove(this);
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
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


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }


    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return sentFrom + "\n" + subject;
    }
}
