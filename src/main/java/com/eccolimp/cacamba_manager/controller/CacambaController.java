package com.eccolimp.cacamba_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.dto.CacambaDTO;
import com.eccolimp.cacamba_manager.mapper.CacambaMapper;
import com.eccolimp.cacamba_manager.repository.CacambaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/cacambas")
@RequiredArgsConstructor
public class CacambaController {

    private final CacambaRepository cacambaRepository;
    private final CacambaMapper cacambaMapper;

    /** Criar caçamba */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CacambaDTO criar(@RequestBody @Validated CacambaDTO dto) {
        var entity = cacambaMapper.toEntity(dto);
        entity.setStatus(StatusCacamba.DISPONIVEL);      // estado inicial
        return cacambaMapper.toDto(cacambaRepository.save(entity));
    }

    /** Listar todas (paginado) */
    @GetMapping
    public Page<CacambaDTO> listar(Pageable pageable) {
        return cacambaRepository.findAll(pageable).map(cacambaMapper::toDto);
    }

    /** Buscar por ID */
    @GetMapping("/{id}")
    public ResponseEntity<CacambaDTO> buscar(@PathVariable Long id) {
        return cacambaRepository.findById(id)
                .map(cacambaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Alterar status ou dados básicos (simples) */
    @PutMapping("/{id}")
    public ResponseEntity<CacambaDTO> atualizar(@PathVariable Long id,
                                                @RequestBody @Validated CacambaDTO dto) {
        return cacambaRepository.findById(id).map(entity -> {
            entity.setCodigo(dto.codigo());
            entity.setCapacidadeM3(dto.capacidadeM3());
            entity.setStatus(dto.status());
            return ResponseEntity.ok(cacambaMapper.toDto(cacambaRepository.save(entity)));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** Excluir (opcional) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        cacambaRepository.deleteById(id);
    }
}
