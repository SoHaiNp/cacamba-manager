package com.eccolimp.cacamba_manager.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eccolimp.cacamba_manager.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Endpoint para testar o envio de email
     */
    @PostMapping("/test")
    public ResponseEntity<String> testarNotificacao(@RequestParam String email) {
        log.info("Testando envio de notificação para: {}", email);
        notificationService.testarNotificacao(email);
        return ResponseEntity.ok("Email de teste enviado com sucesso para: " + email);
    }

    /**
     * Endpoint para forçar envio de notificações de vencimento
     */
    @PostMapping("/vencimento/forcar")
    public ResponseEntity<String> forcarNotificacoesVencimento() {
        log.info("Forçando envio de notificações de vencimento");
        notificationService.enviarNotificacoesVencimento();
        return ResponseEntity.ok("Notificações de vencimento enviadas com sucesso");
    }

    /**
     * Endpoint para forçar envio de relatório semanal
     */
    @PostMapping("/relatorio/forcar")
    public ResponseEntity<String> forcarRelatorioSemanal() {
        log.info("Forçando envio de relatório semanal");
        notificationService.enviarRelatorioSemanal();
        return ResponseEntity.ok("Relatório semanal enviado com sucesso");
    }
}
