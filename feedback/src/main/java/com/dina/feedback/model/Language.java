package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Language Dimension
@Entity
@Table(name = "dim_language")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Long languageId;

    @Column(name = "language_code", unique = true, nullable = false)
    private String languageCode;

    @Column(name = "language_name", nullable = false)
    private String languageName;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
