package com.eccolimp.cacamba_manager.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;
import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.AluguelDetalhadoDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;
import com.eccolimp.cacamba_manager.dto.AluguelVencendoDTO;
import com.eccolimp.cacamba_manager.dto.AlertasVencimentoDTO;
import com.eccolimp.cacamba_manager.mapper.AluguelMapper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CacambaRepository cacambaRepository;
    private final ClienteRepository clienteRepository;
    private final AluguelMapper aluguelMapper;

    public AluguelDTO registrar(NovoAluguelRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        Cacamba cacamba = cacambaRepository.findById(request.cacambaId())
            .orElseThrow(() -> new BusinessException("Caçamba não encontrada"));

        if (cacamba.getStatus() != StatusCacamba.DISPONIVEL) {
            throw new BusinessException("Caçamba indisponível");
        }

        // Validar se a caçamba está disponível no período solicitado
        LocalDate dataInicio = request.dataInicio();
        LocalDate dataFim = dataInicio.plusDays(request.dias() - 1); // -1 porque o dia de início conta como primeiro dia
        
        // Verificar se existe algum aluguel ativo para esta caçamba no período
        boolean caçambaDisponivelNoPeriodo = aluguelRepository
            .findByCacambaAndStatusAndPeriodo(
                cacamba, 
                StatusAluguel.ATIVO, 
                dataInicio, 
                dataFim
            ).isEmpty();
        
        if (!caçambaDisponivelNoPeriodo) {
            throw new BusinessException("Caçamba não está disponível no período solicitado");
        }

        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setCacamba(cacamba);
        aluguel.setEndereco(request.endereco());
        aluguel.setDataInicio(dataInicio);
        aluguel.setDataFim(dataFim);
        aluguel.setStatus(StatusAluguel.ATIVO);

        cacamba.setStatus(StatusCacamba.ALUGADA);

        aluguelRepository.save(aluguel);
        cacambaRepository.save(cacamba);

        return aluguelMapper.toDto(aluguel);
    }

    public AluguelDTO finalizar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos podem ser finalizados");
        }

        aluguel.setStatus(StatusAluguel.FINALIZADO);
        aluguel.getCacamba().setStatus(StatusCacamba.DISPONIVEL);

        aluguelRepository.save(aluguel);
        cacambaRepository.save(aluguel.getCacamba());

        return aluguelMapper.toDto(aluguel);
    }

    public AluguelDTO cancelar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos podem ser cancelados");
        }

        aluguel.setStatus(StatusAluguel.CANCELADO);
        aluguel.getCacamba().setStatus(StatusCacamba.DISPONIVEL);

        aluguelRepository.save(aluguel);
        cacambaRepository.save(aluguel.getCacamba());

        return aluguelMapper.toDto(aluguel);
    }

    @Transactional(readOnly = true)
    public List<AluguelDTO> listarAtivos() {
        return aluguelRepository.findByStatus(StatusAluguel.ATIVO)
                          .stream()
                          .map(aluguelMapper::toDto)
                          .toList();
    }

    @Transactional(readOnly = true)
    public Page<AluguelDTO> listar(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("dataInicio").descending());
        return aluguelRepository.findAll(pageable).map(aluguelMapper::toDto);
    }

    @Transactional(readOnly = true)
    public AluguelDTO buscarPorId(Long id) {
        return aluguelRepository.findById(id).map(aluguelMapper::toDto)
                   .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));
    }

    @Transactional(readOnly = true)
    public AluguelDetalhadoDTO buscarDetalhadoPorId(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));
        
        AluguelDetalhadoDTO dto = aluguelMapper.toDetalhadoDto(aluguel);
        
        // Calcular dias restantes
        LocalDate hoje = LocalDate.now();
        long diasRestantes = hoje.isAfter(aluguel.getDataFim()) ? 0 : 
                           java.time.temporal.ChronoUnit.DAYS.between(hoje, aluguel.getDataFim());
        
        return new AluguelDetalhadoDTO(
            dto.id(), dto.clienteNome(), dto.clienteContato(), dto.cacambaCodigo(), 
            dto.cacambaCapacidade(), dto.endereco(), dto.dataInicio(), dto.dataFim(), 
            dto.status(), (int) diasRestantes
        );
    }

    @Transactional(readOnly = true)
    public List<AluguelDTO> listarVencendoEm(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(dias);
        return aluguelRepository.findVencendoEm(dataLimite, hoje)
                          .stream()
                          .map(aluguelMapper::toDto)
                          .toList();
    }
    
    @Transactional(readOnly = true)
    public AlertasVencimentoDTO buscarAlertasVencimento() {
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);
        LocalDate dataLimite = hoje.plusDays(3);
        
        // Buscar aluguéis vencendo hoje
        var vencendoHoje = aluguelRepository.findVencendoNaData(hoje)
                .stream()
                .map(this::toAluguelVencendoDTO)
                .toList();
        
        // Buscar aluguéis vencendo amanhã
        var vencendoAmanha = aluguelRepository.findVencendoNaData(amanha)
                .stream()
                .map(this::toAluguelVencendoDTO)
                .toList();
        
        // Buscar aluguéis vencendo nos próximos dias (2 e 3 dias)
        var vencendoProximosDias = aluguelRepository.findVencendoEm(dataLimite, amanha.plusDays(1))
                .stream()
                .map(this::toAluguelVencendoDTO)
                .toList();
        
        int total = vencendoHoje.size() + vencendoAmanha.size() + vencendoProximosDias.size();
        
        return new AlertasVencimentoDTO(vencendoHoje, vencendoAmanha, vencendoProximosDias, total);
    }
    
    private AluguelVencendoDTO toAluguelVencendoDTO(Aluguel aluguel) {
        LocalDate hoje = LocalDate.now();
        int diasRestantes = (int) java.time.temporal.ChronoUnit.DAYS.between(hoje, aluguel.getDataFim());
        
        String tipoVencimento;
        if (aluguel.getDataFim().equals(hoje)) {
            tipoVencimento = "HOJE";
        } else if (aluguel.getDataFim().equals(hoje.plusDays(1))) {
            tipoVencimento = "AMANHA";
        } else {
            tipoVencimento = "PROXIMOS_DIAS";
        }
        
        return new AluguelVencendoDTO(
            aluguel.getId(),
            aluguel.getCliente().getNome(),
            aluguel.getCacamba().getCodigo(),
            aluguel.getDataFim(),
            diasRestantes,
            tipoVencimento
        );
    }

    @Transactional(readOnly = true)
    public long countAtivos() {
        return aluguelRepository.countAtivos();
    }
}
