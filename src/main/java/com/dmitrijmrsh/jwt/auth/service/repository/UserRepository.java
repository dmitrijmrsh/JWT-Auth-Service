package com.dmitrijmrsh.jwt.auth.service.repository;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByUsername(String username);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);
}
