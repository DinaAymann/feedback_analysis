package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fact_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private Long userId;
    private Integer rating;
    private String comment;

    @Column(name = "agency_id")
    private Long agencyId;

    @Column(name = "location_id")
    private Long locationId;

    private String languageCode;

    private java.time.LocalDate feedbackDate;
}
