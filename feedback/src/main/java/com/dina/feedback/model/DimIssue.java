package com.dina.feedback.model;
import jakarta.persistence.*; // If using Jakarta EE (Spring Boot 3+)
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dim_issue")
@Data
public class DimIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer issueKey;

    private Integer issueId;
    private Integer issueClassKey;
    private String issueClassCode;
}
