package com.eccolimp.cacamba_manager.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.model.Cliente;

import org.apache.commons.validator.routines.EmailValidator;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final com.eccolimp.cacamba_manager.domain.service.SystemSettingsService systemSettingsService;
    private final EmailValidator emailValidator = EmailValidator.getInstance();

    @Value("${app.notification.email.enabled:true}")
    private boolean emailEnabledDefault;

    @Value("${app.notification.email.from}")
    private String fromEmailDefault;

    @Value("${app.notification.email.from-name}")
    private String fromNameDefault;

    /**
     * Envia notificação de aluguel vencendo
     */
    public void enviarNotificacaoVencimento(Aluguel aluguel, int diasRestantes) {
        if (!isNotificationsEnabled()) {
            log.info("Notificações por email desabilitadas");
            return;
        }

        try {
            Cliente cliente = aluguel.getCliente();
            String to = cliente.getEmail();
            if (!cliente.isRecebeNotificacoes()) {
                log.info("Cliente {} desativado para notificações. Pulando envio.", cliente.getNome());
                return;
            }
            
            if (!emailValidator.isValid(to)) {
                log.warn("Email inválido para cliente {}: {}", cliente.getNome(), to);
                return;
            }

            String subject = getSubjectVencimento(diasRestantes);
            String htmlContent = gerarTemplateVencimento(aluguel, diasRestantes);

            enviarEmail(to, subject, htmlContent);
            log.info("Notificação de vencimento enviada para {} - Aluguel #{}", to, aluguel.getId());

        } catch (Exception e) {
            log.error("Erro ao enviar notificação de vencimento para aluguel #{}", aluguel.getId(), e);
        }
    }

    /**
     * Envia confirmação de novo aluguel
     */
    public void enviarConfirmacaoAluguel(Aluguel aluguel) {
        if (!isNotificationsEnabled()) {
            log.info("Notificações por email desabilitadas");
            return;
        }

        try {
            Cliente cliente = aluguel.getCliente();
            String to = cliente.getEmail();
            if (!cliente.isRecebeNotificacoes()) {
                log.info("Cliente {} desativado para notificações. Pulando envio.", cliente.getNome());
                return;
            }
            
            if (!emailValidator.isValid(to)) {
                log.warn("Email inválido para cliente {}: {}", cliente.getNome(), to);
                return;
            }

            String subject = "Confirmação de Aluguel - Caçamba #" + aluguel.getCacamba().getCodigo();
            String htmlContent = gerarTemplateConfirmacao(aluguel);

            enviarEmail(to, subject, htmlContent);
            log.info("Confirmação de aluguel enviada para {} - Aluguel #{}", to, aluguel.getId());

        } catch (Exception e) {
            log.error("Erro ao enviar confirmação de aluguel #{}", aluguel.getId(), e);
        }
    }

    /**
     * Envia relatório semanal de aluguéis ativos
     */
    public void enviarRelatorioSemanal(String to, Map<String, Object> dados) {
        if (!isNotificationsEnabled()) {
            log.info("Notificações por email desabilitadas");
            return;
        }

        try {
            String subject = "Relatório Semanal - Aluguéis Ativos";
            String htmlContent = gerarTemplateRelatorioSemanal(dados);

            enviarEmail(to, subject, htmlContent);
            log.info("Relatório semanal enviado para {}", to);

        } catch (Exception e) {
            log.error("Erro ao enviar relatório semanal para {}", to, e);
        }
    }

    /**
     * Envia relatório semanal ignorando o toggle de notificações (uso: teste manual)
     */
    public void enviarRelatorioSemanalForcado(String to, Map<String, Object> dados) {
        try {
            String subject = "Relatório Semanal - Aluguéis Ativos";
            String htmlContent = gerarTemplateRelatorioSemanal(dados);

            enviarEmail(to, subject, htmlContent);
            log.info("[FORÇADO] Relatório semanal enviado para {}", to);

        } catch (Exception e) {
            log.error("Erro ao enviar relatório semanal (forçado) para {}", to, e);
        }
    }

    private void enviarEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            var settings = systemSettingsService.getOrCreateDefaults();
            String fromEmail = (settings.getFromEmail() != null && !settings.getFromEmail().isBlank()) ? settings.getFromEmail() : fromEmailDefault;
            String fromName = (settings.getFromName() != null && !settings.getFromName().isBlank()) ? settings.getFromName() : fromNameDefault;
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
        } catch (java.io.UnsupportedEncodingException e) {
            log.error("Erro de encoding ao enviar email para {}", to, e);
            throw new MessagingException("Erro de encoding: " + e.getMessage(), e);
        }
    }

    private boolean isNotificationsEnabled() {
        try {
            var settings = systemSettingsService.getOrCreateDefaults();
            return settings.isNotificationsEnabled();
        } catch (Exception e) {
            // fallback para property
            return emailEnabledDefault;
        }
    }

    private String getSubjectVencimento(int diasRestantes) {
        if (diasRestantes == 0) {
            return "URGENTE: Seu aluguel de caçamba vence HOJE!";
        } else if (diasRestantes == 1) {
            return "ATENÇÃO: Seu aluguel de caçamba vence AMANHÃ!";
        } else {
            return "Lembrete: Seu aluguel de caçamba vence em " + diasRestantes + " dias";
        }
    }

    private String gerarTemplateVencimento(Aluguel aluguel, int diasRestantes) {
        Context context = new Context();
        context.setVariable("cliente", aluguel.getCliente());
        context.setVariable("aluguel", aluguel);
        context.setVariable("cacamba", aluguel.getCacamba());
        context.setVariable("diasRestantes", diasRestantes);
        context.setVariable("dataVencimento", aluguel.getDataFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        context.setVariable("urgencia", diasRestantes <= 1 ? "URGENTE" : "ATENÇÃO");

        return templateEngine.process("email/vencimento", context);
    }

    private String gerarTemplateConfirmacao(Aluguel aluguel) {
        Context context = new Context();
        context.setVariable("cliente", aluguel.getCliente());
        context.setVariable("aluguel", aluguel);
        context.setVariable("cacamba", aluguel.getCacamba());
        context.setVariable("dataInicio", aluguel.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        context.setVariable("dataFim", aluguel.getDataFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return templateEngine.process("email/confirmacao", context);
    }

    private String gerarTemplateRelatorioSemanal(Map<String, Object> dados) {
        Context context = new Context();
        context.setVariable("dados", dados);
        return templateEngine.process("email/relatorio-semanal", context);
    }

}