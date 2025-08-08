package com.eccolimp.cacamba_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.AluguelDetalhadoDTO;

@Mapper(componentModel = "spring")
public interface AluguelMapper {
    
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "cacambaId", source = "cacamba.id")
    @Mapping(target = "diasAtraso", ignore = true)
    AluguelDTO toDto(Aluguel entity);
    
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "cacamba", ignore = true)
    Aluguel toEntity(AluguelDTO dto);
    
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "clienteContato", source = "cliente.contato")
    @Mapping(target = "cacambaCodigo", source = "cacamba.codigo")
    @Mapping(target = "cacambaCapacidade", source = "cacamba.capacidadeM3")
    @Mapping(target = "diasRestantes", ignore = true)
    @Mapping(target = "diasAtraso", ignore = true)
    AluguelDetalhadoDTO toDetalhadoDto(Aluguel entity);
}
