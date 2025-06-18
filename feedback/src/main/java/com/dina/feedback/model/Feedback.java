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

// Agency Dimension
@Entity
@Table(name = "dim_agency")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agency_id")
    private Long agencyId;

    @Column(name = "agency_code", unique = true, nullable = false)
    private String agencyCode;

    @Column(name = "agency_name", nullable = false)
    private String agencyName;

    @Column(name = "agency_type")
    private String agencyType;

    @Column(name = "parent_agency_id")
    private Long parentAgencyId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}

// Location Dimension
@Entity
@Table(name = "dim_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "location_code", unique = true, nullable = false)
    private String locationCode;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "location_type")
    private String locationType;

    @Column(name = "parent_location_id")
    private Long parentLocationId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "state_code")
    private String stateCode;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "latitude", precision = 10, scale = 8)
    private Double latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private Double longitude;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}

// User Dimension
@Entity
@Table(name = "dim_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_external_id", unique = true)
    private String userExternalId;

    @Column(name = "user_segment")
    private String userSegment;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "registration_date")
    private java.time.LocalDate registrationDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}

// Language Dimension
@Entity
@Table(name = "dim_language")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Long languageId;

    @Column(name = "language_code", unique = true, nullable = false)
    private String languageCode;

    @Column(name = "language_name", nullable = false)
    private String languageName;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

// Job Status Entity
@Entity
@Table(name = "job_execution_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecutionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_execution_id", unique = true)
    private Long jobExecutionId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "batch_status")
    private String batchStatus;

    @Column(name = "exit_status")
    private String exitStatus;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "records_processed")
    private Long recordsProcessed;

    @Column(name = "records_failed")
    private Long recordsFailed;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}