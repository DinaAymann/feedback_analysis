package com.dina.feedback.model;
import jakarta.persistence.*; // If using Jakarta EE (Spring Boot 3+)
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dim_hashtag")
@Data
public class DimHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @ManyToOne
    @JoinColumn(name = "tweet_id")
    private FactFeedback tweet;

    private String hashtag;
}

