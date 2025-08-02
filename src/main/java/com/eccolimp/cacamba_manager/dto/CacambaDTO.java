package com.eccolimp.cacamba_manager.dto;

import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;

public record CacambaDTO(Long id, String codigo, Integer capacidadeM3, StatusCacamba status) {}
