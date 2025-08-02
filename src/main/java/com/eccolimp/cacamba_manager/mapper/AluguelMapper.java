package com.eccolimp.cacamba_manager.mapper;

import org.mapstruct.Mapper;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;

@Mapper(componentModel = "spring")
public interface AluguelMapper {
    AluguelDTO toDto(Aluguel entity);
    Aluguel toEntity(AluguelDTO dto);
}
