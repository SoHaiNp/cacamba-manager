package com.eccolimp.cacamba_manager.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eccolimp.cacamba_manager.domain.service.CacambaService;
import com.eccolimp.cacamba_manager.domain.service.ClienteService;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("ui")
@RequiredArgsConstructor
public class DashboardController {

    private final CacambaService cacambaService;
    private final ClienteService clienteService;
    private final AluguelService aluguelService;

    @GetMapping
    public String dashboard(Model model) {
        // Otimizar consultas - buscar uma única vez e filtrar em memória
        var todasCacambas = cacambaService.listarTodas();
        var todosClientes = clienteService.listarTodos();
        var alertasVencimento = aluguelService.buscarAlertasVencimento();
        
        model.addAttribute("totalCacambas", todasCacambas.size());
        long alugadas = todasCacambas.stream()
                .filter(c -> !cacambaService.estaDisponivel(c.id()))
                .count();
        model.addAttribute("cacambasAlugadas", alugadas);
        model.addAttribute("cacambasDisponiveis", todasCacambas.size() - alugadas);
        model.addAttribute("totalClientes", todosClientes.size());
        model.addAttribute("alugueisAtivos", aluguelService.countAtivos());
        model.addAttribute("alertasVencimento", alertasVencimento);
        
        return "dashboard/index";
    }
} 