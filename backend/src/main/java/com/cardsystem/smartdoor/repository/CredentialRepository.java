package com.cardsystem.smartdoor.repository;

import com.cardsystem.smartdoor.model.Credential;
import com.cardsystem.smartdoor.model.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Integer> {
    List<Credential> findByUserId(Integer userId);
    Optional<Credential> findByTypeAndIndex(CredentialType type, Integer index);
}
