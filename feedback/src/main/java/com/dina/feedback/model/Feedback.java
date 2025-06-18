////package com.dina.feedback.model;
////
////import jakarta.persistence.*;
////import lombok.*;
////
////@Entity
////@Table(name = "fact_feedback")
////@Data
////@NoArgsConstructor
////@AllArgsConstructor
////public class Feedback {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long feedbackId;
////
////    private Long userId;
////    private Integer rating;
////    private String comment;
////
////    @Column(name = "agency_id")
////    private Long agencyId;
////
////    @Column(name = "location_id")
////    private Long locationId;
////
////    private String languageCode;
////
////    private java.time.LocalDate feedbackDate;
////}
//// ===========================
//// ENHANCED FEEDBACK ENTITY
//// ===========================
//package com.dina.feedback.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "fact_feedback")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Getter
//public class Feedback {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "feedback_key")
//    private Long feedbackKey;
//
//    // Foreign key references
//    @Column(name = "agency_key", nullable = false)
//    private Long agencyKey;
//
//    @Column(name = "location_key", nullable = false)
//    private Long locationKey;
//
//    @Column(name = "user_key", nullable = false)
//    private Long userKey;
//
//    @Column(name = "date_key", nullable = false)
//    private Integer dateKey;
//
//    @Column(name = "language_key", nullable = false)
//    private Long languageKey;
//
//    // Measures
//    @Column(name = "rating_score", nullable = false)
//    private Integer rating;
//
//    @Column(name = "comment_text", columnDefinition = "TEXT")
//    private String comment;
//
//    @Column(name = "comment_length")
//    private Integer commentLength;
//
//    @Column(name = "sentiment_score", precision = 3, scale = 2)
//    private Double sentimentScore;
//
//    // Metadata
//    @Column(name = "feedback_timestamp", nullable = false)
//    private LocalDateTime feedbackTimestamp;
//
//    @Column(name = "processing_batch_id")
//    private String processingBatchId;
//
//    @Column(name = "created_date")
//    private LocalDateTime createdDate;
//
//    @PrePersist
//    protected void onCreate() {
//        createdDate = LocalDateTime.now();
//        if (feedbackTimestamp == null) {
//            feedbackTimestamp = LocalDateTime.now();
//        }
//        if (comment != null) {
//            commentLength = comment.length();
//        }
//        // Calculate date key (YYYYMMDD format)
//        if (feedbackTimestamp != null) {
//            dateKey = feedbackTimestamp.getYear() * 10000 +
//                    feedbackTimestamp.getMonthValue() * 100 +
//                    feedbackTimestamp.getDayOfMonth();
//        }
//    }
//
//    // Transient fields for JSON mapping (input)
//    @Transient
//    private Long userId; // Will be mapped to userKey
//
//    @Transient
//    private Long agencyId; // Will be mapped to agencyKey
//
//    @Transient
//    private Long locationId; // Will be mapped to locationKey
//
//    @Transient
//    private String languageCode; // Will be mapped to languageKey
//
//    @Transient
//    private java.time.LocalDate feedbackDate; // Will be converted to feedbackTimestamp
//
//    public Integer getRating() {
//        return rating;
//    }
//
//}
//
//// ===========================
//// DIMENSION ENTITIES
//// ===========================
//
package com.dina.feedback.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import lombok.*;

@Entity
@Table(name = "fact_feedback")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_key")
    public Long feedbackKey;

    @Column(name = "agency_key", nullable = false)
    public Long agencyKey;

    @Column(name = "location_key", nullable = false)
    public Long locationKey;

    @Column(name = "user_key", nullable = false)
    public Long userKey;

    @Column(name = "date_key", nullable = false)
    public Integer dateKey;

    @Column(name = "language_key", nullable = false)
    public Long languageKey;

    @Column(name = "rating_score", nullable = false)
    public Integer rating;

    @Column(name = "comment_text", columnDefinition = "TEXT")
    public String comment;

    @Column(name = "comment_length")
    public Integer commentLength;

    @Column(name = "sentiment_score", precision = 3, scale = 2)
    public Double sentimentScore;

    @Column(name = "feedback_timestamp", nullable = false)
    public LocalDateTime feedbackTimestamp;

    @Column(name = "processing_batch_id")
    public String processingBatchId;

    @Column(name = "created_date")
    public LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (feedbackTimestamp == null) {
            feedbackTimestamp = LocalDateTime.now();
        }
        if (comment != null) {
            commentLength = comment.length();
        }
        if (feedbackTimestamp != null) {
            dateKey = feedbackTimestamp.getYear() * 10000 +
                    feedbackTimestamp.getMonthValue() * 100 +
                    feedbackTimestamp.getDayOfMonth();
        }
    }

    // Transient fields for JSON mapping (input)
    @Transient
    private Long userId;

    @Transient
    private Long agencyId;

    @Transient
    private Long locationId;

    @Transient
    private String languageCode;

    @Transient
    private LocalDate feedbackDate;

    // Getters and Setters

    public Long getFeedbackKey() {
        return feedbackKey;
    }

    public void setFeedbackKey(Long feedbackKey) {
        this.feedbackKey = feedbackKey;
    }

    public Long getAgencyKey() {
        return agencyKey;
    }

    public void setAgencyKey(Long agencyKey) {
        this.agencyKey = agencyKey;
    }

    public Long getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(Long locationKey) {
        this.locationKey = locationKey;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }

    public Integer getDateKey() {
        return dateKey;
    }

    public void setDateKey(Integer dateKey) {
        this.dateKey = dateKey;
    }

    public Long getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(Long languageKey) {
        this.languageKey = languageKey;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCommentLength() {
        return commentLength;
    }

    public void setCommentLength(Integer commentLength) {
        this.commentLength = commentLength;
    }

    public Double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public LocalDateTime getFeedbackTimestamp() {
        return feedbackTimestamp;
    }

    public void setFeedbackTimestamp(LocalDateTime feedbackTimestamp) {
        this.feedbackTimestamp = feedbackTimestamp;
    }

    public String getProcessingBatchId() {
        return processingBatchId;
    }

    public void setProcessingBatchId(String processingBatchId) {
        this.processingBatchId = processingBatchId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public LocalDate getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDate feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
}
