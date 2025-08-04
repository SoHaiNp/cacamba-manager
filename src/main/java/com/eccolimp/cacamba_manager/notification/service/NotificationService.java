package com.eccolimp.cacamba_manager.notification.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.dto.AlertasVencimentoDTO;
import com.eccolimp.cacamba_manager.dto.AluguelVencendoDTO;

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

    /**
     * Envia notificações de vencimento automaticamente
     * Executa todos os dias às 8:00 da manhã
     */
    @Scheduled(cron = "0 0 8 * * ?") // Todos os dias às 8:00
    @Transactional
    public void enviarNotificacoesVencimento() {
        log.info("Iniciando envio de notificações de vencimento...");
        
        try {
            AlertasVencimentoDTO alertas = aluguelService.buscarAlertasVencimento();
            
            // Notificar aluguéis vencendo hoje
            if (alertas.temVencendoHoje()) {
                for (var aluguelDTO : alertas.vencendoHoje()) {
                    Aluguel aluguel = buscarAluguelCompleto(aluguelDTO.id());
                    emailService.enviarNotificacaoVencimento(aluguel, 0);
                }
            }
            
            // Notificar aluguéis vencendo amanhã
            if (alertas.temVencendoAmanha()) {
                for (var aluguelDTO : alertas.vencendoAmanha()) {
                    Aluguel aluguel = buscarAluguelCompleto(aluguelDTO.id());
                    emailService.enviarNotificacaoVencimento(aluguel, 1);
                }
            }
            
            // Notificar aluguéis vencendo nos próximos dias
            if (alertas.temVencendoProximosDias()) {
                for (var aluguelDTO : alertas.vencendoProximosDias()) {
                    Aluguel aluguel = buscarAluguelCompleto(aluguelDTO.id());
                    int diasRestantes = aluguelDTO.diasRestantes();
                    emailService.enviarNotificacaoVencimento(aluguel, diasRestantes);
                }
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
     * Executa toda segunda-feira às 9:00
     */
    @Scheduled(cron = "0 0 9 ? * MON") // Toda segunda às 9:00
    @Transactional
    public void enviarRelatorioSemanal() {
        log.info("Iniciando envio de relatório semanal...");
        
        try {
            // Aqui você pode implementar a lógica para buscar dados do relatório
            // e enviar para um email específico (ex: gerente@empresa.com)
            String emailDestino = "minhacacambaon@gmail.com"; // Configurável
            
            // Dados do relatório (implementar conforme necessário)
            Map<String, Object> dadosRelatorio = gerarDadosRelatorioSemanal();
            
            emailService.enviarRelatorioSemanal(emailDestino, dadosRelatorio);
            log.info("Relatório semanal enviado com sucesso");
            
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
            emailService.enviarRelatorioSemanal(emailDestino, dadosTeste);
            
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
} 