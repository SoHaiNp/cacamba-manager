package com.eccolimp.cacamba_manager.domain.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.domain.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.service.CacambaService;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.CacambaDTO;
import com.eccolimp.cacamba_manager.mapper.CacambaMapper;
import com.eccolimp.cacamba_manager.domain.model.Cacamba;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service("cacambaService")
@RequiredArgsConstructor
@Transactional
public class CacambaServiceImpl implements CacambaService {

    private final CacambaRepository repo;
    private final AluguelRepository aluguelRepo;
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
        var salvo = repo.save(entity);
        return mapper.toDto(salvo);
    }

    /** Atualizar dados básicos. */
    @Override
    public CacambaDTO atualizar(Long id, CacambaDTO dto) {
        var entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caçamba não encontrada"));
        entity.setCodigo(dto.codigo());
        entity.setCapacidadeM3(dto.capacidadeM3());
        return mapper.toDto(repo.save(entity));
    }

    @Override 
    public void deletar(Long id) { 
        repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caçamba não encontrada"));
        
        // Impedir exclusão se houver aluguel ATIVO para esta caçamba
        var alugueisAtivos = aluguelRepo.findByCacambaIdAndStatusAtivo(id);
        if (!alugueisAtivos.isEmpty()) {
            throw new BusinessException("Não é possível excluir uma caçamba que está em uso (aluguel ativo).");
        }
        
        repo.deleteById(id); 
    }

    /* ---------- Leituras ---------- */

    @Override
    public List<CacambaDTO> listarTodas() {
        List<Cacamba> cacambas = repo.findAll();
        return cacambas.stream()
                      .map(mapper::toDto)
                      .collect(Collectors.toList());
    }

    @Override
    public Page<CacambaDTO> listar(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("codigo"));
        Page<Cacamba> cacambas = repo.findAll(pageable);
        return cacambas.map(mapper::toDto);
    }

    @Override
    public CacambaDTO buscarPorId(Long id) {
        Cacamba cacamba = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caçamba não encontrada"));
        return mapper.toDto(cacamba);
    }

    @Override
    public boolean estaDisponivel(Long id) {
        return aluguelRepo.findByCacambaIdAndStatusAtivo(id).isEmpty();
    }
}
