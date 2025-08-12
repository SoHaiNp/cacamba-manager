package com.eccolimp.cacamba_manager.controller.admin;

import com.eccolimp.cacamba_manager.security.dto.UserRegistrationDto;
import com.eccolimp.cacamba_manager.domain.service.ClienteService;
import com.eccolimp.cacamba_manager.domain.service.SystemSettingsService;
import com.eccolimp.cacamba_manager.domain.model.SystemSettings;
import com.eccolimp.cacamba_manager.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    private final UserService userService;
    private final ClienteService clienteService;
    private final SystemSettingsService systemSettingsService;

    @GetMapping("/login")
    public String adminLogin(Model model,
                             @RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Usuário ou senha inválidos");
        }
        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }
        return "admin/login";
    }

    @GetMapping({"", "/"})
    public String adminIndex() {
        return "admin/index";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        SystemSettings settings = systemSettingsService.getOrCreateDefaults();
        model.addAttribute("settings", settings);
        var usuariosAtivos = userService.listarUsuariosAtivos();
        model.addAttribute("usuariosAtivos", usuariosAtivos);
        String adminEmail = usuariosAtivos.stream()
                .filter(u -> u.getRole() == com.eccolimp.cacamba_manager.security.model.User.Role.ADMIN)
                .map(com.eccolimp.cacamba_manager.security.model.User::getEmail)
                .findFirst().orElse(settings.getReportToEmail());
        model.addAttribute("adminEmail", adminEmail);
        var clientesComEmail = clienteService.listarTodos().stream()
                .filter(c -> c.email() != null && !c.email().isBlank() && !"-".equals(c.email()))
                .map(c -> new com.eccolimp.cacamba_manager.controller.admin.vm.ClienteRecebedorVM(
                        c.id(), c.nome(), c.email(),
                        // fallback: se DTO ainda não expõe, tratamos como true por padrão
                        true
                ))
                .toList();
        model.addAttribute("clientesComEmail", clientesComEmail);
        return "admin/settings";
    }

    @GetMapping("/import")
    public String importPage() {
        return "admin/import";
    }

    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("userForm", new UserRegistrationDto());
        model.addAttribute("usuarios", userService.listarUsuariosAtivos());
        return "admin/users";
    }

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "admin/reports";
    }
}


