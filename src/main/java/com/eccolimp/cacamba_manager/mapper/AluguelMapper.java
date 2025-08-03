package com.eccolimp.cacamba_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;

@Mapper(componentModel = "spring")
public interface AluguelMapper {
    
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "cacambaId", source = "cacamba.id")
    AluguelDTO toDto(Aluguel entity);
    
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "cacamba", ignore = true)
    Aluguel toEntity(AluguelDTO dto);
}
