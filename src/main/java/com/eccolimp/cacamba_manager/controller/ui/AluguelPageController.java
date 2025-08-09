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
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) com.eccolimp.cacamba_manager.domain.model.StatusAluguel status,
                       @RequestParam(required = false) String cliente,
                       @RequestParam(required = false) String cacamba,
                       @RequestParam(required = false) java.time.LocalDate dataInicioDe,
                       @RequestParam(required = false) java.time.LocalDate dataInicioAte,
                       @RequestParam(required = false) java.time.LocalDate dataFimDe,
                       @RequestParam(required = false) java.time.LocalDate dataFimAte,
                       @RequestParam(required = false) com.eccolimp.cacamba_manager.dto.SituacaoVencimento situacao,
                       @RequestParam(required = false) Boolean comAtraso,
                       @RequestParam(required = false) String texto) {

        var filtro = new com.eccolimp.cacamba_manager.dto.FiltroAluguel();
        filtro.setStatus(status);
        filtro.setClienteNome(cliente);
        filtro.setCacambaCodigo(cacamba);
        filtro.setDataInicioDe(dataInicioDe);
        filtro.setDataInicioAte(dataInicioAte);
        filtro.setDataFimDe(dataFimDe);
        filtro.setDataFimAte(dataFimAte);
        filtro.setSituacao(situacao);
        filtro.setComAtraso(comAtraso);
        filtro.setTexto(texto);

        var pageDetalhada = aluguelService.listarFiltrado(filtro, page, 10);
        model.addAttribute("alugueis", pageDetalhada);
        return "aluguel/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("novoAluguel", new NovoAluguelRequest(null, null, null, LocalDate.now(), 0));
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("cacambas", cacambaService.listarTodas().stream()
                .filter(c -> !aluguelService.cacambaEstaEmUso(c.id()))
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

    // Removido endpoint de cancelar conforme novo fluxo de negócios

    @PostMapping("/{id}/trocar")
    public String trocar(@PathVariable Long id,
                         @RequestParam("novaCacambaId") Long novaCacambaId,
                         RedirectAttributes redirectAttributes) {
        try {
            aluguelService.trocarCacamba(id, novaCacambaId);
            redirectAttributes.addFlashAttribute("mensagem", "Troca de caçamba realizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ui/alugueis/" + id;
    }

    @PostMapping("/{id}/renovar")
    public String renovar(@PathVariable Long id,
                          @RequestParam(value = "diasAdicionais", required = false) Integer diasAdicionais,
                          @RequestParam(value = "novaDataInicio", required = false) java.time.LocalDate novaDataInicio,
                          @RequestParam(value = "dias", required = false) Integer dias,
                          RedirectAttributes redirectAttributes) {
        try {
            // Se vierem campos de novaDataInicio e dias, cria novo contrato e finaliza o atual
            if (novaDataInicio != null && dias != null) {
                var novo = aluguelService.renovarCriandoNovo(id, novaDataInicio, dias);
                redirectAttributes.addFlashAttribute("mensagem", "Contrato renovado! Novo contrato criado com sucesso.");
                return "redirect:/ui/alugueis/" + novo.id();
            }

            // fallback: renovação simples por dias adicionais (mantém o mesmo registro)
            aluguelService.renovar(id, diasAdicionais != null ? diasAdicionais : 1);
            redirectAttributes.addFlashAttribute("mensagem", "Aluguel renovado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao renovar aluguel: " + e.getMessage());
        }
        return "redirect:/ui/alugueis/" + id;
    }

    @PostMapping("/{id}/arquivar")
    public String arquivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            aluguelService.arquivar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Aluguel arquivado e removido da lista!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao arquivar aluguel: " + e.getMessage());
        }
        return "redirect:/ui/alugueis";
    }
} 