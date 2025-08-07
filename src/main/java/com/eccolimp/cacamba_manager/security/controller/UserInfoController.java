package com.eccolimp.cacamba_manager.security.controller;

import com.eccolimp.cacamba_manager.security.service.CustomUserDetailsService.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controlador para disponibilizar informações do usuário logado em todos os templates
 */
@ControllerAdvice
public class UserInfoController {

    @ModelAttribute("currentUser")
    public CustomUserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && 
            auth.getPrincipal() instanceof CustomUserPrincipal) {
            return (CustomUserPrincipal) auth.getPrincipal();
        }
        
        return null;
    }
    
    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && 
               auth.getPrincipal() instanceof CustomUserPrincipal;
    }
}