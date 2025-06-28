package com.dina.feedback.repository;
import com.dina.feedback.model.DimDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DimDateRepository extends JpaRepository<DimDate, Integer> {}

