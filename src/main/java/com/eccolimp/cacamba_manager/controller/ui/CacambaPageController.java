package com.eccolimp.cacamba_manager.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eccolimp.cacamba_manager.domain.service.CacambaService;
import com.eccolimp.cacamba_manager.dto.CacambaDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("ui/cacambas")
@RequiredArgsConstructor
public class CacambaPageController {

    private final CacambaService service;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("cacambas", service.listar(page, 10));
        return "cacamba/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cacamba", new CacambaDTO(null, "", null, null));
        return "cacamba/form";
    }

    @PostMapping
    public String salvar(CacambaDTO cacamba, RedirectAttributes redirectAttributes) {
        try {
            service.criar(cacamba);
            redirectAttributes.addFlashAttribute("mensagem", "Caçamba criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar caçamba: " + e.getMessage());
        }
        return "redirect:/ui/cacambas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        try {
            CacambaDTO cacamba = service.buscarPorId(id);
            model.addAttribute("cacamba", cacamba);
            return "cacamba/form";
        } catch (Exception e) {
            return "redirect:/ui/cacambas";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, CacambaDTO cacamba, RedirectAttributes redirectAttributes) {
        try {
            service.atualizar(id, cacamba);
            redirectAttributes.addFlashAttribute("mensagem", "Caçamba atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar caçamba: " + e.getMessage());
        }
        return "redirect:/ui/cacambas";
    }

    @PostMapping("/{id}/delete")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Caçamba excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir caçamba: " + e.getMessage());
        }
        return "redirect:/ui/cacambas";
    }
}
