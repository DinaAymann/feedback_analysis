package com.dina.feedback.repository;


import com.dina.feedback.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByProcessedFalse();
}
