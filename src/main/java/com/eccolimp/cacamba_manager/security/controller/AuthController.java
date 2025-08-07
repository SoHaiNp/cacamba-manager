package com.eccolimp.cacamba_manager.security.controller;

import com.eccolimp.cacamba_manager.security.dto.LoginRequest;
import com.eccolimp.cacamba_manager.security.dto.UserRegistrationDto;
import com.eccolimp.cacamba_manager.security.service.UserService;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String loginPage(Model model,
                           @RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "registered", required = false) String registered) {
        
        model.addAttribute("loginRequest", new LoginRequest());
        
        if (error != null) {
            model.addAttribute("error", "Usuário ou senha inválidos");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }
        
        if (registered != null) {
            model.addAttribute("message", "Usuário registrado com sucesso! Faça login para continuar.");
        }
        
        return "auth/login";
    }

    /**
     * Página de registro
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRegistration", new UserRegistrationDto());
        return "auth/register";
    }

    /**
     * Processar registro de usuário
     */
    @PostMapping("/register")
    public String registerUser(@Valid UserRegistrationDto userRegistration,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registrarUsuario(userRegistration);
            redirectAttributes.addAttribute("registered", "true");
            return "redirect:/login";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "Erro interno do servidor. Tente novamente.");
            return "auth/register";
        }
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