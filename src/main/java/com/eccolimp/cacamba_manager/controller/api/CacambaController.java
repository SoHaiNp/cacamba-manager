package com.eccolimp.cacamba_manager.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import com.eccolimp.cacamba_manager.dto.CacambaDTO;
import com.eccolimp.cacamba_manager.domain.service.CacambaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller responsável pelo CRUD de Caçambas.
 * <p>
 * A regra de negócio (validações, verificação de status, etc.) é delegada
 * integralmente ao {@link CacambaService}.  O controller apenas expõe os
 * endpoints HTTP e converte parâmetros de request → objetos de domínio.
 */
@RestController
@RequestMapping("/api/v1/cacambas")
@RequiredArgsConstructor
@Validated
public class CacambaController {

    private final CacambaService cacambaService;

    /**
     * POST /api/v1/cacambas
     */
    @PostMapping
    public ResponseEntity<CacambaDTO> criar(@Valid @RequestBody CacambaDTO dto) {
        CacambaDTO created = cacambaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/v1/cacambas?page=0&size=20
     */
    @GetMapping
    public Page<CacambaDTO> listar(Pageable pageable) {
        return cacambaService.listar(pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * GET /api/v1/cacambas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CacambaDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(cacambaService.buscarPorId(id));
    }

    /**
     * PUT /api/v1/cacambas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CacambaDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody CacambaDTO dto) {
        return ResponseEntity.ok(cacambaService.atualizar(id, dto));
    }

    /**
     * DELETE /api/v1/cacambas/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        cacambaService.deletar(id);
    }
}
