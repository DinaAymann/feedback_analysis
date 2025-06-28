package com.dina.feedback.model;
import jakarta.persistence.*; // If using Jakarta EE (Spring Boot 3+)
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dim_user")
@Data
public class DimUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userKey;

    @Column(unique = true)
    private String userId;

    private String username;
    private LocalDateTime accountCreated;
    private Integer followersCount;
    private Integer followingCount;
    private Integer tweetCount;
    private Integer listedCount;
}

