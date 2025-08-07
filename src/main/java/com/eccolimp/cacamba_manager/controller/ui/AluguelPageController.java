package com.eccolimp.cacamba_manager.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.domain.service.CacambaService;
import com.eccolimp.cacamba_manager.domain.service.ClienteService;
import com.eccolimp.cacamba_manager.dto.AluguelDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Controller
@RequestMapping("ui/alugueis")
@RequiredArgsConstructor
public class AluguelPageController {

    private final AluguelService aluguelService;
    private final ClienteService clienteService;
    private final CacambaService cacambaService;

    @GetMapping
    public String list(Model model, @RequestParam(defaultValue = "0") int page) {
        var alugueisPage = aluguelService.listar(page, 10);
        var alugueisDetalhados = alugueisPage.getContent().stream()
                .map(dto -> aluguelService.buscarDetalhadoPorId(dto.id()))
                .toList();
        
        // Criar uma nova página com os dados detalhados
        var pageDetalhada = new org.springframework.data.domain.PageImpl<>(
                alugueisDetalhados, 
                alugueisPage.getPageable(), 
                alugueisPage.getTotalElements()
        );
        
        model.addAttribute("alugueis", pageDetalhada);
        return "aluguel/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("novoAluguel", new NovoAluguelRequest(null, null, null, LocalDate.now(), 0));
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("cacambas", cacambaService.listarTodas().stream()
                .filter(c -> c.status().name().equals("DISPONIVEL"))
                .toList());
        return "aluguel/form";
    }

    @PostMapping("/salvar")
    public String salvar(NovoAluguelRequest request, RedirectAttributes redirectAttributes) {
        try {
            aluguelService.registrar(request);
            redirectAttributes.addFlashAttribute("mensagem", "Aluguel registrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar aluguel: " + e.getMessage());
        }
        return "redirect:/ui/alugueis";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        model.addAttribute("aluguel", aluguelService.buscarDetalhadoPorId(id));
        return "aluguel/detail";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            aluguelService.finalizar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Aluguel finalizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao finalizar aluguel: " + e.getMessage());
        }
        return "redirect:/ui/alugueis";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            aluguelService.cancelar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Aluguel cancelado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar aluguel: " + e.getMessage());
        }
        return "redirect:/ui/alugueis";
    }
} 