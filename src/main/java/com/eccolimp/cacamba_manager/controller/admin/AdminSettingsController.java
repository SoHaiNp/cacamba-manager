package com.eccolimp.cacamba_manager.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eccolimp.cacamba_manager.domain.service.SystemSettingsService;
import com.eccolimp.cacamba_manager.notification.scheduling.NotificationScheduler;
import com.eccolimp.cacamba_manager.security.model.User;
import com.eccolimp.cacamba_manager.security.service.UserService;
import com.eccolimp.cacamba_manager.domain.service.ClienteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SystemSettingsService systemSettingsService;
    private final NotificationScheduler notificationScheduler;
    private final UserService userService;
    private final ClienteService clienteService;

    @PostMapping
    public String salvar(
            @RequestParam(value = "notificationsEnabled", required = false) String notificationsEnabled,
            @RequestParam(value = "fromEmail", required = false) String fromEmail,
            @RequestParam(value = "fromName", required = false) String fromName,
            @RequestParam(value = "reportToEmail", required = false) String reportToEmail,
            @RequestParam(value = "vencimentoTime", required = false) String vencimentoTime,
            @RequestParam(value = "relatorioTime", required = false) String relatorioTime,
            @RequestParam(value = "timeZone", required = false) String timeZone,
            @RequestParam(value = "runMon", required = false) String runMon,
            @RequestParam(value = "runTue", required = false) String runTue,
            @RequestParam(value = "runWed", required = false) String runWed,
            @RequestParam(value = "runThu", required = false) String runThu,
            @RequestParam(value = "runFri", required = false) String runFri,
            @RequestParam(value = "runSat", required = false) String runSat,
            @RequestParam(value = "runSun", required = false) String runSun,
            RedirectAttributes redirect
    ) {
        systemSettingsService.updateSettings(s -> {
            s.setNotificationsEnabled("on".equalsIgnoreCase(notificationsEnabled) || "true".equalsIgnoreCase(notificationsEnabled));
            s.setFromEmail(normalize(fromEmail));
            s.setFromName(normalize(fromName));
            // Usa sempre o email do primeiro ADMIN ativo como destinatário administrativo fixo
            String adminEmail = userService.listarUsuariosAtivos().stream()
                    .filter(u -> u.getRole() == User.Role.ADMIN)
                    .map(User::getEmail)
                    .findFirst()
                    .orElse(normalize(reportToEmail));
            s.setReportToEmail(adminEmail);
            s.setVencimentoTime(normalizeTime(vencimentoTime));
            s.setRelatorioTime(normalizeTime(relatorioTime));
            // Fuso fixo em Brasília
            s.setTimeZone("America/Sao_Paulo");
            s.setRunMon(runMon != null);
            s.setRunTue(runTue != null);
            s.setRunWed(runWed != null);
            s.setRunThu(runThu != null);
            s.setRunFri(runFri != null);
            s.setRunSat(runSat != null);
            s.setRunSun(runSun != null);
        });

        // Reagendar tarefas com base nas novas configurações
        notificationScheduler.refreshSchedules();

        redirect.addFlashAttribute("message", "Configurações salvas com sucesso.");
        return "redirect:/admin/settings";
    }
    @PostMapping("/clientes/recebedores")
    public ResponseEntity<String> atualizarRecebedores(@RequestBody java.util.List<ClienteRecebedorDTO> body) {
        // Atualiza preferências de recebimento por cliente
        for (ClienteRecebedorDTO dto : body) {
            try {
                clienteService.definirRecebeNotificacoes(dto.id(), dto.recebe());
            } catch (Exception ignored) {}
        }
        return ResponseEntity.ok("OK");
    }

    public record ClienteRecebedorDTO(Long id, boolean recebe) {}

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private String normalizeTime(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.matches("\\d{2}:\\d{2}") ? v : null;
    }
}


