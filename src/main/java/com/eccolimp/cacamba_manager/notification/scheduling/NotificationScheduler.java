package com.eccolimp.cacamba_manager.notification.scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.eccolimp.cacamba_manager.domain.model.SystemSettings;
import com.eccolimp.cacamba_manager.domain.service.SystemSettingsService;
import com.eccolimp.cacamba_manager.notification.service.NotificationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;
    private final SystemSettingsService systemSettingsService;

    private final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();

    @PostConstruct
    public void init() {
        refreshSchedules();
    }

    public synchronized void refreshSchedules() {
        cancelAll();
        SystemSettings s = systemSettingsService.getOrCreateDefaults();

        // Fuso horário fixo em Brasília conforme solicitação
        TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");

        // Notificações de Vencimento (diário nos dias selecionados)
        String vencTime = s.getVencimentoTime() != null && s.getVencimentoTime().matches("\\d{2}:\\d{2}") ? s.getVencimentoTime() : "08:00";
        String vencCron = buildDailyCron(vencTime, buildDaysList(s));
        scheduleCron(vencCron, timeZone, () -> {
            if (!systemSettingsService.getOrCreateDefaults().isNotificationsEnabled()) {
                log.debug("Notificações desativadas: pulando execução de vencimentos");
                return;
            }
            notificationService.enviarNotificacoesVencimento();
        }, "vencimentos");

        // Relatório Semanal (nos dias selecionados)
        String relTime = s.getRelatorioTime() != null && s.getRelatorioTime().matches("\\d{2}:\\d{2}") ? s.getRelatorioTime() : "09:00";
        String relCron = buildDailyCron(relTime, buildDaysList(s));
        scheduleCron(relCron, timeZone, () -> {
            if (!systemSettingsService.getOrCreateDefaults().isNotificationsEnabled()) {
                log.debug("Notificações desativadas: pulando execução de relatório");
                return;
            }
            notificationService.enviarRelatorioSemanal();
        }, "relatorio");
    }

    private void cancelAll() {
        for (ScheduledFuture<?> f : scheduledTasks) {
            try { f.cancel(false); } catch (Exception ignored) {}
        }
        scheduledTasks.clear();
    }

    private void scheduleCron(String cron, TimeZone tz, Runnable task, String tag) {
        CronTrigger trigger = new CronTrigger(cron, tz);
        log.info("Agendando tarefa '{}' com cron '{}' e timezone '{}'", tag, cron, tz.getID());
        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);
        scheduledTasks.add(future);
    }

    private String buildDailyCron(String hhmm, String daysList) {
        String[] parts = hhmm.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        // Spring Cron: sec min hour day-of-month month day-of-week
        // Usamos '?' para dia do mês e definimos os dias da semana selecionados
        return String.format("0 %d %d ? * %s", minute, hour, daysList);
    }

    private String buildDaysList(SystemSettings s) {
        List<String> days = new ArrayList<>();
        if (s.isRunMon()) days.add("MON");
        if (s.isRunTue()) days.add("TUE");
        if (s.isRunWed()) days.add("WED");
        if (s.isRunThu()) days.add("THU");
        if (s.isRunFri()) days.add("FRI");
        if (s.isRunSat()) days.add("SAT");
        if (s.isRunSun()) days.add("SUN");
        if (days.isEmpty()) {
            days.add("MON");
            days.add("TUE");
            days.add("WED");
            days.add("THU");
            days.add("FRI");
        }
        return days.stream().collect(Collectors.joining(","));
    }
}


