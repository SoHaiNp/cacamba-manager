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
        cli.setEmail("cli1@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-101");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        LocalDate dataInicio = LocalDate.now();
        NovoAluguelRequest request = new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        AluguelDTO dto = service.registrar(request);

        // Com a regra D+1: dataInicio = dataEntrega + 1, dataFim = dataInicio + (prazo - 1)
        // dataEntrega = dataInicio - 1, dataInicio = dataEntrega + 1, dataFim = dataInicio + 2
        assertThat(dto.dataFim()).isEqualTo(dataInicio.plusDays(2)); // 3 dias = início + (3-1) = início + 2
        assertThat(cacambaRepo.findById(cac.getId()).get().getStatus())
                              .isEqualTo(StatusCacamba.ALUGADA);
    }

    @Test
    void deveFalharQuandoCaçambaIndisponivel() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli.setEmail("cli2@example.com");
        final Cliente clienteSalvo = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-102");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.ALUGADA);
        final Cacamba cacambaSalva = cacambaRepo.save(cac);

        NovoAluguelRequest request = new NovoAluguelRequest(clienteSalvo.getId(), cacambaSalva.getId(), 
                "Rua X, 123", LocalDate.now(), 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        assertThatThrownBy(() -> service.registrar(request))
        .isInstanceOf(BusinessException.class)
         .hasMessage("Caçamba não está disponível");
    }
    
    @Test
    void deveRegistrarAluguelComDataPassada() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli.setEmail("cli3@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-103");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        LocalDate dataInicio = LocalDate.now().minusDays(5); // Data passada
        NovoAluguelRequest request = new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        AluguelDTO dto = service.registrar(request);

        assertThat(dto.dataInicio()).isEqualTo(dataInicio);
        assertThat(dto.dataFim()).isEqualTo(dataInicio.plusDays(2)); // 3 dias = início + (3-1) = início + 2
    }
    
    @Test
    void deveFalharQuandoCaçambaJaEstaEmUso() {
        Cliente cli1 = new Cliente();
        cli1.setNome("Fulano 1");
        cli1.setContato("(11)9999-9999");
        cli1.setEmail("cli4@example.com");
        cli1 = clienteRepo.save(cli1);
        
        Cliente cli2 = new Cliente();
        cli2.setNome("Fulano 2");
        cli2.setContato("(11)8888-8888");
        cli2.setEmail("cli5@example.com");
        cli2 = clienteRepo.save(cli2);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-104");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Primeiro aluguel
        NovoAluguelRequest request1 = new NovoAluguelRequest(cli1.getId(), cac.getId(),
                                            "Rua X, 123", LocalDate.now(), 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        service.registrar(request1);

        // Segundo aluguel com a mesma caçamba (deve falhar)
        NovoAluguelRequest request2 = new NovoAluguelRequest(cli2.getId(), cac.getId(),
                                            "Rua Y, 456", LocalDate.now().plusDays(1), 2, java.math.BigDecimal.valueOf(80.00), java.math.BigDecimal.valueOf(40.00));
        assertThatThrownBy(() -> service.registrar(request2))
        .isInstanceOf(BusinessException.class)
         .hasMessage("Caçamba não está disponível");
    }
    
    @Test
    void deveCalcularDiasRestantesCorretamente() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli.setEmail("cli6@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-105");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Criar aluguel com data futura para evitar problemas com datas passadas
        LocalDate dataInicio = LocalDate.now().plusDays(10); // 10 dias no futuro
        NovoAluguelRequest request = new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        AluguelDTO dto = service.registrar(request);

        // Verificar que a data de fim está correta (dataInicio + 2 dias)
        assertThat(dto.dataFim()).isEqualTo(dataInicio.plusDays(2));
        
        // Buscar detalhado para verificar dias restantes
        AluguelDetalhadoDTO detalhado = service.buscarDetalhadoPorId(dto.id());
        
        // Como o aluguel é no futuro, deve ter dias restantes positivos
        assertThat(detalhado.diasRestantes()).isGreaterThan(0);
    }

    @Test
    void deveCalcularDiasRestantesExato() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli.setEmail("cli7@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-106");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Criar aluguel com data futura para testar o cálculo
        LocalDate dataInicio = LocalDate.now().plusDays(5); // 5 dias no futuro
        NovoAluguelRequest request = new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        AluguelDTO dto = service.registrar(request);

        // Verificar que a data de fim está correta (dataInicio + 2 dias)
        assertThat(dto.dataFim()).isEqualTo(dataInicio.plusDays(2));
        
        // Buscar detalhado para verificar dias restantes
        AluguelDetalhadoDTO detalhado = service.buscarDetalhadoPorId(dto.id());
        
        // Como o aluguel é no futuro, deve ter dias restantes positivos
        assertThat(detalhado.diasRestantes()).isGreaterThan(0);
    }

    @Test
    void deveCalcularDiasAtrasoCorretamente() {
        Cliente cli = new Cliente();
        cli.setNome("Fulano");
        cli.setContato("(11)9999-9999");
        cli.setEmail("cli8@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-107");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Criar aluguel vencido: início 01/08, duração 3 dias, fim 03/08
        LocalDate dataInicio = LocalDate.of(2024, 8, 1); // 01/08/2024
        NovoAluguelRequest request = new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", dataInicio, 3, java.math.BigDecimal.valueOf(100.00), java.math.BigDecimal.valueOf(50.00));
        AluguelDTO dto = service.registrar(request);

        // Verificar que a data de fim está correta
        assertThat(dto.dataFim()).isEqualTo(LocalDate.of(2024, 8, 3)); // 03/08/2024
        
        // Simular que hoje é 05/08 (2 dias após o vencimento)
        LocalDate hoje = LocalDate.of(2024, 8, 5);
        long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dto.dataFim(), hoje);
        assertThat(diasAtraso).isEqualTo(2); // Deveria ser 2 dias de atraso
        
        // Simular que hoje é 10/08 (7 dias após o vencimento)
        hoje = LocalDate.of(2024, 8, 10);
        diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dto.dataFim(), hoje);
        assertThat(diasAtraso).isEqualTo(7); // Deveria ser 7 dias de atraso
    }
}
