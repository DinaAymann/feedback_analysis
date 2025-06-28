package com.dina.feedback.model;
import jakarta.persistence.*; // If using Jakarta EE (Spring Boot 3+)
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dim_agency")
@Data
public class DimAgency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer agencyKey;

    @Column(unique = true)
    private String mention;

//    private String sector;

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FactMention> mentions = new HashSet<>();

}
