package com.dina.feedback.repository;

import com.dina.feedback.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

    @Query("SELECT a.agencyId FROM Agency a WHERE a.agencyCode = :agencyCode AND a.isActive = true")
    Optional<Long> findAgencyIdByCode(String agencyCode);

    Optional<Agency> findByAgencyCodeAndIsActive(String agencyCode, Boolean isActive);
}