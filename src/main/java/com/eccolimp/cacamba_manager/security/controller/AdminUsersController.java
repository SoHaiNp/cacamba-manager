package com.eccolimp.cacamba_manager.security.controller;

import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;
import com.eccolimp.cacamba_manager.security.dto.UserRegistrationDto;
import com.eccolimp.cacamba_manager.security.model.User;
import com.eccolimp.cacamba_manager.security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {

    private final UserService userService;

    @PostMapping("/create")
    public String create(@Valid UserRegistrationDto userForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("userForm", userForm);
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        }

        try {
            User created = userService.registrarUsuario(userForm);
            redirectAttributes.addFlashAttribute("message", "Usuário criado: " + created.getUsername());
            return "redirect:/admin/users";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userForm", userForm);
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Erro interno ao criar usuário");
            model.addAttribute("userForm", userForm);
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         org.springframework.security.core.Authentication authentication,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        try {
            Long solicitanteId = null;
            if (authentication != null && authentication.getPrincipal() instanceof com.eccolimp.cacamba_manager.security.service.CustomUserDetailsService.CustomUserPrincipal principal) {
                solicitanteId = principal.getId();
            }
            userService.excluirUsuario(id, solicitanteId);
            redirectAttributes.addFlashAttribute("message", "Usuário excluído com sucesso");
            return "redirect:/admin/users";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userForm", new UserRegistrationDto());
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Erro interno ao excluir usuário");
            model.addAttribute("userForm", new UserRegistrationDto());
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        }
    }

    @PostMapping("/{id}/password")
    public String changePassword(@PathVariable("id") Long id,
                                 String novaSenha,
                                 String confirmarSenha,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            userService.alterarSenha(id, novaSenha, confirmarSenha);
            redirectAttributes.addFlashAttribute("message", "Senha atualizada com sucesso");
            return "redirect:/admin/users";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userForm", new UserRegistrationDto());
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Erro interno ao alterar senha");
            model.addAttribute("userForm", new UserRegistrationDto());
            model.addAttribute("usuarios", userService.listarUsuariosAtivos());
            return "admin/users";
        }
    }
}


