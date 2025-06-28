//package com.dina.feedback.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/batch")
//public class BatchJobController {
//
//    private final JobLauncher jobLauncher;
//    private final Job feedbackJob;
//
//    @Value("${batch.input-file}")
//    private String inputFile;
//
//    @GetMapping("/start")
//    public ResponseEntity<String> startJob() throws Exception {
//        JobParameters params = new JobParametersBuilder()
//                .addString("startAt", String.valueOf(System.currentTimeMillis()))
//                .toJobParameters();
//
//        jobLauncher.run(feedbackJob, params);
//        return ResponseEntity.ok("✅ Batch job triggered");
//    }
//}
//
