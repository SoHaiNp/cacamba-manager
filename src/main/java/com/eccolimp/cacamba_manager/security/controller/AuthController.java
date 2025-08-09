package com.eccolimp.cacamba_manager.security.controller;

import com.eccolimp.cacamba_manager.security.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String loginPage(Model model,
                           @RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout) {
        
        model.addAttribute("loginRequest", new LoginRequest());
        
        if (error != null) {
            // Mensagem uniforme para evitar vazamento de informação
            model.addAttribute("error", "Usuário ou senha inválidos");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }
        
        return "auth/login";
    }

    /**
     * Página de acesso negado
     */
    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "auth/access-denied";
    }

    /**
     * Página de erro de autenticação
     */
    @GetMapping("/auth-error")
    public String authErrorPage(Model model,
                               @RequestParam(value = "message", required = false) String message) {
        model.addAttribute("message", message != null ? message : "Erro de autenticação");
        return "auth/error";
    }
}