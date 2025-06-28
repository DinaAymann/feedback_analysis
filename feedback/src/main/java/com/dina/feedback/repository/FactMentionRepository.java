package com.dina.feedback.repository;


import com.dina.feedback.model.FactMention;
import com.dina.feedback.model.FactMentionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactMentionRepository extends JpaRepository<FactMention, FactMentionId> {
}

