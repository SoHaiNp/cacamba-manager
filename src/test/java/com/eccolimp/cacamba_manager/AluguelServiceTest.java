package com.eccolimp.cacamba_manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.domain.repository.CacambaRepository;
import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
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

        AluguelDTO dto = service.registrar(new NovoAluguelRequest(cli.getId(), cac.getId(),
                                           "Rua X, 123", 3));

        assertThat(dto.dataFim()).isEqualTo(LocalDate.now().plusDays(3));
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
            service.registrar(new NovoAluguelRequest(clienteSalvo.getId(), cacambaSalva.getId(), "Rua X, 123", 3))
        ).isInstanceOf(BusinessException.class)
         .hasMessage("Caçamba indisponível");
    }
}
