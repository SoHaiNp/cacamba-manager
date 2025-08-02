package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;

public record AluguelDTO(Long id, Long clienteId, Long cacambaId, String endereco, LocalDate dataInicio, LocalDate dataFim) {}
