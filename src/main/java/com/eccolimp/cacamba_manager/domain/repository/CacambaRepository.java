package com.eccolimp.cacamba_manager.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;

public interface CacambaRepository extends JpaRepository<Cacamba, Long> {
    List<Cacamba> findByStatus(StatusCacamba status);
    boolean existsByCodigoIgnoreCase(String codigo);
}
