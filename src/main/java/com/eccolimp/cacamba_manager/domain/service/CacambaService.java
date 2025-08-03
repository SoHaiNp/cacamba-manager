package com.eccolimp.cacamba_manager.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.eccolimp.cacamba_manager.dto.CacambaDTO;

public interface CacambaService {

    CacambaDTO criar(CacambaDTO dto);
    CacambaDTO atualizar(Long id, CacambaDTO dto);
    void deletar(Long id);

    List<CacambaDTO> listarTodas();
    Page<CacambaDTO> listar(int page, int size);
    CacambaDTO buscarPorId(Long id);

    boolean estaDisponivel(Long id);
}
