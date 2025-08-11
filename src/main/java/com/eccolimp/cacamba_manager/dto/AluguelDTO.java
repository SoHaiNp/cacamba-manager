package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

public record AluguelDTO(
    Long id,
    Long clienteId,
    Long cacambaId,
    String endereco,
    LocalDate dataInicio,
    LocalDate dataFim,
    StatusAluguel status,
    Integer diasAtraso,
    BigDecimal valorContrato,
    BigDecimal valorTroca,
    Integer numeroTrocas,
    BigDecimal totalTrocas
) {}
