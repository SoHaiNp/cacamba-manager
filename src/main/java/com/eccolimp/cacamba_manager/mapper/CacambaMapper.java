package com.eccolimp.cacamba_manager.mapper;

import org.mapstruct.Mapper;

import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.dto.CacambaDTO;

@Mapper(componentModel = "spring")
public interface CacambaMapper {
    CacambaDTO toDto(Cacamba entity);
    Cacamba toEntity(CacambaDTO dto);
}
