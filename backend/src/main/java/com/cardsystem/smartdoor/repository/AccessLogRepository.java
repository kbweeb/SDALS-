package com.cardsystem.smartdoor.repository;

import com.cardsystem.smartdoor.model.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {
    Page<AccessLog> findAllByOrderByLoggedAtDesc(Pageable pageable);
}
