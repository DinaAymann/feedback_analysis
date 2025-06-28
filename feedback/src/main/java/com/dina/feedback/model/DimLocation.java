package com.dina.feedback.model;
import jakarta.persistence.*; // If using Jakarta EE (Spring Boot 3+)
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dim_location")
@Data
public class DimLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationKey;

    private String locationString;
    private String city;
    private String country;
}

