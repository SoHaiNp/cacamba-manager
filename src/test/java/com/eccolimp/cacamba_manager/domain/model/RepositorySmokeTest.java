package com.eccolimp.cacamba_manager.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.domain.repository.ClienteRepository;

@SpringBootTest
public class RepositorySmokeTest {

    @Autowired ClienteRepository clienteRepository;

    @Test
    void contextLoads_andPersists() {
        Cliente c = new Cliente();
        c.setNome("João da Silva");
        c.setContato("11999999999");
        clienteRepository.save(c);
        assertThat(clienteRepository.findAll()).hasSize(1);
    }

}
