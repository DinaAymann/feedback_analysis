package com.dina.feedback.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedbackFilter {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String language;
    private String userId;
    private String location;
    private String issueCode;
    private String agencyMention;
    private String hashtag;
}
