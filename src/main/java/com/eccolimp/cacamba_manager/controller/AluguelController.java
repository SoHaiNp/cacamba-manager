package com.eccolimp.cacamba_manager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.service.AluguelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/alugueis")
@RequiredArgsConstructor
public class AluguelController {

    private final AluguelService aluguelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AluguelDTO registrar(@RequestBody @Valid NovoAluguelRequest req) {
        return aluguelService.registrar(
                req.clienteId(), req.cacambaId(),
                req.endereco(), req.dias());
    }

    @GetMapping("/ativos")
    public List<AluguelDTO> ativos() { return aluguelService.listarAtivos(); }
}
