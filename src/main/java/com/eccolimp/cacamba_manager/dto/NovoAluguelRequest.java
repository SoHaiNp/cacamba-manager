package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NovoAluguelRequest(
    @NotNull(message = "Cliente é obrigatório")
    Long clienteId,
    
    @NotNull(message = "Caçamba é obrigatória")
    Long cacambaId,
    
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 10, max = 180, message = "Endereço deve ter entre 10 e 180 caracteres")
    String endereco,
    
    @NotNull(message = "Data de início é obrigatória")
    LocalDate dataInicio,
    
    @NotNull(message = "Dias de aluguel é obrigatório")
    @Min(value = 1, message = "Dias de aluguel deve ser pelo menos 1")
    @Max(value = 10, message = "Dias de aluguel não pode ser maior que 10")
    Integer dias
) {} 