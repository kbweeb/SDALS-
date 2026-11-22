package com.cardsystem.smartdoor.repository;

import com.cardsystem.smartdoor.model.Role;
import com.cardsystem.smartdoor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(Role role);
}
