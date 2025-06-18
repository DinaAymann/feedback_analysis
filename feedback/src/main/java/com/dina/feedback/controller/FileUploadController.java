package com.dina.feedback.controller;

import com.dina.feedback.service.FileStorageService;
import com.dina.feedback.service.FeedbackJobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private FeedbackJobLauncher feedbackJobLauncher;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (fileStorageService.fileExists(filename)) {
            return ResponseEntity.badRequest().body("❌ File already exists.");
        }
        try {
            fileStorageService.saveFile(file);
            feedbackJobLauncher.launchJob(filename);
            return ResponseEntity.ok("✅ File uploaded successfully.");
        }  catch (Exception e) {
            e.printStackTrace();  // ← Add this to see full stack trace in console
            return ResponseEntity.internalServerError().body("❌ Upload failed: " + e.getMessage());
    }

}
}
