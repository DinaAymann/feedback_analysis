package com.dina.feedback.repository;

import com.dina.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
