package com.eccolimp.cacamba_manager;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
public class RegraDmais1Test {

    @Autowired AluguelService service;
    @Autowired ClienteRepository clienteRepo;
    @Autowired CacambaRepository cacambaRepo;

    @Test
    void deveAplicarRegraDmais1Corretamente() {
        // Preparar dados
        Cliente cli = new Cliente();
        cli.setNome("Teste Regra D+1");
        cli.setContato("(11)9999-9999");
        cli.setEmail("teste@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-TESTE");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Testar com data específica: 27/08/2025 e 7 dias
        LocalDate dataInicioInformada = LocalDate.of(2025, 8, 27); // 27/08/2025
        int dias = 7;
        
        NovoAluguelRequest request = new NovoAluguelRequest(
            cli.getId(), 
            cac.getId(),
            "Rua Teste, 123", 
            dataInicioInformada, 
            dias, 
            java.math.BigDecimal.valueOf(100.00), 
            java.math.BigDecimal.valueOf(50.00)
        );
        
        // Executar
        AluguelDTO dto = service.registrar(request);
        
        // Verificar regra D+1
        // dataEntrega = dataInicio - 1 = 26/08/2025
        // dataInicio = dataEntrega + 1 = 27/08/2025  
        // dataFim = dataInicio + (prazo - 1) = 27/08 + (7-1) = 27/08 + 6 = 02/09/2025
        
        assertThat(dto.dataInicio()).isEqualTo(LocalDate.of(2025, 8, 27)); // 27/08/2025
        assertThat(dto.dataFim()).isEqualTo(LocalDate.of(2025, 9, 2));     // 02/09/2025
        
        // Verificar que a duração está correta (7 dias)
        long duracaoCalculada = java.time.temporal.ChronoUnit.DAYS.between(
            dto.dataInicio(), dto.dataFim()) + 1; // +1 porque é inclusivo
        assertThat(duracaoCalculada).isEqualTo(7);
        
        System.out.println("✅ Regra D+1 funcionando:");
        System.out.println("   Data Início informada: " + dataInicioInformada);
        System.out.println("   Dias informados: " + dias);
        System.out.println("   Data Início calculada: " + dto.dataInicio());
        System.out.println("   Data Fim calculada: " + dto.dataFim());
        System.out.println("   Duração: " + duracaoCalculada + " dias");
    }
    
    @Test
    void deveAplicarRegraDmais1Com3Dias() {
        // Preparar dados
        Cliente cli = new Cliente();
        cli.setNome("Teste Regra D+1 - 3 dias");
        cli.setContato("(11)8888-8888");
        cli.setEmail("teste3@example.com");
        cli = clienteRepo.save(cli);
        
        Cacamba cac = new Cacamba();
        cac.setCodigo("CX-TESTE-3");
        cac.setCapacidadeM3(5);
        cac.setStatus(StatusCacamba.DISPONIVEL);
        cac = cacambaRepo.save(cac);

        // Testar com 3 dias
        LocalDate dataInicioInformada = LocalDate.of(2025, 8, 27); // 27/08/2025
        int dias = 3;
        
        NovoAluguelRequest request = new NovoAluguelRequest(
            cli.getId(), 
            cac.getId(),
            "Rua Teste 3, 123", 
            dataInicioInformada, 
            dias, 
            java.math.BigDecimal.valueOf(100.00), 
            java.math.BigDecimal.valueOf(50.00)
        );
        
        // Executar
        AluguelDTO dto = service.registrar(request);
        
        // Verificar regra D+1 com 3 dias
        // dataEntrega = dataInicio - 1 = 26/08/2025
        // dataInicio = dataEntrega + 1 = 27/08/2025  
        // dataFim = dataInicio + (prazo - 1) = 27/08 + (3-1) = 27/08 + 2 = 29/08/2025
        
        assertThat(dto.dataInicio()).isEqualTo(LocalDate.of(2025, 8, 27)); // 27/08/2025
        assertThat(dto.dataFim()).isEqualTo(LocalDate.of(2025, 8, 29));     // 29/08/2025
        
        // Verificar que a duração está correta (3 dias)
        long duracaoCalculada = java.time.temporal.ChronoUnit.DAYS.between(
            dto.dataInicio(), dto.dataFim()) + 1; // +1 porque é inclusivo
        assertThat(duracaoCalculada).isEqualTo(3);
        
        System.out.println("✅ Regra D+1 com 3 dias funcionando:");
        System.out.println("   Data Início: " + dto.dataInicio());
        System.out.println("   Data Fim: " + dto.dataFim());
        System.out.println("   Duração: " + duracaoCalculada + " dias");
    }
}
