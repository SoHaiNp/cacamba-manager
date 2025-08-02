package com.eccolimp.cacamba_manager.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    @Query("SELECT a FROM Aluguel a WHERE a.dataFim >= :today")
    List<Aluguel> findByDataFimAfter(LocalDate today);
}
