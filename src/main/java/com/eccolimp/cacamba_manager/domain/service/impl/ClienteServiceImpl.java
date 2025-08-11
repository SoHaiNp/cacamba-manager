package com.eccolimp.cacamba_manager.domain.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
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
    private final AluguelRepository aluguelRepo;
    private final ClienteMapper mapper;

    @Override
    public ClienteDTO criar(ClienteDTO dto) {
        // Verificar se já existe um cliente com o mesmo contato
        if (repo.existsByContatoIgnoreCase(dto.contato())) {
            throw new BusinessException("Contato já cadastrado");
        }
        // Verificar e-mail obrigatório e único
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BusinessException("Email é obrigatório");
        }
        if (repo.existsByEmailIgnoreCase(dto.email())) {
            throw new BusinessException("Email já cadastrado");
        }
        var entity = mapper.toEntity(dto);
        if (entity.getEmail() != null) {
            entity.setEmail(entity.getEmail().trim().toLowerCase());
        }
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
        // Verificar se o e-mail já existe em outro cliente
        if (!entity.getEmail().equalsIgnoreCase(dto.email()) &&
            repo.existsByEmailIgnoreCase(dto.email())) {
            throw new BusinessException("Email já cadastrado");
        }
        
        entity.setNome(dto.nome());
        entity.setContato(dto.contato());
        entity.setEmail(dto.email() == null ? null : dto.email().trim().toLowerCase());
        return mapper.toDto(repo.save(entity));
    }

    @Override
    public void deletar(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }
        
        // Verificar se há aluguéis associados a este cliente
        var alugueis = aluguelRepo.findByClienteId(id);
        if (!alugueis.isEmpty()) {
            throw new BusinessException("Não é possível excluir um cliente que possui histórico de aluguéis. Os dados são mantidos para fins de auditoria.");
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