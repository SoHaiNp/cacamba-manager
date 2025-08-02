package com.eccolimp.cacamba_manager.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NovoAluguelRequest(@NotNull Long clienteId, @NotNull Long cacambaId, @NotBlank String endereco, @Min(1) int dias) {}
