package com.dina.feedback.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
public class FeedbackJobLauncher {

    private final JobLauncher jobLauncher;
    private final Job importFeedbackJob;

    public FeedbackJobLauncher(JobLauncher jobLauncher, Job importFeedbackJob) {
        this.jobLauncher = jobLauncher;
        this.importFeedbackJob = importFeedbackJob;
    }

    public void launchJob(String filename) throws Exception {
        var params = new JobParametersBuilder()
                .addString("inputFile", filename) // Changed from "fileName" to "inputFile"
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(importFeedbackJob, params);
    }
}