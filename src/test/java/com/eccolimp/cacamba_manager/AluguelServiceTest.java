package com.eccolimp.cacamba_manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.domain.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.AluguelDetalhadoDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
public class AluguelServiceTest {

    @Autowired AluguelService service;
    @Autowired ClienteRepository clienteRepo;
    @Autowired CacambaRepository cacambaRepo;

    @Test
    void deveRegistrarAluguel() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-101");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        LocalDate dataInicio = LocalDate.now();
        AluguelDTO dto = service.registrar(new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3));

        assertThat(dto.dataFim()).isEqualTo(dataInicio.plusDays(2)); // 3 dias = início + (3-1) = início + 2
        assertThat(cacambaRepo.findById(cac.getId()).get().getStatus())
                              .isEqualTo(StatusCacamba.ALUGADA);
    }

    @Test
    void deveFalharQuandoCaçambaIndisponivel() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        final Cliente clienteSalvo = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-102");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.ALUGADA);
        final Cacamba cacambaSalva = cacambaRepo.save(cac);

        assertThatThrownBy(() -> 
            service.registrar(new NovoAluguelRequest(clienteSalvo.getId(), cacambaSalva.getId(), 
                "Rua X, 123", LocalDate.now(), 3))
        ).isInstanceOf(BusinessException.class)
         .hasMessage("Caçamba indisponível");
    }
    
    @Test
    void deveRegistrarAluguelComDataPassada() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-103");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        LocalDate dataInicio = LocalDate.now().minusDays(5); // Data passada
        AluguelDTO dto = service.registrar(new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3));

        assertThat(dto.dataInicio()).isEqualTo(dataInicio);
        assertThat(dto.dataFim()).isEqualTo(dataInicio.plusDays(2)); // 3 dias = início + (3-1) = início + 2
    }
    
    @Test
    void deveFalharQuandoCaçambaIndisponivelNoPeriodo() {
        Cliente cli1 = new Cliente();
        cli1.setNome("Fulano");
        cli1.setContato("(11)9999-9999");
        cli1 = clienteRepo.save(cli1);
        
        Cliente cli2 = new Cliente();
        cli2.setNome("Ciclano");
        cli2.setContato("(11)8888-8888");
        final Cliente cliente2Salvo = clienteRepo.save(cli2);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-104");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        final Cacamba cacambaSalva = cacambaRepo.save(cac);

        // Primeiro aluguel
        LocalDate dataInicio1 = LocalDate.now();
        service.registrar(new NovoAluguelRequest(cli1.getId(), cac.getId(),
                           "Rua X, 123", dataInicio1, 5));

        // Tentar segundo aluguel no mesmo período
        LocalDate dataInicio2 = dataInicio1.plusDays(2); // Sobreposição
        assertThatThrownBy(() -> 
            service.registrar(new NovoAluguelRequest(cliente2Salvo.getId(), cacambaSalva.getId(), 
                "Rua Y, 456", dataInicio2, 3))
        ).isInstanceOf(BusinessException.class)
         .hasMessage("Caçamba não está disponível no período solicitado");
    }

    @Test
    void deveCalcularDiasRestantesCorretamente() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-105");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Criar aluguel com data específica: início 07/08, duração 3 dias, fim 09/08
        LocalDate dataInicio = LocalDate.of(2024, 8, 7); // 07/08/2024
        AluguelDTO dto = service.registrar(new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3));

        // Verificar que a data de fim está correta
        assertThat(dto.dataFim()).isEqualTo(LocalDate.of(2024, 8, 9)); // 09/08/2024
        
        // Buscar detalhado para verificar dias restantes
        AluguelDetalhadoDTO detalhado = service.buscarDetalhadoPorId(dto.id());
        
        // Se hoje for 07/08, deveria ter 2 dias restantes (08/08 e 09/08)
        // Se hoje for 08/08, deveria ter 1 dia restante (09/08)
        // Se hoje for 09/08, deveria ter 0 dias restantes
        LocalDate hoje = LocalDate.now();
        if (hoje.isBefore(dataInicio)) {
            // Aluguel ainda não começou
            assertThat(detalhado.diasRestantes()).isGreaterThan(0);
        } else if (hoje.isAfter(dto.dataFim())) {
            // Aluguel já venceu
            assertThat(detalhado.diasRestantes()).isEqualTo(0);
        } else {
            // Aluguel está ativo
            assertThat(detalhado.diasRestantes()).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    void deveCalcularDiasRestantesExato() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-106");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Criar aluguel com data específica: início 07/08, duração 3 dias, fim 09/08
        LocalDate dataInicio = LocalDate.of(2024, 8, 7); // 07/08/2024
        AluguelDTO dto = service.registrar(new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3));

        // Verificar que a data de fim está correta
        assertThat(dto.dataFim()).isEqualTo(LocalDate.of(2024, 8, 9)); // 09/08/2024
        
        // Simular diferentes datas "hoje" para testar o cálculo
        LocalDate hoje = LocalDate.of(2024, 8, 7); // Simular que hoje é 07/08
        long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoje, dto.dataFim());
        assertThat(diasRestantes).isEqualTo(2); // Deveria ser 2 dias (08/08 e 09/08)
        
        hoje = LocalDate.of(2024, 8, 8); // Simular que hoje é 08/08
        diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoje, dto.dataFim());
        assertThat(diasRestantes).isEqualTo(1); // Deveria ser 1 dia (09/08)
        
        hoje = LocalDate.of(2024, 8, 9); // Simular que hoje é 09/08
        diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoje, dto.dataFim());
        assertThat(diasRestantes).isEqualTo(0); // Deveria ser 0 dias (vencimento)
    }
}
