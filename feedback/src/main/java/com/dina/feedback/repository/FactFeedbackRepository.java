package com.dina.feedback.repository;

import com.dina.feedback.model.FactFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactFeedbackRepository extends JpaRepository<FactFeedback, Long> , JpaSpecificationExecutor<FactFeedback> {
    List<FactFeedback> findByDate_DateKeyBetween(Integer from, Integer to);

}
