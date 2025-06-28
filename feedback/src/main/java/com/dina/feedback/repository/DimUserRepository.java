package com.dina.feedback.repository;


import com.dina.feedback.model.DimUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimUserRepository extends JpaRepository<DimUser, Integer> {
    Optional<DimUser> findByUserId(String userId);

}

