package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

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
    Integer dias,

    @NotNull(message = "Valor do contrato é obrigatório")
    @DecimalMin(value = "0.00", inclusive = true, message = "Valor do contrato deve ser maior ou igual a 0")
    @Digits(integer = 10, fraction = 2, message = "Valor do contrato deve ter no máximo 2 casas decimais")
    BigDecimal valorContrato,

    @NotNull(message = "Valor da troca é obrigatório")
    @DecimalMin(value = "0.00", inclusive = true, message = "Valor da troca deve ser maior ou igual a 0")
    @Digits(integer = 10, fraction = 2, message = "Valor da troca deve ter no máximo 2 casas decimais")
    BigDecimal valorTroca
) {} 