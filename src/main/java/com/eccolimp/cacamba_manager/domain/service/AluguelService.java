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
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.domain.repository.AluguelHistoricoRepository;
import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.AluguelDetalhadoDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;
import com.eccolimp.cacamba_manager.dto.FiltroAluguel;
import com.eccolimp.cacamba_manager.dto.AluguelVencendoDTO;
import com.eccolimp.cacamba_manager.dto.AlertasVencimentoDTO;
import com.eccolimp.cacamba_manager.mapper.AluguelMapper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import com.eccolimp.cacamba_manager.domain.repository.spec.AluguelSpecifications;

@Service
@RequiredArgsConstructor
@Transactional
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CacambaRepository cacambaRepository;
    private final ClienteRepository clienteRepository;
    private final AluguelHistoricoRepository aluguelHistoricoRepository;
    private final AluguelMapper aluguelMapper;

    public AluguelDTO registrar(NovoAluguelRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        Cacamba cacamba = cacambaRepository.findById(request.cacambaId())
            .orElseThrow(() -> new BusinessException("Caçamba não encontrada"));

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

        aluguelRepository.save(aluguel);

        return aluguelMapper.toDto(aluguel);
    }

    public AluguelDTO finalizar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos podem ser finalizados");
        }

        aluguel.setStatus(StatusAluguel.FINALIZADO);

        aluguelRepository.save(aluguel);

        return aluguelMapper.toDto(aluguel);
    }

    public AluguelDTO cancelar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos podem ser cancelados");
        }

        aluguel.setStatus(StatusAluguel.CANCELADO);

        aluguelRepository.save(aluguel);

        return aluguelMapper.toDto(aluguel);
    }

    /**
     * Arquiva um aluguel CANCELADO ou FINALIZADO para o histórico e remove-o da tabela principal.
     */
    public void arquivar(Long id) {
        Aluguel aluguel = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() == StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis cancelados ou finalizados podem ser arquivados");
        }

        var historico = new com.eccolimp.cacamba_manager.domain.model.AluguelHistorico();
        historico.setAluguelId(aluguel.getId());
        historico.setClienteId(aluguel.getCliente().getId());
        historico.setCacambaId(aluguel.getCacamba().getId());
        historico.setEndereco(aluguel.getEndereco());
        historico.setDataInicio(aluguel.getDataInicio());
        historico.setDataFim(aluguel.getDataFim());
        historico.setStatus(aluguel.getStatus().name());
        historico.setDataArquivamento(java.time.LocalDateTime.now());

        aluguelHistoricoRepository.save(historico);
        aluguelRepository.deleteById(id);
    }

    /**
     * Renova um aluguel ativo adicionando mais dias ao período.
     * Regras:
     * - Apenas aluguéis ATIVOS podem ser renovados
     * - diasAdicionais deve ser > 0
     * - Verifica disponibilidade da caçamba no novo período
     * - Se o aluguel estiver vencido, a renovação conta a partir de hoje
     * - Se ainda não estiver vencido, a renovação conta a partir do dia seguinte ao dataFim atual
     */
    public AluguelDTO renovar(Long id, int diasAdicionais) {
        if (diasAdicionais <= 0) {
            throw new BusinessException("Informe um número de dias adicionais maior que zero");
        }

        Aluguel aluguel = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos podem ser renovados");
        }

        LocalDate hoje = LocalDate.now();
        LocalDate periodoInicio = hoje.isAfter(aluguel.getDataFim())
            ? hoje
            : aluguel.getDataFim().plusDays(1);
        LocalDate novaDataFim = periodoInicio.plusDays(diasAdicionais - 1);

        // Verificar disponibilidade da caçamba no novo período
        boolean disponivel = aluguelRepository
            .findByCacambaAndStatusAndPeriodo(
                aluguel.getCacamba(),
                StatusAluguel.ATIVO,
                periodoInicio,
                novaDataFim
            )
            .isEmpty();

        if (!disponivel) {
            throw new BusinessException("Caçamba não está disponível no período solicitado para renovação");
        }

        aluguel.setDataFim(novaDataFim);

        aluguelRepository.save(aluguel);
        return aluguelMapper.toDto(aluguel);
    }

    /**
     * Renova criando um novo contrato (nova linha) e finaliza o contrato atual.
     * Retorna o DTO do novo contrato criado.
     */
    public AluguelDTO renovarCriandoNovo(Long id, LocalDate novaDataInicio, int dias) {
        if (dias <= 0) {
            throw new BusinessException("Informe um número de dias maior que zero");
        }

        Aluguel aluguelAtual = aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguelAtual.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos podem ser renovados");
        }

        LocalDate hoje = LocalDate.now();
        if (novaDataInicio.isBefore(hoje)) {
            throw new BusinessException("A data de início da renovação não pode ser no passado");
        }
        if (novaDataInicio.isEqual(aluguelAtual.getDataInicio())) {
            throw new BusinessException("A data de início da renovação não pode ser igual à data de início do contrato atual");
        }

        LocalDate novaDataFim = novaDataInicio.plusDays(dias - 1);

        // Finalizar o contrato atual para não conflitar com a verificação de disponibilidade
        aluguelAtual.setStatus(StatusAluguel.FINALIZADO);
        aluguelRepository.save(aluguelAtual);

        // Verificar disponibilidade da mesma caçamba no novo período
        boolean disponivel = aluguelRepository
            .findByCacambaAndStatusAndPeriodo(
                aluguelAtual.getCacamba(),
                StatusAluguel.ATIVO,
                novaDataInicio,
                novaDataFim
            )
            .isEmpty();

        if (!disponivel) {
            // Lança exceção para provocar rollback e reverter a finalização
            throw new BusinessException("Caçamba não está disponível no período solicitado para a renovação");
        }

        // Criar novo contrato com os mesmos dados base
        Aluguel novo = new Aluguel();
        novo.setCliente(aluguelAtual.getCliente());
        novo.setCacamba(aluguelAtual.getCacamba());
        novo.setEndereco(aluguelAtual.getEndereco());
        novo.setDataInicio(novaDataInicio);
        novo.setDataFim(novaDataFim);
        novo.setStatus(StatusAluguel.ATIVO);

        Aluguel salvo = aluguelRepository.save(novo);
        return aluguelMapper.toDto(salvo);
    }

    /**
     * Troca a caçamba de um aluguel ATIVO por outra caçamba disponível.
     */
    public AluguelDTO trocarCacamba(Long aluguelId, Long novaCacambaId) {
        Aluguel aluguel = aluguelRepository.findById(aluguelId)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado"));

        if (aluguel.getStatus() != StatusAluguel.ATIVO) {
            throw new BusinessException("Apenas aluguéis ativos permitem troca de caçamba");
        }

        Cacamba nova = cacambaRepository.findById(novaCacambaId)
            .orElseThrow(() -> new BusinessException("Caçamba não encontrada"));

        if (aluguel.getCacamba().getId().equals(nova.getId())) {
            throw new BusinessException("Selecione uma caçamba diferente da atual");
        }

        // Verificar se a nova caçamba está disponível agora (sem outro aluguel ATIVO)
        boolean ocupada = !aluguelRepository.findByCacambaIdAndStatusAtivo(nova.getId()).isEmpty();
        if (ocupada) {
            throw new BusinessException("A caçamba selecionada ficou indisponível. Lista atualizada. Tente novamente.");
        }

        aluguel.setCacamba(nova);
        aluguelRepository.save(aluguel);
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
    public boolean cacambaEstaEmUso(Long cacambaId) {
        return !aluguelRepository.findByCacambaIdAndStatusAtivo(cacambaId).isEmpty();
    }

    @Transactional(readOnly = true)
    public Page<AluguelDTO> listar(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("dataInicio").descending());
        return aluguelRepository.findAll(pageable).map(this::toDtoWithDiasAtraso);
    }

    @Transactional(readOnly = true)
    public Page<AluguelDetalhadoDTO> listarFiltrado(FiltroAluguel filtro, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("dataInicio").descending());

        Specification<Aluguel> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and(AluguelSpecifications.statusEquals(filtro.getStatus()));
        spec = spec.and(AluguelSpecifications.clienteNomeLike(filtro.getClienteNome()));
        spec = spec.and(AluguelSpecifications.cacambaCodigoLike(filtro.getCacambaCodigo()));
        spec = spec.and(AluguelSpecifications.dataInicioBetween(filtro.getDataInicioDe(), filtro.getDataInicioAte()));
        spec = spec.and(AluguelSpecifications.dataFimBetween(filtro.getDataFimDe(), filtro.getDataFimAte()));
        if (filtro.getComAtraso() != null && filtro.getComAtraso()) {
            spec = spec.and(AluguelSpecifications.comAtraso());
        }
        spec = spec.and(AluguelSpecifications.textoLivre(filtro.getTexto()));

        if (filtro.getSituacao() != null) {
            switch (filtro.getSituacao()) {
                case VENCE_HOJE -> spec = spec.and(AluguelSpecifications.venceHoje());
                case VENCIDOS -> spec = spec.and(AluguelSpecifications.vencidos());
                case PROXIMOS_7_DIAS -> spec = spec.and(AluguelSpecifications.proximos7Dias());
                default -> {
                }
            }
        }

        Page<Aluguel> pageResult = aluguelRepository.findAll(spec, pageable);

        // Mapear para detalhado com cálculo de dias
        return pageResult.map(a -> {
            AluguelDetalhadoDTO dto = aluguelMapper.toDetalhadoDto(a);
            LocalDate hoje = LocalDate.now();
            long diasRestantes = hoje.isAfter(a.getDataFim())
                ? -java.time.temporal.ChronoUnit.DAYS.between(a.getDataFim(), hoje)
                : java.time.temporal.ChronoUnit.DAYS.between(hoje, a.getDataFim());
            Integer diasAtraso = (a.getStatus() == StatusAluguel.ATIVO && hoje.isAfter(a.getDataFim()))
                ? (int) java.time.temporal.ChronoUnit.DAYS.between(a.getDataFim(), hoje)
                : null;
            return new AluguelDetalhadoDTO(
                dto.id(), dto.clienteNome(), dto.clienteContato(), dto.cacambaCodigo(), dto.cacambaCapacidade(),
                dto.endereco(), dto.dataInicio(), dto.dataFim(), dto.status(), (int) diasRestantes, diasAtraso
            );
        });
    }
    
    private AluguelDTO toDtoWithDiasAtraso(Aluguel aluguel) {
        AluguelDTO dto = aluguelMapper.toDto(aluguel);
        
        // Calcular dias de atraso se o aluguel estiver ativo e vencido
        Integer diasAtraso = null;
        if (aluguel.getStatus() == StatusAluguel.ATIVO) {
            LocalDate hoje = LocalDate.now();
            if (hoje.isAfter(aluguel.getDataFim())) {
                diasAtraso = (int) java.time.temporal.ChronoUnit.DAYS.between(aluguel.getDataFim(), hoje);
            }
        }
        
        return new AluguelDTO(
            dto.id(), dto.clienteId(), dto.cacambaId(), dto.endereco(),
            dto.dataInicio(), dto.dataFim(), dto.status(), diasAtraso
        );
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
        
        // Calcular dias restantes: negativo para vencido, 0 para hoje, positivo para futuro
        LocalDate hoje = LocalDate.now();
        long diasRestantes;
        if (hoje.isAfter(aluguel.getDataFim())) {
            diasRestantes = -java.time.temporal.ChronoUnit.DAYS.between(aluguel.getDataFim(), hoje);
        } else {
            diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoje, aluguel.getDataFim());
        }
        
        // Calcular dias de atraso
        Integer diasAtraso = null;
        if (aluguel.getStatus() == StatusAluguel.ATIVO && hoje.isAfter(aluguel.getDataFim())) {
            diasAtraso = (int) java.time.temporal.ChronoUnit.DAYS.between(aluguel.getDataFim(), hoje);
        }
        
        return new AluguelDetalhadoDTO(
            dto.id(), dto.clienteNome(), dto.clienteContato(), dto.cacambaCodigo(), 
            dto.cacambaCapacidade(), dto.endereco(), dto.dataInicio(), dto.dataFim(), 
            dto.status(), (int) diasRestantes, diasAtraso
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
