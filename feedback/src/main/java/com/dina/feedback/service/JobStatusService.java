
package com.dina.feedback.service;

import com.dina.feedback.model.JobExecutionStatus;
import com.dina.feedback.repository.JobExecutionStatusRepository;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class JobStatusService {

    private final JobExecutionStatusRepository jobStatusRepository;
    private final JobExplorer jobExplorer;

    public JobStatusService(JobExecutionStatusRepository jobStatusRepository, JobExplorer jobExplorer) {
        this.jobStatusRepository = jobStatusRepository;
        this.jobExplorer = jobExplorer;
    }

    public List<JobExecutionStatus> getRecentJobs(LocalDateTime fromDate) {
        return jobStatusRepository.findRecentJobs(fromDate);
    }

    public Optional<JobExecutionStatus> getJobStatus(Long jobExecutionId) {
        return jobStatusRepository.findByJobExecutionId(jobExecutionId);
    }

    public List<JobExecutionStatus> getRunningJobs() {
        return jobStatusRepository.findRunningJobs();
    }

    public Map<String, Object> getJobSummary(LocalDateTime fromDate) {
        List<JobExecutionStatus> jobs = getRecentJobs(fromDate);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalJobs", jobs.size());
        summary.put("completedJobs", jobs.stream().filter(j -> "COMPLETED".equals(j.getBatchStatus())).count());
        summary.put("failedJobs", jobs.stream().filter(j -> "FAILED".equals(j.getBatchStatus())).count());
        summary.put("runningJobs", jobs.stream().filter(j -> "RUNNING".equals(j.getBatchStatus())).count());
        summary.put("totalRecordsProcessed", jobs.stream().mapToLong(j -> j.getRecordsProcessed() != null ? j.getRecordsProcessed() : 0).sum());
        summary.put("totalRecordsFailed", jobs.stream().mapToLong(j -> j.getRecordsFailed() != null ? j.getRecordsFailed() : 0).sum());

        return summary;
    }

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        List<JobExecutionStatus> runningJobs = getRunningJobs();
        List<JobExecutionStatus> recentJobs = getRecentJobs(LocalDateTime.now().minusHours(1));

        health.put("status", runningJobs.isEmpty() ? "HEALTHY" : "PROCESSING");
        health.put("runningJobsCount", runningJobs.size());
        health.put("recentJobsCount", recentJobs.size());
        health.put("timestamp", LocalDateTime.now());

        // Check for stuck jobs (running for more than 1 hour)
        long stuckJobs = runningJobs.stream()
                .filter(job -> job.getStartTime().isBefore(LocalDateTime.now().minusHours(1)))
                .count();

        if (stuckJobs > 0) {
            health.put("status", "WARNING");
            health.put("stuckJobsCount", stuckJobs);
        }

        return health;
    }

    public void updateJobStatus(JobExecution jobExecution) {
        JobExecutionStatus status = JobExecutionStatus.builder()
                .jobExecutionId(jobExecution.getId())
                .jobName(jobExecution.getJobInstance().getJobName())
                .batchStatus(jobExecution.getStatus().toString())
                .exitStatus(jobExecution.getExitStatus().getExitCode())
                .startTime(jobExecution.getStartTime())  // Use directly
                .endTime(jobExecution.getEndTime())      // Use directly
                .fileName(jobExecution.getJobParameters().getString("inputFile"))
                .recordsProcessed(0L) // Will be updated by step listener
                .recordsFailed(0L)   // Will be updated by step listener
                .build();

        jobStatusRepository.save(status);
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date != null ? date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime() : null;
    }
}