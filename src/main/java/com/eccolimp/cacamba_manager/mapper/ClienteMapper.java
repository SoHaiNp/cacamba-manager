package com.eccolimp.cacamba_manager.mapper;

import org.mapstruct.Mapper;

import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.dto.ClienteDTO;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteDTO toDto(Cliente entity);
    Cliente toEntity(ClienteDTO dto);
}
