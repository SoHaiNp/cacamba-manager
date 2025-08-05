package com.eccolimp.cacamba_manager.dto;

import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CacambaDTO(
        Long id,
        @NotBlank @Size(max = 10) String codigo,
        @NotNull @Min(1) Integer capacidadeM3,
        StatusCacamba status) {}
