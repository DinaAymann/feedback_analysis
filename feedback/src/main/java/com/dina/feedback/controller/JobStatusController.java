package com.dina.feedback.controller;

import com.dina.feedback.model.JobExecutionStatus;
import com.dina.feedback.service.JobStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobStatusController {

    private final JobStatusService jobStatusService;

    public JobStatusController(JobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    @GetMapping("/status")
    public ResponseEntity<List<JobExecutionStatus>> getAllJobStatuses(
            @RequestParam(value = "hours", defaultValue = "24") int hours) {

        LocalDateTime fromDate = LocalDateTime.now().minusHours(hours);
        List<JobExecutionStatus> jobs = jobStatusService.getRecentJobs(fromDate);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/status/{jobExecutionId}")
    public ResponseEntity<JobExecutionStatus> getJobStatus(@PathVariable Long jobExecutionId) {
        return jobStatusService.getJobStatus(jobExecutionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/running")
    public ResponseEntity<List<JobExecutionStatus>> getRunningJobs() {
        List<JobExecutionStatus> runningJobs = jobStatusService.getRunningJobs();
        return ResponseEntity.ok(runningJobs);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getJobSummary(
            @RequestParam(value = "hours", defaultValue = "24") int hours) {

        LocalDateTime fromDate = LocalDateTime.now().minusHours(hours);
        Map<String, Object> summary = jobStatusService.getJobSummary(fromDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = jobStatusService.getSystemHealth();
        return ResponseEntity.ok(health);
    }
}

