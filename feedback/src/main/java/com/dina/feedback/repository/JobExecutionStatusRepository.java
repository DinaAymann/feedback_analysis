package com.dina.feedback.repository;

import com.dina.feedback.model.JobExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobExecutionStatusRepository extends JpaRepository<JobExecutionStatus, Long> {

    Optional<JobExecutionStatus> findByJobExecutionId(Long jobExecutionId);

    List<JobExecutionStatus> findByJobNameOrderByStartTimeDesc(String jobName);

    @Query("SELECT j FROM JobExecutionStatus j WHERE j.startTime >= :fromDate ORDER BY j.startTime DESC")
    List<JobExecutionStatus> findRecentJobs(LocalDateTime fromDate);

    @Query("SELECT j FROM JobExecutionStatus j WHERE j.batchStatus IN ('RUNNING', 'STARTING') ORDER BY j.startTime DESC")
    List<JobExecutionStatus> findRunningJobs();
}