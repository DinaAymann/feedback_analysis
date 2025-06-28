package com.dina.feedback.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    // Optional: use JobBuilderFactory and StepBuilderFactory beans here
}
