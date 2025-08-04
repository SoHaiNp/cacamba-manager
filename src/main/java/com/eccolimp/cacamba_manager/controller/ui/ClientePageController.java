package com.eccolimp.cacamba_manager.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eccolimp.cacamba_manager.domain.service.ClienteService;
import com.eccolimp.cacamba_manager.dto.ClienteDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("ui/clientes")
@RequiredArgsConstructor
public class ClientePageController {

    private final ClienteService service;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("clientes", service.listar(page, 10));
        return "cliente/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new ClienteDTO(null, "", ""));
        return "cliente/form";
    }

    @PostMapping("/salvar")
    public String salvar(ClienteDTO cliente, RedirectAttributes redirectAttributes) {
        try {
            service.criar(cliente);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar cliente: " + e.getMessage());
        }
        return "redirect:/ui/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        try {
            ClienteDTO cliente = service.buscarPorId(id);
            model.addAttribute("cliente", cliente);
            return "cliente/form";
        } catch (Exception e) {
            return "redirect:/ui/clientes";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, ClienteDTO cliente, RedirectAttributes redirectAttributes) {
        try {
            service.atualizar(id, cliente);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar cliente: " + e.getMessage());
        }
        return "redirect:/ui/clientes";
    }

    @PostMapping("/{id}/delete")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir cliente: " + e.getMessage());
        }
        return "redirect:/ui/clientes";
    }
} 