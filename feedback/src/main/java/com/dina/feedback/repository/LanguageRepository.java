package com.dina.feedback.repository;

import com.dina.feedback.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    @Query("SELECT l.languageId FROM Language l WHERE l.languageCode = :languageCode AND l.isActive = true")
    Optional<Long> findLanguageIdByCode(String languageCode);

    Optional<Language> findByLanguageCodeAndIsActive(String languageCode, Boolean isActive);
}
