package com.dina.feedback.repository;

import com.dina.feedback.DTO.*;
import com.dina.feedback.model.FactFeedback;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackService {
    List<FactFeedback> filterFeedbacks(FeedbackFilter filter);
    void insertFeedback(List<FeedbackDTO> dtos);
    List<FactFeedback> getAll();
    List<FactFeedback> filterByDateRange(LocalDate from, LocalDate to);
    void updateUserTweetCount(String userId, int newTweetCount);
}
