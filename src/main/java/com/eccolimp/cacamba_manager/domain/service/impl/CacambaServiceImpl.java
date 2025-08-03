package com.eccolimp.cacamba_manager.domain.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.domain.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.domain.service.CacambaService;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.CacambaDTO;
import com.eccolimp.cacamba_manager.mapper.CacambaMapper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CacambaServiceImpl implements CacambaService {

    private final CacambaRepository repo;
    private final CacambaMapper mapper;

    /** Criar uma nova caçamba – código deve ser único. */
    @Override
    public CacambaDTO criar(CacambaDTO dto) {
        // Verificar se já existe uma caçamba com o mesmo código
        if (repo.existsByCodigoIgnoreCase(dto.codigo())) {
            throw new BusinessException("Código já utilizado");
        }
        var entity = mapper.toEntity(dto);
        entity.setStatus(StatusCacamba.DISPONIVEL);
        return mapper.toDto(repo.save(entity));
    }

    /** Atualizar dados básicos ou status. */
    @Override
    public CacambaDTO atualizar(Long id, CacambaDTO dto) {
        var entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caçamba não encontrada"));
        
        // Verificar se a caçamba está alugada e tentando alterar o status
        if (entity.getStatus() == StatusCacamba.ALUGADA && dto.status() != StatusCacamba.ALUGADA) {
            throw new BusinessException("Não é possível alterar o status de uma caçamba alugada. Finalize ou cancele o aluguel primeiro.");
        }
        
        entity.setCodigo(dto.codigo());
        entity.setCapacidadeM3(dto.capacidadeM3());
        entity.setStatus(dto.status());
        return mapper.toDto(repo.save(entity));
    }

    @Override 
    public void deletar(Long id) { 
        var entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caçamba não encontrada"));
        
        if (entity.getStatus() == StatusCacamba.ALUGADA) {
            throw new BusinessException("Não é possível excluir uma caçamba alugada. Finalize ou cancele o aluguel primeiro.");
        }
        
        repo.deleteById(id); 
    }

    /* ---------- Leituras ---------- */

    @Transactional(readOnly = true)
    @Override
    public List<CacambaDTO> listarTodas() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CacambaDTO> listar(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("codigo"));
        return repo.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public CacambaDTO buscarPorId(Long id) {
        return repo.findById(id).map(mapper::toDto)
                   .orElseThrow(() -> new EntityNotFoundException("Caçamba não encontrada"));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean estaDisponivel(Long id) {
        return repo.findById(id)
                   .map(c -> c.getStatus() == StatusCacamba.DISPONIVEL)
                   .orElse(false);
    }
    
}
