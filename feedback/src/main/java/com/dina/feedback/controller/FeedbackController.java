package com.dina.feedback.controller;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.DTO.FeedbackFilter;
import com.dina.feedback.model.FactFeedback;
import com.dina.feedback.service.FeedbackServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackServiceImpl feedbackService;

//    // 🔁 1. Internal endpoint to bulk insert feedbacks
//    @PostMapping("/internal/bulk-insert")
//    public ResponseEntity<String> bulkInsert(@RequestBody List<FeedbackDTO> feedbackList) {
//        feedbackService.insertFeedback(feedbackList);
//        return ResponseEntity.ok("✅ Feedbacks processed successfully.");
//    }

    // 🔍 2. Public endpoint to filter feedbacks by all criteria
    @GetMapping("/filter")
    public ResponseEntity<List<FactFeedback>> filterFeedbacks(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String issueCode,
            @RequestParam(required = false) String agencyMention,
            @RequestParam(required = false) String hashtag
    ) {
        FeedbackFilter filter = new FeedbackFilter();
        filter.setUserId(userId);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setLanguage(language);
        filter.setLocation(location);
        filter.setIssueCode(issueCode);
        filter.setAgencyMention(agencyMention);
        filter.setHashtag(hashtag);

        List<FactFeedback> result = feedbackService.filterFeedbacks(filter);
        return ResponseEntity.ok(result);
    }
}
