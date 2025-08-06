package com.eccolimp.cacamba_manager.notification;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.dto.AlertasVencimentoDTO;
import com.eccolimp.cacamba_manager.dto.AluguelVencendoDTO;
import com.eccolimp.cacamba_manager.notification.service.EmailService;
import com.eccolimp.cacamba_manager.notification.service.NotificationService;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = "app.notification.email.report-to=relatorio@test.com")
class NotificationServiceTest {

    @Autowired
    NotificationService notificationService;

    @MockBean
    EmailService emailService;

    @MockBean
    AluguelService aluguelService;

    @MockBean
    AluguelRepository aluguelRepository;

    @Test
    void deveEnviarNotificacoesVencimento() {
        Aluguel aluguelHoje = criarAluguel(1L, "a@b.com");
        Aluguel aluguelAmanha = criarAluguel(2L, "b@b.com");
        Aluguel aluguelProx = criarAluguel(3L, "c@b.com");

        AluguelVencendoDTO dtoHoje = new AluguelVencendoDTO(1L, "", "", LocalDate.now(), 0, "HOJE");
        AluguelVencendoDTO dtoAmanha = new AluguelVencendoDTO(2L, "", "", LocalDate.now().plusDays(1), 1, "AMANHA");
        AluguelVencendoDTO dtoProx = new AluguelVencendoDTO(3L, "", "", LocalDate.now().plusDays(3), 3, "PROXIMOS_DIAS");

        AlertasVencimentoDTO alertas = new AlertasVencimentoDTO(List.of(dtoHoje), List.of(dtoAmanha), List.of(dtoProx), 3);

        when(aluguelService.buscarAlertasVencimento()).thenReturn(alertas);
        when(aluguelRepository.findById(1L)).thenReturn(Optional.of(aluguelHoje));
        when(aluguelRepository.findById(2L)).thenReturn(Optional.of(aluguelAmanha));
        when(aluguelRepository.findById(3L)).thenReturn(Optional.of(aluguelProx));

        notificationService.enviarNotificacoesVencimento();

        verify(emailService).enviarNotificacaoVencimento(aluguelHoje, 0);
        verify(emailService).enviarNotificacaoVencimento(aluguelAmanha, 1);
        verify(emailService).enviarNotificacaoVencimento(aluguelProx, 3);
    }

    @Test
    void deveEnviarRelatorioSemanal() {
        when(aluguelService.countAtivos()).thenReturn(5L);
        AlertasVencimentoDTO alertas = new AlertasVencimentoDTO(List.of(), List.of(), List.of(), 2);
        when(aluguelService.buscarAlertasVencimento()).thenReturn(alertas);

        notificationService.enviarRelatorioSemanal();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(emailService).enviarRelatorioSemanal(eq("relatorio@test.com"), captor.capture());

        Map<String, Object> dados = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(5L, dados.get("totalAlugueisAtivos"));
        org.junit.jupiter.api.Assertions.assertEquals(2, dados.get("alugueisVencendo"));
        org.junit.jupiter.api.Assertions.assertNotNull(dados.get("dataRelatorio"));
    }

    @Test
    void deveLogarErroAoEnviarNotificacoesVencimento(CapturedOutput output) {
        when(aluguelService.buscarAlertasVencimento()).thenThrow(new RuntimeException("falha"));

        notificationService.enviarNotificacoesVencimento();

        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("Erro ao enviar notificações de vencimento"));
    }

    @Test
    void deveLogarErroAoEnviarRelatorioSemanal(CapturedOutput output) {
        when(aluguelService.countAtivos()).thenReturn(0L);
        when(aluguelService.buscarAlertasVencimento()).thenReturn(new AlertasVencimentoDTO(List.of(), List.of(), List.of(), 0));
        doThrow(new RuntimeException("erro")).when(emailService).enviarRelatorioSemanal(anyString(), anyMap());

        notificationService.enviarRelatorioSemanal();

        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("Erro ao enviar relatório semanal"));
    }

    private Aluguel criarAluguel(Long id, String email) {
        Cliente cliente = new Cliente();
        cliente.setNome("Fulano");
        cliente.setContato(email);

        Cacamba cacamba = new Cacamba();
        cacamba.setCodigo("CX-" + id);
        cacamba.setCapacidadeM3(5);

        Aluguel aluguel = new Aluguel();
        aluguel.setId(id);
        aluguel.setCliente(cliente);
        aluguel.setCacamba(cacamba);
        aluguel.setEndereco("Rua " + id);
        aluguel.setDataInicio(LocalDate.now());
        aluguel.setDataFim(LocalDate.now().plusDays(5));
        return aluguel;
    }
}

