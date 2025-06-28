package com.dina.feedback.service;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.batch.FeedbackItemProcessor;
import com.dina.feedback.batch.FeedbackItemWriter;
import com.dina.feedback.batch.FeedbackJsonItemReader;
import com.dina.feedback.model.FileRecord;
import com.dina.feedback.repository.FileRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileRecordRepository fileRecordRepository;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FeedbackServiceImpl feedbackService;

    public void processUploadedFile(File file) throws Exception {
        String filename = file.getName();

        Optional<FileRecord> optionalRecord = fileRecordRepository.findByFilename(filename);
        FileRecord record = optionalRecord.orElseGet(() -> {
            FileRecord newRecord = new FileRecord();
            newRecord.setFilename(filename);
            newRecord.setProcessed(false);
            return fileRecordRepository.save(newRecord);
        });

        if (record.isProcessed()) {
            System.out.println("⏩ File already processed, skipping: " + filename);
            return;
        }

        record.setProcessed(false);
        fileRecordRepository.save(record);

        FeedbackJsonItemReader reader = new FeedbackJsonItemReader(new FileSystemResource(file));

        Step step = new StepBuilder("step-" + UUID.randomUUID(), jobRepository)
                .<FeedbackDTO, FeedbackDTO>chunk(100, transactionManager)
                .reader(reader)
                .processor(new FeedbackItemProcessor())
                .writer(new FeedbackItemWriter(feedbackService))
                .build();

        Job job = new JobBuilder("job-" + UUID.randomUUID(), jobRepository)
                .start(step)
                .build();

        JobParameters params = new JobParametersBuilder()
                .addString("input.file", file.getAbsolutePath())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, params);

        if (execution.getStatus() == BatchStatus.COMPLETED) {
            record.setProcessed(true);
            fileRecordRepository.save(record);
            Files.delete(file.toPath());
            System.out.println("✅ Successfully processed and deleted: " + filename);
        } else {
            System.out.println("❌ Failed to process file: " + filename);
        }
    }
}
