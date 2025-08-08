package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;

import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

public record AluguelDTO(Long id, Long clienteId, Long cacambaId, String endereco, LocalDate dataInicio, LocalDate dataFim, StatusAluguel status, Integer diasAtraso) {}
