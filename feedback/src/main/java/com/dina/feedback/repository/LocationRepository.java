package com.dina.feedback.repository;

import com.dina.feedback.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l.locationId FROM Location l WHERE l.locationCode = :locationCode AND l.isActive = true")
    Optional<Long> findLocationIdByCode(String locationCode);

    Optional<Location> findByLocationCodeAndIsActive(String locationCode, Boolean isActive);
}