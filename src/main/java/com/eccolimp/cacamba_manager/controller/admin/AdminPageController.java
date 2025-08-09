package com.eccolimp.cacamba_manager.controller.admin;

import com.eccolimp.cacamba_manager.security.dto.UserRegistrationDto;
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
    public String settingsPage() {
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
    public String reportsPage() {
        return "admin/reports";
    }
}


