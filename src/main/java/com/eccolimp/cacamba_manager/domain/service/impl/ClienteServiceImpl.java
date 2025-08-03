package com.eccolimp.cacamba_manager.domain.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;
import com.eccolimp.cacamba_manager.domain.service.ClienteService;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.ClienteDTO;
import com.eccolimp.cacamba_manager.mapper.ClienteMapper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repo;
    private final ClienteMapper mapper;

    @Override
    public ClienteDTO criar(ClienteDTO dto) {
        // Verificar se já existe um cliente com o mesmo contato
        if (repo.existsByContatoIgnoreCase(dto.contato())) {
            throw new BusinessException("Contato já cadastrado");
        }
        var entity = mapper.toEntity(dto);
        return mapper.toDto(repo.save(entity));
    }

    @Override
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        var entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        
        // Verificar se o contato já existe em outro cliente
        if (!entity.getContato().equalsIgnoreCase(dto.contato()) && 
            repo.existsByContatoIgnoreCase(dto.contato())) {
            throw new BusinessException("Contato já cadastrado");
        }
        
        entity.setNome(dto.nome());
        entity.setContato(dto.contato());
        return mapper.toDto(repo.save(entity));
    }

    @Override
    public void deletar(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteDTO> listarTodos() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ClienteDTO> listar(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("nome"));
        return repo.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteDTO buscarPorId(Long id) {
        return repo.findById(id).map(mapper::toDto)
                   .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    }
} 