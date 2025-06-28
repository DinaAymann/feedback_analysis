package com.dina.feedback.batch;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.service.FeedbackServiceImpl;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

public class FeedbackItemWriter implements ItemWriter<FeedbackDTO> {

    private final FeedbackServiceImpl feedbackService;

    public FeedbackItemWriter(FeedbackServiceImpl feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Override
    public void write(Chunk<? extends FeedbackDTO> items) {
        List<FeedbackDTO> dtoList = new ArrayList<>(items.getItems()); // ✅ resolves the mismatch
        feedbackService.insertFeedback(dtoList);
    }

}
