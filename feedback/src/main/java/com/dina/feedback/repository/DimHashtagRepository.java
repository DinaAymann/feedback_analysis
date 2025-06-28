package com.dina.feedback.repository;
import com.dina.feedback.model.DimHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DimHashtagRepository extends JpaRepository<DimHashtag, Long> {}