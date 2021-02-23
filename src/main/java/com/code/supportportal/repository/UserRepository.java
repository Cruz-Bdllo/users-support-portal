package com.code.supportportal.repository;

import com.code.supportportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    User findUserByEmail(String email);
}
