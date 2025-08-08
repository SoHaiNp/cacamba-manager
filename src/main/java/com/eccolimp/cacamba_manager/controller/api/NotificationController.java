package com.eccolimp.cacamba_manager.controller.api;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
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
    private final TaskScheduler taskScheduler;

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

    /**
     * Agenda uma execução única para o envio de notificações de vencimento.
     * Ex.: POST /api/v1/notifications/agendar?v=2025-08-08T22:30:00&zone=America/Sao_Paulo
     */
    @PostMapping("/agendar")
    public ResponseEntity<String> agendar(@RequestParam("v") String dateTimeIso,
                                          @RequestParam(value = "zone", defaultValue = "America/Sao_Paulo") String zone) {
        ZonedDateTime zdt;
        try {
            // Tenta interpretar com timezone no próprio parâmetro
            zdt = ZonedDateTime.parse(dateTimeIso).withZoneSameInstant(ZoneId.of(zone));
        } catch (DateTimeParseException ex) {
            // Fallback: interpreta como LocalDateTime no fuso informado
            LocalDateTime ldt = LocalDateTime.parse(dateTimeIso);
            zdt = ldt.atZone(ZoneId.of(zone));
        }
        Date when = Date.from(Instant.from(zdt));
        // Usa a sobrecarga com Date (pode gerar warning de depreciação, mas é a forma simples para uma execução única)
        taskScheduler.schedule(notificationService::enviarNotificacoesVencimento, when);
        return ResponseEntity.ok("Agendado para " + zdt.toString());
    }
}
