//package com.dina.feedback.batch;
//
//import com.dina.feedback.model.FileRecord;
//import com.dina.feedback.repository.FileRecordRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class MultiFileBatchRunner implements ApplicationRunner {
//
//    private final FileRecordRepository fileRecordRepository;
//    private final JobLauncher jobLauncher;
//    private final Job feedbackJob;
//
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//
//    @Override
//    public void run(ApplicationArguments args) {
//        File folder = new File(uploadDir);
//        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
//
//        if (files == null || files.length == 0) {
//            System.out.println("✅ No files to process.");
//            return;
//        }
//
//        for (File file : files) {
//            String filename = file.getName();
//
//            Optional<FileRecord> optionalRecord = fileRecordRepository.findByFilename(filename);
//            FileRecord record = optionalRecord.orElseGet(() -> {
//                FileRecord newRecord = new FileRecord();
//                newRecord.setFilename(filename);
//                newRecord.setProcessed(false);
//                return fileRecordRepository.save(newRecord);
//            });
//
//            if (record.isProcessed()) {
//                System.out.println("⏩ File already processed, skipping: " + filename);
//                continue;
//            }
//
//            try {
//                JobParameters params = new JobParametersBuilder()
//                        .addString("input.file", file.getAbsolutePath())
//                        .addLong("timestamp", System.currentTimeMillis()) // Ensures job uniqueness
//                        .toJobParameters();
//
//                JobExecution execution = jobLauncher.run(feedbackJob, params);
//
//                if (execution.getStatus() == BatchStatus.COMPLETED) {
//                    record.setProcessed(true);
//                    fileRecordRepository.save(record);
//                    Files.delete(file.toPath());
//                    System.out.println("✅ Successfully processed and deleted: " + filename);
//                } else {
//                    System.out.println("❌ Failed to process file: " + filename);
//                }
//
//            } catch (Exception e) {
//                System.out.println("❌ Exception occurred during processing: " + filename);
//                e.printStackTrace();
//            }
//        }
//    }
//}

package com.dina.feedback.batch;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.model.FileRecord;
import com.dina.feedback.repository.FileRecordRepository;
import com.dina.feedback.service.FeedbackServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MultiFileBatchRunner implements ApplicationRunner {

    private final FileRecordRepository fileRecordRepository;
    private final JobLauncher jobLauncher;
    private final FeedbackServiceImpl feedbackService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void run(ApplicationArguments args) {
        File folder = new File(uploadDir);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.out.println("✅ No files to process.");
            return;
        }

        for (File file : files) {
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
                continue;
            }

            try {
                // Mark as processing
                record.setProcessed(false);
                fileRecordRepository.save(record);

                // Build dynamic job
                Job job = buildJobForFile(file.getAbsolutePath());

                // Run job
                JobParameters params = new JobParametersBuilder()
                        .addString("input.file", file.getAbsolutePath())
                        .addLong("timestamp", System.currentTimeMillis()) // ensures uniqueness
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

            } catch (Exception e) {
                System.out.println("❌ Exception occurred during processing: " + filename);
                e.printStackTrace();
            }
        }
    }

    private Job buildJobForFile(String path) throws Exception {
        FeedbackJsonItemReader reader = new FeedbackJsonItemReader(new FileSystemResource(path));

        Step step = new StepBuilder("step-" + UUID.randomUUID(), jobRepository)
                .<FeedbackDTO, FeedbackDTO>chunk(100, transactionManager)
                .reader(reader)
                .processor(new FeedbackItemProcessor())
                .writer(new FeedbackItemWriter(feedbackService))
                .build();

        return new JobBuilder("job-" + UUID.randomUUID(), jobRepository)
                .start(step)
                .build();
    }
}

