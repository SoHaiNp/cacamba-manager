package com.eccolimp.cacamba_manager;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.Cacamba;
import com.eccolimp.cacamba_manager.domain.model.Cliente;
import com.eccolimp.cacamba_manager.notification.service.EmailService;

import jakarta.mail.internet.MimeMessage;

public class EmailServiceTest {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        emailService = new EmailService(mailSender, templateEngine);
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        ReflectionTestUtils.setField(emailService, "fromEmail", "no-reply@exemplo.com");
        ReflectionTestUtils.setField(emailService, "fromName", "Sistema");

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("html");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));
    }

    @Test
    void deveEnviarEmailQuandoEnderecoValido() throws Exception {
        Aluguel aluguel = criarAluguel("cliente@exemplo.com");

        emailService.enviarNotificacaoVencimento(aluguel, 3);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void naoDeveEnviarEmailQuandoEnderecoInvalido() throws Exception {
        Aluguel aluguel = criarAluguel("email-invalido");

        emailService.enviarNotificacaoVencimento(aluguel, 3);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    private Aluguel criarAluguel(String email) {
        Cliente cliente = new Cliente();
        cliente.setNome("Fulano");
        cliente.setContato(email);

        Cacamba cacamba = new Cacamba();
        cacamba.setCodigo("CX-1");
        cacamba.setCapacidadeM3(5);

        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setCliente(cliente);
        aluguel.setCacamba(cacamba);
        aluguel.setEndereco("Rua X, 123");
        aluguel.setDataInicio(LocalDate.now());
        aluguel.setDataFim(LocalDate.now().plusDays(1));
        return aluguel;
    }
}
