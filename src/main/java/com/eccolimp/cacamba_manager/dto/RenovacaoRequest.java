package com.eccolimp.cacamba_manager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RenovacaoRequest(
    @NotNull Long aluguelId,
    @NotNull LocalDate novaDataInicio,
    @NotNull @Min(1) Integer dias,
    @NotNull @DecimalMin(value = "0.00") @Digits(integer = 10, fraction = 2) BigDecimal valorContrato,
    @NotNull @DecimalMin(value = "0.00") @Digits(integer = 10, fraction = 2) BigDecimal valorTroca
) {}


