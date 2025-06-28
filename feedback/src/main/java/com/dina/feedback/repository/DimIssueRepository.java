package com.dina.feedback.repository;
import com.dina.feedback.model.DimIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimIssueRepository extends JpaRepository<DimIssue, Integer> {
    Optional<DimIssue> findByIssueIdAndIssueClassKey(int issueId, int issueClassKey);

}

