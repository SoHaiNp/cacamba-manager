package com.eccolimp.cacamba_manager.notification.service;

import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.dto.AlertasVencimentoDTO;
import com.eccolimp.cacamba_manager.dto.AluguelVencendoDTO;
import com.eccolimp.cacamba_manager.notification.model.Notification;
import com.eccolimp.cacamba_manager.notification.model.NotificationType;
import com.eccolimp.cacamba_manager.notification.repository.NotificationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationInboxService {

    private final NotificationRepository notificationRepository;
    private final AluguelService aluguelService;

    @Transactional
    public List<Notification> refreshDailyAlerts() {
        AlertasVencimentoDTO alertas = aluguelService.buscarAlertasVencimento();
        List<Notification> toSave = new ArrayList<>();

        for (AluguelVencendoDTO a : alertas.vencendoHoje()) {
            String title = "Vence HOJE";
            String msg = String.format("Aluguel #%d de %s (caçamba %s) vence hoje.", a.id(), a.clienteNome(), a.cacambaCodigo());
            toSave.add(new Notification(NotificationType.VENCIMENTO_HOJE, title, msg, a.id()));
        }

        for (AluguelVencendoDTO a : alertas.vencendoAmanha()) {
            String title = "Vence AMANHÃ";
            String msg = String.format("Aluguel #%d de %s (caçamba %s) vence amanhã.", a.id(), a.clienteNome(), a.cacambaCodigo());
            toSave.add(new Notification(NotificationType.VENCIMENTO_AMANHA, title, msg, a.id()));
        }

        for (AluguelVencendoDTO a : alertas.vencendoProximosDias()) {
            String title = "Vencimento em breve";
            String msg = String.format("Aluguel #%d de %s (caçamba %s) vence em %d dias.", a.id(), a.clienteNome(), a.cacambaCodigo(), a.diasRestantes());
            toSave.add(new Notification(NotificationType.VENCIMENTO_PROXIMOS_DIAS, title, msg, a.id()));
        }

        // Estratégia simples: limpa notificações do dia e recria (mantém leiturabilidade para dias diferentes)
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        List<Notification> today = notificationRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        notificationRepository.deleteAll(today);

        return notificationRepository.saveAll(toSave);
    }

    public long countUnread() {
        return notificationRepository.countByReadFlagFalse();
    }

    public List<Notification> latest() {
        return notificationRepository.findTop50ByOrderByCreatedAtDesc();
    }

    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }

    @Transactional
    public void markOneAsRead(Long id) {
        notificationRepository.markOneAsRead(id);
    }
}


