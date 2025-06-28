package com.dina.feedback.repository;
import com.dina.feedback.model.DimLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimLocationRepository extends JpaRepository<DimLocation, Integer> {
    Optional<DimLocation> findByLocationString(String locationString);

}

