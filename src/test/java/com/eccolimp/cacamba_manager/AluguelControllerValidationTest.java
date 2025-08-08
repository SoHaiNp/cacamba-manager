package com.eccolimp.cacamba_manager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.eccolimp.cacamba_manager.controller.api.AluguelController;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;

@WebMvcTest(AluguelController.class)
class AluguelControllerValidationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AluguelService aluguelService;

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        String json = """
                {
                  "clienteId": null,
                  "cacambaId": null,
                  "endereco": "",
                  "dataInicio": null,
                  "dias": 0
                }
                """;

        mockMvc.perform(post("/api/v1/alugueis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void deveRetornar400QuandoDiasMaiorQue10() throws Exception {
        String json = """
                {
                  "clienteId": 1,
                  "cacambaId": 1,
                  "endereco": "Rua Teste, 123",
                  "dataInicio": "2024-01-01",
                  "dias": 11
                }
                """;

        mockMvc.perform(post("/api/v1/alugueis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
