package com.eccolimp.cacamba_manager.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.eccolimp.cacamba_manager.notification.service.NotificationService;
import com.eccolimp.cacamba_manager.notification.service.EmailService;

public class NotificationFlowTest {

    @Test
    void testeEnvioForcadoNaoDependeDoToggle() {
        EmailService emailService = mock(EmailService.class);
        NotificationService notificationService = new NotificationService(
                emailService, null, null, null);

        // Quando
        notificationService.testarNotificacao("admin@empresa.com");

        // Então (qualquer chamada ao EmailService)
        verify(emailService, atLeastOnce()).enviarRelatorioSemanalForcado(eq("admin@empresa.com"), any(Map.class));
    }
}


