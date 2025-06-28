package com.dina.feedback;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.DTO.FeedbackFilter;
import com.dina.feedback.model.FactFeedback;
import com.dina.feedback.service.FeedbackServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedbackServiceTestLoader {

    private final FeedbackServiceImpl feedbackService;

    @PostConstruct
    public void testAllFeatures() throws Exception {
        System.out.println("🚀 Starting FeedbackService integration test...");

        // 1️⃣ Load sample input JSON (inline, or from file)
        String json = """
        [
          {
            "platform": "Twitter",
            "tweet_id": "17472093",
            "text": "كيف اقدم على مساعدة",
            "created_at": "2024-07-21T12:04:52",
            "language": "ar",
            "hashtags": ["#طلب_دعم", "#مساعدة"],
            "mentions": ["@Account2"],
            "metrics": {
              "retweet_count": 18,
              "reply_count": 38,
              "like_count": 136,
              "quote_count": 14,
              "bookmark_count": 27,
              "impression_count": 1849
            },
            "issue": {
              "issue_id": 101,
              "issue_class": {
                "issue_class_key": 5,
                "issue_class_code": "طلب دعم"
              }
            },
            "user": {
              "user_id": "99887766",
              "username": "user_00",
              "created_at": "2020-09-14T09:00:00Z",
              "followers_count": 241,
              "following_count": 143,
              "tweet_count": 1,
              "listed_count": 10,
              "location_string": "Cairo, Egypt"
            }
          }
        ]
        """;

        List<FeedbackDTO> dtos = new ObjectMapper().readValue(json, new TypeReference<>() {});
        feedbackService.insertFeedback(dtos);
        System.out.println("✅ Inserted feedback");

        // 2️⃣ Get all feedback
        List<FactFeedback> all = feedbackService.getAll();
        System.out.println("📊 All feedback count: " + all.size());

        // 3️⃣ Filter feedback with full filter
        FeedbackFilter filter = new FeedbackFilter();
        filter.setUserId("99887766");
        filter.setFromDate(LocalDate.of(2024, 1, 1));
        filter.setToDate(LocalDate.of(2024, 12, 31));
        filter.setLanguage("ar");
        filter.setLocation("Cairo, Egypt");
        filter.setIssueCode("طلب دعم");
        filter.setAgencyMention("@Account2");
        filter.setHashtag("#مساعدة");

        List<FactFeedback> filtered = feedbackService.filterFeedbacks(filter);
        System.out.println("🔍 Filtered feedback count: " + filtered.size());

        // 4️⃣ Filter by date range only
        List<FactFeedback> byDate = feedbackService.filterByDateRange(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
        );
        System.out.println("📅 Date range filtered count: " + byDate.size());

        // 5️⃣ Update user tweet count
        System.out.println("✏️ Updating tweet count for user 99887766");
        feedbackService.updateUserTweetCount("99887766", 9001);
        System.out.println("✅ User tweet count updated");

        System.out.println("🎉 Finished FeedbackService integration test.");
    }
}
