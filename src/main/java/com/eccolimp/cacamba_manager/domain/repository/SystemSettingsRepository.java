package com.eccolimp.cacamba_manager.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eccolimp.cacamba_manager.domain.model.SystemSettings;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    Optional<SystemSettings> findTopByOrderByIdAsc();
}


