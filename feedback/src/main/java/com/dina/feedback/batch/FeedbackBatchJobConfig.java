package com.dina.feedback.batch;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.service.FeedbackServiceImpl;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

@Configuration
public class FeedbackBatchJobConfig {

//    @Bean
//    public Job feedbackJob(JobRepository jobRepository, Step step1) {
//        return new JobBuilder("feedbackJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .flow(step1)
//                .end()
//                .build();
//    }
//
//    @Bean
//    public Step step1(JobRepository jobRepository,
//                      PlatformTransactionManager transactionManager,
//                      FeedbackServiceImpl feedbackService,
//                      @Value("${batch.input-file}") String inputFile) throws Exception {
//
//        FeedbackJsonItemReader reader = new FeedbackJsonItemReader(new FileSystemResource(inputFile));
//
//        return new StepBuilder("step1", jobRepository)
//                .<FeedbackDTO, FeedbackDTO>chunk(100, transactionManager)
//                .reader(reader)
//                .processor(new FeedbackItemProcessor())
//                .writer(new FeedbackItemWriter(feedbackService))
//                .build();
//    }

//    @Bean
//    public ApplicationRunner runJobOnStartup(JobLauncher jobLauncher, Job feedbackJob) {
//        return args -> {
//            JobParameters params = new JobParametersBuilder()
//                    .addString("startAt", String.valueOf(System.currentTimeMillis()))
//                    .toJobParameters();
//            jobLauncher.run(feedbackJob, params);
//        };
//    }
}
