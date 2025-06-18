package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// User Dimension
@Entity
@Table(name = "dim_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_external_id", unique = true)
    private String userExternalId;

    @Column(name = "user_segment")
    private String userSegment;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "registration_date")
    private java.time.LocalDate registrationDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
