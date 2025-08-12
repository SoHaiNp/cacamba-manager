package com.eccolimp.cacamba_manager.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.dto.AlertasVencimentoDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final AluguelService aluguelService;
    private final AluguelRepository aluguelRepository;

    @Value("${app.notification.email.report-to}")
    private String reportToEmailDefault;

    private final com.eccolimp.cacamba_manager.domain.service.SystemSettingsService systemSettingsService;

    /**
     * Envia notificações de vencimento automaticamente
     * Cron e fuso configuráveis por propriedade.
     */
    @Transactional
    public void enviarNotificacoesVencimento() {
        log.info("Iniciando envio de notificações de vencimento...");
        boolean enabled = true;
        try { enabled = systemSettingsService.getOrCreateDefaults().isNotificationsEnabled(); } catch (Exception ignored) {}
        log.info("Notificações habilitadas (toggle): {}", enabled);
        
        try {
            AlertasVencimentoDTO alertas = aluguelService.buscarAlertasVencimento();
            
            java.util.List<String> recipientsHoje = new java.util.ArrayList<>();
            java.util.List<String> recipientsAmanha = new java.util.ArrayList<>();
            java.util.List<String> recipientsProximos = new java.util.ArrayList<>();

            // Notificar aluguéis vencendo hoje
            if (alertas.temVencendoHoje()) {
                for (var aluguelDTO : alertas.vencendoHoje()) {
                    Aluguel aluguel = buscarAluguelCompleto(aluguelDTO.id());
                    var nome = aluguel.getCliente().getNome();
                    var email = aluguel.getCliente().getEmail();
                    emailService.enviarNotificacaoVencimento(aluguel, 0);
                    recipientsHoje.add(nome + " <" + email + ">");
                }
                log.info("Vencendo HOJE: {} destinatários -> {}", recipientsHoje.size(), String.join(", ", recipientsHoje));
            }
            
            // Notificar aluguéis vencendo amanhã
            if (alertas.temVencendoAmanha()) {
                for (var aluguelDTO : alertas.vencendoAmanha()) {
                    Aluguel aluguel = buscarAluguelCompleto(aluguelDTO.id());
                    var nome = aluguel.getCliente().getNome();
                    var email = aluguel.getCliente().getEmail();
                    emailService.enviarNotificacaoVencimento(aluguel, 1);
                    recipientsAmanha.add(nome + " <" + email + ">");
                }
                log.info("Vencendo AMANHÃ: {} destinatários -> {}", recipientsAmanha.size(), String.join(", ", recipientsAmanha));
            }
            
            // Notificar aluguéis vencendo nos próximos dias
            if (alertas.temVencendoProximosDias()) {
                for (var aluguelDTO : alertas.vencendoProximosDias()) {
                    Aluguel aluguel = buscarAluguelCompleto(aluguelDTO.id());
                    int diasRestantes = aluguelDTO.diasRestantes();
                    var nome = aluguel.getCliente().getNome();
                    var email = aluguel.getCliente().getEmail();
                    emailService.enviarNotificacaoVencimento(aluguel, diasRestantes);
                    recipientsProximos.add(nome + " <" + email + ">");
                }
                log.info("Vencendo PRÓXIMOS DIAS: {} destinatários -> {}", recipientsProximos.size(), String.join(", ", recipientsProximos));
            }
            
            log.info("Notificações de vencimento enviadas com sucesso");
            
        } catch (Exception e) {
            log.error("Erro ao enviar notificações de vencimento", e);
        }
    }

    /**
     * Envia confirmação de novo aluguel
     */
    public void enviarConfirmacaoNovoAluguel(Aluguel aluguel) {
        log.info("Enviando confirmação de novo aluguel #{}", aluguel.getId());
        emailService.enviarConfirmacaoAluguel(aluguel);
    }

    /**
     * Envia relatório semanal de aluguéis ativos
     * Cron e fuso configuráveis por propriedade.
     */
    @Transactional
    public void enviarRelatorioSemanal() {
        log.info("Iniciando envio de relatório semanal...");
        
        try {
            // Aqui você pode implementar a lógica para buscar dados do relatório
            // e enviar para um email configurado
            String emailDestino = resolveReportToEmail();
            log.info("Destino do relatório semanal: {}", emailDestino);
            
            // Dados do relatório (implementar conforme necessário)
            Map<String, Object> dadosRelatorio = gerarDadosRelatorioSemanal();
            
            emailService.enviarRelatorioSemanal(emailDestino, dadosRelatorio);
            log.info("Relatório semanal enviado com sucesso para {}", emailDestino);
            
        } catch (Exception e) {
            log.error("Erro ao enviar relatório semanal", e);
        }
    }

    /**
     * Método para teste manual de notificações
     */
    @Transactional
    public void testarNotificacao(String emailDestino) {
        log.info("Enviando email de teste para: {}", emailDestino);
        
        try {
            // Criar dados de teste
            Map<String, Object> dadosTeste = gerarDadosRelatorioSemanal();
            // Envia FORÇADO (teste deve funcionar mesmo com toggle OFF)
            emailService.enviarRelatorioSemanalForcado(emailDestino, dadosTeste);
            
            log.info("Email de teste enviado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao enviar email de teste", e);
            throw new RuntimeException("Falha no envio do email de teste", e);
        }
    }

    /**
     * Busca aluguel completo por ID
     */
    private Aluguel buscarAluguelCompleto(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado: " + id));
    }

    /**
     * Gera dados para relatório semanal
     */
    private Map<String, Object> gerarDadosRelatorioSemanal() {
        Map<String, Object> dados = new HashMap<>();
        
        // Dados básicos do relatório
        dados.put("dataRelatorio", LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dados.put("totalAlugueisAtivos", aluguelService.countAtivos());
        dados.put("alugueisVencendo", aluguelService.buscarAlertasVencimento().totalVencendo());
        
        return dados;
    }

    private String resolveReportToEmail() {
        try {
            var settings = systemSettingsService.getOrCreateDefaults();
            if (settings.getReportToEmail() != null && !settings.getReportToEmail().isBlank()) {
                return settings.getReportToEmail();
            }
        } catch (Exception ignored) {}
        return reportToEmailDefault;
    }
} 