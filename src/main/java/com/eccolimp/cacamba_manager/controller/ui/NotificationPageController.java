package com.eccolimp.cacamba_manager.controller.ui;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eccolimp.cacamba_manager.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ui/notifications")
@RequiredArgsConstructor
public class NotificationPageController {

    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;

    @GetMapping("/agendar")
    public String schedulePage(Model model,
                               @RequestParam(value = "mensagem", required = false) String mensagem) {
        if (mensagem != null) {
            model.addAttribute("mensagem", mensagem);
        }
        // Valor padrão de fuso
        model.addAttribute("defaultZone", "America/Sao_Paulo");
        return "notifications/schedule";
    }

    @PostMapping("/agendar")
    public String schedule(@RequestParam("v") String dateTime,
                           @RequestParam(value = "zone", defaultValue = "America/Sao_Paulo") String zone,
                           RedirectAttributes ra) {
        ZonedDateTime zdt = parseToZoned(dateTime, zone);
        Date oneTime = Date.from(zdt.toInstant());
        // Usa a sobrecarga com Date para execução única
        taskScheduler.schedule(notificationService::enviarNotificacoesVencimento, oneTime);
        ra.addAttribute("mensagem", "Agendado para " + zdt.toString());
        return "redirect:/ui/notifications/agendar";
    }

    private ZonedDateTime parseToZoned(String input, String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        // Tenta ZonedDateTime completo
        try {
            return ZonedDateTime.parse(input).withZoneSameInstant(zoneId);
        } catch (DateTimeParseException ignored) { }

        // Tenta LocalDateTime com segundos
        try {
            LocalDateTime ldt = LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return ldt.atZone(zoneId);
        } catch (DateTimeParseException ignored) { }

        // Tenta LocalDateTime sem segundos (padrão do input datetime-local)
        try {
            LocalDateTime ldt = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            return ldt.atZone(zoneId);
        } catch (DateTimeParseException ex) {
            throw ex;
        }
    }
}


