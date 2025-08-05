package com.eccolimp.cacamba_manager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.eccolimp.cacamba_manager.controller.api.CacambaController;
import com.eccolimp.cacamba_manager.domain.service.CacambaService;

@WebMvcTest(CacambaController.class)
class CacambaControllerValidationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CacambaService cacambaService;

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        String json = """
                {
                  \"codigo\": \"\",
                  \"capacidadeM3\": null,
                  \"status\": \"DISPONIVEL\"
                }
                """;

        mockMvc.perform(post("/api/v1/cacambas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
