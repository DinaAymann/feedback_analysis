package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fact_feedback")
@Data
public class FactFeedback {

    @Id
    private Long tweetId;

    private String platform;

    @Column(columnDefinition = "TEXT")
    private String text;

    private String language;

    private Integer retweetCount;
    private Integer replyCount;
    private Integer likeCount;
    private Integer quoteCount;
    private Integer bookmarkCount;
    private Integer impressionCount;

    // Computed in queries: likes + retweets + replies
    @Transient
    private Integer totalInteraction;

    @ManyToOne
    @JoinColumn(name = "date_key")
    private DimDate date;

    @ManyToOne
    @JoinColumn(name = "user_key")
    private DimUser user;

    @ManyToOne
    @JoinColumn(name = "issue_key")
    private DimIssue issue;

    @ManyToOne
    @JoinColumn(name = "location_key")
    private DimLocation location;

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FactMention> mentions = new HashSet<>();
    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DimHashtag> hashtags = new HashSet<>();

    // Getters & Setters omitted for brevity
}

