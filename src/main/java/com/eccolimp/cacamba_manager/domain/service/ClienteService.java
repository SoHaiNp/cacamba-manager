package com.eccolimp.cacamba_manager.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.eccolimp.cacamba_manager.dto.ClienteDTO;

public interface ClienteService {

    ClienteDTO criar(ClienteDTO dto);
    ClienteDTO atualizar(Long id, ClienteDTO dto);
    void deletar(Long id);

    List<ClienteDTO> listarTodos();
    Page<ClienteDTO> listar(int page, int size);
    ClienteDTO buscarPorId(Long id);
} 