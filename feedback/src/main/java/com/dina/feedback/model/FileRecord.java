package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "file_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String filename;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(name = "uploaded_at", nullable = false)
    private Timestamp uploadedAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "processing_started_at")
    private Timestamp processingStartedAt;

    @Column(name = "processing_completed_at")
    private Timestamp processingCompletedAt;

    @Column(name = "records_processed")
    private Long recordsProcessed = 0L;

    @Column(name = "records_failed")
    private Long recordsFailed = 0L;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}