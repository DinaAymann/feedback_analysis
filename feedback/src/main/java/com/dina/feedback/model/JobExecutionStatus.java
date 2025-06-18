package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Job Status Entity
@Entity
@Table(name = "job_execution_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecutionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_execution_id", unique = true)
    private Long jobExecutionId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "batch_status")
    private String batchStatus;

    @Column(name = "exit_status")
    private String exitStatus;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "records_processed")
    private Long recordsProcessed;

    @Column(name = "records_failed")
    private Long recordsFailed;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
