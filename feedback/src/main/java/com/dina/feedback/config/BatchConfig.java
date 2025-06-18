package com.dina.feedback.config;

import com.dina.feedback.model.Feedback;
import com.dina.feedback.repository.*;
import com.dina.feedback.service.DimensionLookupService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FeedbackRepository feedbackRepository;
    private final DimensionLookupService dimensionLookupService;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       FeedbackRepository feedbackRepository,
                       DimensionLookupService dimensionLookupService) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.feedbackRepository = feedbackRepository;
        this.dimensionLookupService = dimensionLookupService;
    }

    @Bean
    public Job importFeedbackJob(Step importFeedbackStep) {
        return new JobBuilder("importFeedbackJob", jobRepository)
                .start(importFeedbackStep)
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener())
                .build();
    }

    @Bean
    public Step importFeedbackStep(ItemReader<Feedback> jsonFeedbackReader) {
        return new StepBuilder("importFeedbackStep", jobRepository)
                .<Feedback, Feedback>chunk(100, transactionManager)
                .reader(jsonFeedbackReader)
                .processor(feedbackItemProcessor())
                .writer(feedbackItemWriter())
                .faultTolerant()
                .skipPolicy(customSkipPolicy())
                .skipLimit(1000) // Allow up to 1000 failed records
                .listener(stepExecutionListener())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Feedback> jsonFeedbackReader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        return new AbstractItemCountingItemStreamItemReader<>() {

            private List<Feedback> feedbackList;
            private int currentIndex = 0;

            @Override
            protected void doOpen() throws Exception {
                try {
                    File file = new File("./uploads/" + inputFile);
                    if (!file.exists()) {
                        throw new ItemStreamException("File not found: " + file.getAbsolutePath());
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());

                    feedbackList = mapper.readValue(file, new TypeReference<List<Feedback>>() {});
                    log.info("Successfully loaded {} feedback records from file: {}", feedbackList.size(), inputFile);
                } catch (Exception e) {
                    log.error("Error reading file: {}", inputFile, e);
                    throw new ItemStreamException("Failed to read file: " + inputFile, e);
                }
            }

            @Override
            protected Feedback doRead() {
                if (feedbackList != null && currentIndex < feedbackList.size()) {
                    Feedback feedback = feedbackList.get(currentIndex++);
                    log.debug("Reading feedback record {}/{}", currentIndex, feedbackList.size());
                    return feedback;
                }
                return null;
            }

            @Override
            protected void doClose() {
                feedbackList = null;
                currentIndex = 0;
            }
        };
    }

    @Bean
    public ItemProcessor<Feedback, Feedback> feedbackItemProcessor() {
        return item -> {
            try {
                // Validate rating
                if (item.getRating() == null || item.getRating() < 1 || item.getRating() > 5) {
                    throw new ValidationException("Invalid rating: " + item.getRating());
                }

                // Validate required fields
                if (item.getUserId() == null) {
                    throw new ValidationException("UserId is required");
                }
                if (item.getAgencyId() == null) {
                    throw new ValidationException("AgencyId is required");
                }
                if (item.getLocationId() == null) {
                    throw new ValidationException("LocationId is required");
                }

                // Map dimension IDs to keys
                Long userKey = dimensionLookupService.getUserKey(item.getUserId());
                Long agencyKey = dimensionLookupService.getAgencyKey(item.getAgencyId());
                Long locationKey = dimensionLookupService.getLocationKey(item.getLocationId());
                Long languageKey = dimensionLookupService.getLanguageKey(
                        item.getLanguageCode() != null ? item.getLanguageCode() : "en"
                );

                // Build the fact record
                Feedback processedFeedback = Feedback.builder()
                        .userKey(userKey)
                        .agencyKey(agencyKey)
                        .locationKey(locationKey)
                        .languageKey(languageKey)
                        .rating(item.getRating())
                        .comment(item.getComment())
                        .feedbackTimestamp(item.getFeedbackDate() != null ?
                                item.getFeedbackDate().atStartOfDay() : LocalDateTime.now())
                        .processingBatchId(getCurrentJobExecutionId())
                        .build();

                log.debug("Processed feedback: userId={}, rating={}", item.getUserId(), item.getRating());
                return processedFeedback;

            } catch (Exception e) {
                log.error("Error processing feedback record: userId={}, error={}", item.getUserId(), e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public ItemWriter<Feedback> feedbackItemWriter() {
        return items -> {
            try {
                List<Feedback> itemList = (List<Feedback>) items;
                feedbackRepository.saveAll(itemList);
                log.info("Successfully saved {} feedback records", itemList.size());
            } catch (Exception e) {
                log.error("Error saving feedback records", e);
                throw e;
            }
        };
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("Starting job: {} with parameters: {}",
                        jobExecution.getJobInstance().getJobName(),
                        jobExecution.getJobParameters());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                BatchStatus status = jobExecution.getStatus();
                log.info("Job completed with status: {}", status);

                if (status == BatchStatus.COMPLETED) {
                    log.info("Job completed successfully!");
                } else if (status == BatchStatus.FAILED) {
                    log.error("Job failed! Exit description: {}", jobExecution.getExitStatus().getExitDescription());
                }
            }
        };
    }

    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("Starting step: {}", stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("Step completed: {} - Read: {}, Written: {}, Skipped: {}",
                        stepExecution.getStepName(),
                        stepExecution.getReadCount(),
                        stepExecution.getWriteCount(),
                        stepExecution.getSkipCount());
                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    public SkipPolicy customSkipPolicy() {
        return new SkipPolicy() {
            @Override
            public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException