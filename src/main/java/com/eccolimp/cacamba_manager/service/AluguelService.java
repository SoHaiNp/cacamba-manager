package com.eccolimp.cacamba_manager.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.mapper.AluguelMapper;
import com.eccolimp.cacamba_manager.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.repository.ClienteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CacambaRepository cacambaRepository;
    private final ClienteRepository clienteRepository;
    private final AluguelMapper aluguelMapper;

    public AluguelDTO registrar(Long clienteId, Long cacambaId,
    String endereco, int dias) {

    Cliente cliente = clienteRepository.findById(clienteId)
    .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

    Cacamba cacamba = cacambaRepository.findById(cacambaId)
    .orElseThrow(() -> new BusinessException("Caçamba não encontrada"));

    if (cacamba.getStatus() != StatusCacamba.DISPONIVEL) {
    throw new BusinessException("Caçamba indisponível");
    }

    LocalDate inicio = LocalDate.now();
    LocalDate fim = inicio.plusDays(dias);

    Aluguel aluguel = new Aluguel();
    aluguel.setCliente(cliente);
    aluguel.setCacamba(cacamba);
    aluguel.setEndereco(endereco);
    aluguel.setDataInicio(inicio);
    aluguel.setDataFim(fim);

    cacamba.setStatus(StatusCacamba.ALUGADA);

    aluguelRepository.save(aluguel);          // cascade não ativado
    cacambaRepository.save(cacamba);

    return aluguelMapper.toDto(aluguel);
    }

    public List<AluguelDTO> listarAtivos() {
        return aluguelRepository.ativos(LocalDate.now())
                          .stream()
                          .map(aluguelMapper::toDto)
                          .toList();
    }
}
