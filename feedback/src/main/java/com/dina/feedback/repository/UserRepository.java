package com.dina.feedback.repository;

import com.dina.feedback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.userId FROM User u WHERE u.userExternalId = :externalId AND u.isActive = true")
    Optional<Long> findUserIdByExternalId(String externalId);

    Optional<User> findByUserExternalIdAndIsActive(String userExternalId, Boolean isActive);
}
