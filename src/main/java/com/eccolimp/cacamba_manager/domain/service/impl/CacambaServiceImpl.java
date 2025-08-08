package com.eccolimp.cacamba_manager.domain.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
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
        
        // Verificar se há aluguéis associados a esta caçamba
        var alugueis = aluguelRepo.findByCacambaId(id);
        if (!alugueis.isEmpty()) {
            throw new BusinessException("Não é possível excluir uma caçamba que possui histórico de aluguéis. Considere alterar o status para 'Manutenção' em vez de excluir.");
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
        return repo.findById(id)
                   .map(c -> c.getStatus() == StatusCacamba.DISPONIVEL)
                   .orElse(false);
    }
}
