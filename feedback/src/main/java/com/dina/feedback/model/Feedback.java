//package com.dina.feedback.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "fact_feedback")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Feedback {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long feedbackId;
//
//    private Long userId;
//    private Integer rating;
//    private String comment;
//
//    @Column(name = "agency_id")
//    private Long agencyId;
//
//    @Column(name = "location_id")
//    private Long locationId;
//
//    private String languageCode;
//
//    private java.time.LocalDate feedbackDate;
//}
// ===========================
// ENHANCED FEEDBACK ENTITY
// ===========================
package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fact_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_key")
    private Long feedbackKey;

    // Foreign key references
    @Column(name = "agency_key", nullable = false)
    private Long agencyKey;

    @Column(name = "location_key", nullable = false)
    private Long locationKey;

    @Column(name = "user_key", nullable = false)
    private Long userKey;

    @Column(name = "date_key", nullable = false)
    private Integer dateKey;

    @Column(name = "language_key", nullable = false)
    private Long languageKey;

    // Measures
    @Column(name = "rating_score", nullable = false)
    private Integer rating;

    @Column(name = "comment_text", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "comment_length")
    private Integer commentLength;

    @Column(name = "sentiment_score", precision = 3, scale = 2)
    private Double sentimentScore;

    // Metadata
    @Column(name = "feedback_timestamp", nullable = false)
    private LocalDateTime feedbackTimestamp;

    @Column(name = "processing_batch_id")
    private String processingBatchId;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (feedbackTimestamp == null) {
            feedbackTimestamp = LocalDateTime.now();
        }
        if (comment != null) {
            commentLength = comment.length();
        }
        // Calculate date key (YYYYMMDD format)
        if (feedbackTimestamp != null) {
            dateKey = feedbackTimestamp.getYear() * 10000 +
                    feedbackTimestamp.getMonthValue() * 100 +
                    feedbackTimestamp.getDayOfMonth();
        }
    }

    // Transient fields for JSON mapping (input)
    @Transient
    private Long userId; // Will be mapped to userKey

    @Transient
    private Long agencyId; // Will be mapped to agencyKey

    @Transient
    private Long locationId; // Will be mapped to locationKey

    @Transient
    private String languageCode; // Will be mapped to languageKey

    @Transient
    private java.time.LocalDate feedbackDate; // Will be converted to feedbackTimestamp
}

// ===========================
// DIMENSION ENTITIES
// ===========================

