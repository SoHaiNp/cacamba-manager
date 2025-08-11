package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

public record AluguelDetalhadoDTO(
    Long id,
    String clienteNome,
    String clienteContato,
    String cacambaCodigo,
    Integer cacambaCapacidade,
    String endereco,
    LocalDate dataInicio,
    LocalDate dataFim,
    StatusAluguel status,
    Integer diasRestantes,
    Integer diasAtraso,
    BigDecimal valorContrato,
    BigDecimal valorTroca,
    Integer numeroTrocas,
    BigDecimal totalTrocas
) {} 