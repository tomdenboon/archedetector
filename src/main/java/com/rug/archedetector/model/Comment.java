package com.rug.archedetector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "body")
    @Lob
    private String body;

    @Basic
    @Column(name = "author", columnDefinition = "TEXT")
    private String author;

    @Column(name = "date")
    private ZonedDateTime date;

    @JsonIgnore
    @JoinColumn(name = "issue_id", referencedColumnName = "id")
    @ManyToOne(optional=false)
    private Issue issue;

    public Comment(Issue issue, String author, ZonedDateTime date, String body) {
        this.issue = issue;
        this.author = author;
        this.date = date;
        this.body = body;
    }
}
