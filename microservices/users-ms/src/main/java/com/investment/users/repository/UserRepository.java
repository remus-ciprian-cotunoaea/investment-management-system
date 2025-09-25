package com.investment.users.repository;


import com.investment.users.entity.UserEntity;
import com.investment.users.utils.enums.UserStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository  extends JpaRepository<UserEntity, UUID>{

    // Find a users by email (used for login/registration)
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    // Check if a users already exists by email
    boolean existsByEmail(String email);

    // List users by status (e.g., ACTIVE/INACTIVE) with pagination
    Page<UserEntity> findAllByStatus(UserStatusEnum status, Pageable pageable);

    // Retrieve users created within a given time range (reporting use cases)
    Page<UserEntity> findAllByCreatedAtBetween(Instant from, Instant to, Pageable pageable);

    // Search users by partial name (case-insensitive), useful for admin UI
    Page<UserEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
