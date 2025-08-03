package com.eccolimp.cacamba_manager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eccolimp.cacamba_manager.domain.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

}
