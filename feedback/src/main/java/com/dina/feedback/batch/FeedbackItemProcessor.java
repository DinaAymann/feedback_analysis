package com.dina.feedback.batch;

import com.dina.feedback.DTO.FeedbackDTO;
import org.springframework.batch.item.ItemProcessor;

public class FeedbackItemProcessor implements ItemProcessor<FeedbackDTO, FeedbackDTO> {
    @Override
    public FeedbackDTO process(FeedbackDTO item) {
        // Optional: validate, normalize, log, etc.
        return item;
    }
}
