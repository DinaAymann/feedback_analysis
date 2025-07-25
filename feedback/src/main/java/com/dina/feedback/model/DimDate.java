package com.dina.feedback.model;
import jakarta.persistence.*; // If using Jakarta EE (Spring Boot 3+)
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dim_date")
@Data
public class DimDate {
    @Id
    private Integer dateKey; // YYYYMMDD

    private LocalDate fullDate;
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer week;
    private String weekdayName;
}
