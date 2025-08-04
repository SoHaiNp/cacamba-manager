package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;

public record AluguelVencendoDTO(
    Long id,
    String clienteNome,
    String cacambaCodigo,
    LocalDate dataFim,
    int diasRestantes,
    String tipoVencimento // "HOJE", "AMANHA", "PROXIMOS_DIAS"
) {} 