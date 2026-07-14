package com.company.ems.repository;

import com.company.ems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Repository for user authentication */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
