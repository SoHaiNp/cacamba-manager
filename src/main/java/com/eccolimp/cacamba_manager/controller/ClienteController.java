package com.eccolimp.cacamba_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.dto.ClienteDTO;
import com.eccolimp.cacamba_manager.mapper.ClienteMapper;
import com.eccolimp.cacamba_manager.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@RequestBody @Validated ClienteDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toDto(clienteSalvo));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> listar(Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        Page<ClienteDTO> clientesDTO = clientes.map(clienteMapper::toDto);
        return ResponseEntity.ok(clientesDTO);
    }
}
