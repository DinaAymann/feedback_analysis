package com.dina.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
