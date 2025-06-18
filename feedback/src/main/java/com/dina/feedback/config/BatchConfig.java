package com.dina.feedback.config;

import com.dina.feedback.model.Feedback;
import com.dina.feedback.repository.FeedbackRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.JobBuilderHelper;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.StepBuilderHelper;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import javax.validation.ValidationException;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Value;



import java.io.File;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FeedbackRepository feedbackRepository;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       FeedbackRepository feedbackRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.feedbackRepository = feedbackRepository;
    }

    @Bean
    public Job importFeedbackJob(Step importFeedbackStep) {
        return new JobBuilder("importFeedbackJob", jobRepository)
                .start(importFeedbackStep)
                .incrementer(new RunIdIncrementer())
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
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
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
                File file = new File("./uploads/" + inputFile);
                ObjectMapper mapper = new ObjectMapper();
                feedbackList = mapper.readValue(file, new TypeReference<List<Feedback>>() {});
            }

            @Override
            protected Feedback doRead() {
                if (currentIndex < feedbackList.size()) {
                    return feedbackList.get(currentIndex++);
                }
                return null;
            }

            @Override
            protected void doClose() {}
        };
    }


    @Bean
    public ItemProcessor<Feedback, Feedback> feedbackItemProcessor() {
        return item -> {
            // Validate rating between 1–5
            if (item.getRating() < 1 || item.getRating() > 5) {
                throw new ValidationException("Invalid rating");
            }
            return item;
        };
    }

    @Bean
    public ItemWriter<Feedback> feedbackItemWriter() {
        return items -> feedbackRepository.saveAll(items);
    }
}
