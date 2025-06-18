package com.dina.feedback.model;


import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String filename;

    private boolean processed = false;

    private Timestamp uploadedAt = new Timestamp(System.currentTimeMillis());

    // Getters & Setters
}
