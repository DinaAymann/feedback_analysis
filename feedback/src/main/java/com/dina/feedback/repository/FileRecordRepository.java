package com.dina.feedback.repository;

import com.dina.feedback.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByProcessedFalse();
    boolean existsByFilename(String filename);
    long countByProcessedTrue();
    long countByProcessedFalse();
}