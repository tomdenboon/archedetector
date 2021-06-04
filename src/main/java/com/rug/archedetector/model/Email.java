package com.rug.archedetector.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "email")
public class Email {
    private long id;
    private MailingList mailingList;
    private String messageId;
    private String inReplyTo;
    private String sentFrom;
    private String subject;
    private ZonedDateTime date;
    private String body;
    private String raw;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MAIL_SEQ")
    @SequenceGenerator(name = "MAIL_SEQ", sequenceName = "MAIL_SEQ", allocationSize = 100)
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = MailingList.class)
    @JoinColumn(name = "mailing_list_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    @Basic
    @Column(name = "message_id", nullable = false, length = 255)
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "in_reply_to", nullable = true, length = 255)
    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    @Basic
    @Column(name = "sent_from", nullable = true, length = 255)
    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }

    @Basic
    @Column(name = "subject", columnDefinition = "TEXT")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Basic
    @Column(name = "body", columnDefinition = "TEXT")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Basic
    @Column(name = "raw", columnDefinition = "TEXT")
    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    @Column(name = "date")
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
