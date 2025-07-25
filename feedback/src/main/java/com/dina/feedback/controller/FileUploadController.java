//
//
package com.dina.feedback.controller;

import com.dina.feedback.model.FileRecord;
import com.dina.feedback.repository.FileRecordRepository;
import com.dina.feedback.service.FileProcessingService;
import com.dina.feedback.service.FileStorageService;
//import com.dina.feedback.service.FeedbackJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/files")
//public class FileUploadController {
//
//    private final FileStorageService fileStorageService;
//    private final FeedbackJobLauncher feedbackJobLauncher;
//    private final FileRecordRepository fileRecordRepository;
//
//    public FileUploadController(FileStorageService fileStorageService,
//                                FeedbackJobLauncher feedbackJobLauncher,
//                                FileRecordRepository fileRecordRepository) {
//        this.fileStorageService = fileStorageService;
//        this.feedbackJobLauncher = feedbackJobLauncher;
//        this.fileRecordRepository = fileRecordRepository;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
//        Map<String, Object> response = new HashMap<>();
//        String filename = file.getOriginalFilename();
//
//        try {
//            // Check if file already exists in storage
//            if (fileStorageService.fileExists(filename)) {
//                response.put("success", false);
//                response.put("message", "❌ File already exists in storage");
//                response.put("filename", filename);
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            // Check if file was already processed
//            if (fileRecordRepository.existsByFilename(filename)) {
//                response.put("success", false);
//                response.put("message", "❌ File already processed");
//                response.put("filename", filename);
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            // Save file to storage
//            fileStorageService.saveFile(file);
//
//            // Create file record
//            FileRecord fileRecord = new FileRecord();
//            fileRecord.setFilename(filename);
//            fileRecord.setProcessed(false);
//            fileRecordRepository.save(fileRecord);
//
//            // Launch batch job
//            feedbackJobLauncher.launchJob(filename);
//
//            response.put("success", true);
//            response.put("message", "✅ File uploaded and processing started");
//            response.put("filename", filename);
//            //response.put("jobExecutionId", jobExecutionId);
//
//            //log.info("File uploaded successfully: {}, Job ID: {}", filename, jobExecutionId);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error uploading file: {}", filename, e);
//            response.put("success", false);
//            response.put("message", "❌ Upload failed: " + e.getMessage());
//            response.put("filename", filename);
//            return ResponseEntity.internalServerError().body(response);
//        }
//    }
//
//    @GetMapping("/status")
//    public ResponseEntity<Map<String, Object>> getFileStatus() {
//        Map<String, Object> status = new HashMap<>();
//
//        long totalFiles = fileRecordRepository.count();
//        long processedFiles = fileRecordRepository.countByProcessedTrue();
//        long pendingFiles = fileRecordRepository.countByProcessedFalse();
//
//        status.put("totalFiles", totalFiles);
//        status.put("processedFiles", processedFiles);
//        status.put("pendingFiles", pendingFiles);
//        status.put("processingRate", totalFiles > 0 ? (double) processedFiles / totalFiles * 100 : 0);
//
//        return ResponseEntity.ok(status);
//    }
//}
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final FileRecordRepository fileRecordRepository;
    private final FileProcessingService fileProcessingService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        String filename = file.getOriginalFilename();

        try {
            if (fileStorageService.fileExists(filename)) {
                response.put("success", false);
                response.put("message", "❌ File already exists in storage");
                response.put("filename", filename);
                return ResponseEntity.badRequest().body(response);
            }

            if (fileRecordRepository.existsByFilename(filename)) {
                response.put("success", false);
                response.put("message", "❌ File already processed");
                response.put("filename", filename);
                return ResponseEntity.badRequest().body(response);
            }

            fileStorageService.saveFile(file);

            FileRecord fileRecord = new FileRecord();
            fileRecord.setFilename(filename);
            fileRecord.setProcessed(false);
            fileRecordRepository.save(fileRecord);

            // ✅ Trigger Spring Batch to process the file
//            try {
//                fileProcessingService.processUploadedFile(fileStorageService.getFilePath(filename).toFile());
//                response.put("success", true);
//                response.put("message", "✅ File uploaded and processing started");
//            } catch (Exception e) {
//                response.put("success", false);
//                response.put("message", "❌ Processing failed: " + e.getMessage());
//                return ResponseEntity.internalServerError().body(response);
//            }
            response.put("success", true);
            response.put("message", "✅ File uploaded successfully");
            response.put("filename", filename);



            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "❌ Failed to save file");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status")
    public List<Map<String, Object>> getStatuses() {
        return fileRecordRepository.findAll().stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("filename", record.getFilename());
                    map.put("processed", record.isProcessed());
                    map.put("uploadedAt", record.getUploadedAt());
                    return map;
                }).collect(Collectors.toList());
    }

}

