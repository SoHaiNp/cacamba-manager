package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;

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
    Integer diasAtraso
) {} 