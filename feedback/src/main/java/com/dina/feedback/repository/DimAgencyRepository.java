package com.dina.feedback.repository;
import com.dina.feedback.model.DimAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimAgencyRepository extends JpaRepository<DimAgency, Integer> {
    Optional<DimAgency> findByMention(String mention);

}

