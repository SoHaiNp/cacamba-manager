package com.eccolimp.cacamba_manager.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    
    @Query("SELECT a FROM Aluguel a WHERE a.dataFim >= :dataLimite")
    List<Aluguel> ativos(@Param("dataLimite") LocalDate dataLimite);
    
    List<Aluguel> findByStatus(StatusAluguel status);
    
    Page<Aluguel> findByStatus(StatusAluguel status, Pageable pageable);
    
    List<Aluguel> findByClienteId(Long clienteId);
    
    List<Aluguel> findByCacambaId(Long cacambaId);
    
    @Query("SELECT a FROM Aluguel a WHERE a.status = :status AND a.dataFim BETWEEN :dataInicio AND :dataFim")
    List<Aluguel> findByStatusAndDataFimBetween(
        @Param("status") StatusAluguel status,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    @Query("SELECT a FROM Aluguel a WHERE a.status = 'ATIVO' AND a.dataFim <= :dataLimite")
    List<Aluguel> findVencendoEm(@Param("dataLimite") LocalDate dataLimite);
    
    @Query("SELECT COUNT(a) FROM Aluguel a WHERE a.status = 'ATIVO'")
    long countAtivos();
}
