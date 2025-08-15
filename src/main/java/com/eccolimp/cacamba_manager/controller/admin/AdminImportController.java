package com.eccolimp.cacamba_manager.controller.admin;

import com.eccolimp.cacamba_manager.domain.service.ClienteService;
import com.eccolimp.cacamba_manager.domain.service.CacambaService;
import com.eccolimp.cacamba_manager.domain.service.AluguelService;
import com.eccolimp.cacamba_manager.dto.ClienteDTO;
import com.eccolimp.cacamba_manager.dto.CacambaDTO;
import com.eccolimp.cacamba_manager.dto.NovoAluguelRequest;
import com.eccolimp.cacamba_manager.domain.model.StatusCacamba;
import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/import")
@RequiredArgsConstructor
public class AdminImportController {

    private final ClienteService clienteService;
    private final CacambaService cacambaService;
    private final AluguelService aluguelService;

    // Template para Clientes
    @GetMapping("/clientes/template")
    public void downloadClientesTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"template_clientes.csv\"");
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println("nome,contato,email");
            writer.println("João Silva,(11) 99999-9999,joao@email.com");
            writer.println("Maria Santos,(11) 88888-8888,maria@email.com");
        }
    }

    // Template para Caçambas
    @GetMapping("/cacambas/template")
    public void downloadCacambasTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"template_cacambas.csv\"");
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println("codigo,capacidadeM3");
            writer.println("C001,3");
            writer.println("C002,6");
            writer.println("C003,10");
        }
    }

    // Template para Contratos
    @GetMapping("/contratos/template")
    public void downloadContratosTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"template_contratos.csv\"");
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println("cliente_nome,cacamba_codigo,endereco,data_inicio,data_fim,valor_contrato,valor_troca,numero_trocas");
            writer.println("João Silva,C001,Rua A, 123 - São Paulo,2024-01-01,2024-01-31,150.00,50.00,0");
            writer.println("Maria Santos,C002,Av B, 456 - São Paulo,2024-01-15,2024-02-15,200.00,75.00,0");
        }
    }

    // Importar Clientes
    @PostMapping("/clientes")
    public String importarClientes(@RequestParam("file") MultipartFile file, 
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Arquivo não selecionado");
            return "redirect:/admin/import";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linha;
            int linhaNumero = 0;
            int sucessos = 0;
            int erros = 0;
            
            while ((linha = reader.readLine()) != null) {
                linhaNumero++;
                if (linhaNumero == 1) continue; // Pular cabeçalho
                
                try {
                    String[] campos = linha.split(",");
                    if (campos.length >= 3) {
                        String nome = campos[0].trim();
                        String contato = campos[1].trim();
                        String email = campos[2].trim();
                        
                        if (!nome.isEmpty()) {
                            ClienteDTO cliente = new ClienteDTO(null, nome, contato, email);
                            clienteService.criar(cliente);
                            sucessos++;
                        }
                    }
                } catch (Exception e) {
                    erros++;
                }
            }
            
            redirectAttributes.addFlashAttribute("mensagem", 
                String.format("Importação concluída: %d sucessos, %d erros", sucessos, erros));
                
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao ler arquivo: " + e.getMessage());
        }
        
        return "redirect:/admin/import";
    }

    // Importar Caçambas
    @PostMapping("/cacambas")
    public String importarCacambas(@RequestParam("file") MultipartFile file, 
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Arquivo não selecionado");
            return "redirect:/admin/import";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linha;
            int linhaNumero = 0;
            int sucessos = 0;
            int erros = 0;
            
            while ((linha = reader.readLine()) != null) {
                linhaNumero++;
                if (linhaNumero == 1) continue; // Pular cabeçalho
                
                try {
                    String[] campos = linha.split(",");
                    if (campos.length >= 2) {
                        String codigo = campos[0].trim();
                        String capacidadeStr = campos[1].trim();
                        
                        if (!codigo.isEmpty() && !capacidadeStr.isEmpty()) {
                            Integer capacidade = Integer.parseInt(capacidadeStr);
                            CacambaDTO cacamba = new CacambaDTO(null, codigo, capacidade, StatusCacamba.DISPONIVEL);
                            cacambaService.criar(cacamba);
                            sucessos++;
                        }
                    }
                } catch (Exception e) {
                    erros++;
                }
            }
            
            redirectAttributes.addFlashAttribute("mensagem", 
                String.format("Importação concluída: %d sucessos, %d erros", sucessos, erros));
                
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao ler arquivo: " + e.getMessage());
        }
        
        return "redirect:/admin/import";
    }

    // Importar Contratos
    @PostMapping("/contratos")
    public String importarContratos(@RequestParam("file") MultipartFile file, 
                                    RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Arquivo não selecionado");
            return "redirect:/admin/import";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linha;
            int linhaNumero = 0;
            int sucessos = 0;
            int erros = 0;
            
            while ((linha = reader.readLine()) != null) {
                linhaNumero++;
                if (linhaNumero == 1) continue; // Pular cabeçalho
                
                try {
                    String[] campos = linha.split(",");
                    if (campos.length >= 8) {
                        String clienteNome = campos[0].trim();
                        String cacambaCodigo = campos[1].trim();
                        String endereco = campos[2].trim();
                        String dataInicioStr = campos[3].trim();
                        String dataFimStr = campos[4].trim();
                        String valorContratoStr = campos[5].trim();
                        String valorTrocaStr = campos[6].trim();
                        String numeroTrocasStr = campos[7].trim();
                        
                        // Buscar cliente por nome
                        List<ClienteDTO> clientes = clienteService.listarTodos();
                        Optional<ClienteDTO> clienteOpt = clientes.stream()
                            .filter(c -> c.nome().equalsIgnoreCase(clienteNome))
                            .findFirst();
                        
                        if (clienteOpt.isEmpty()) {
                            erros++;
                            continue;
                        }
                        
                        // Buscar caçamba por código
                        List<CacambaDTO> cacambas = cacambaService.listarTodas();
                        Optional<CacambaDTO> cacambaOpt = cacambas.stream()
                            .filter(c -> c.codigo().equalsIgnoreCase(cacambaCodigo))
                            .findFirst();
                        
                        if (cacambaOpt.isEmpty()) {
                            erros++;
                            continue;
                        }
                        
                        // Parsear datas
                        LocalDate dataInicio = LocalDate.parse(dataInicioStr, DateTimeFormatter.ISO_LOCAL_DATE);
                        LocalDate dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ISO_LOCAL_DATE);
                        
                        // Parsear valores
                        BigDecimal valorContrato = new BigDecimal(valorContratoStr);
                        BigDecimal valorTroca = new BigDecimal(valorTrocaStr);
                        
                        // Parsear número de trocas
                        Integer numeroTrocas = Integer.parseInt(numeroTrocasStr);
                        if (numeroTrocas < 0) {
                            erros++;
                            continue;
                        }
                        
                        // Calcular dias
                        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
                        
                        // Criar aluguel
                        NovoAluguelRequest request = new NovoAluguelRequest(
                            clienteOpt.get().id(),
                            cacambaOpt.get().id(),
                            endereco,
                            dataInicio,
                            (int) dias,
                            valorContrato,
                            valorTroca
                        );
                        
                        aluguelService.registrar(request);
                        
                        // Atualizar número de trocas após criação (se necessário)
                        if (numeroTrocas > 0) {
                            // Buscar o aluguel criado e atualizar número de trocas
                            // Isso pode ser implementado se necessário
                        }
                        
                        sucessos++;
                    }
                } catch (Exception e) {
                    erros++;
                }
            }
            
            redirectAttributes.addFlashAttribute("mensagem", 
                String.format("Importação concluída: %d sucessos, %d erros", sucessos, erros));
                
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao ler arquivo: " + e.getMessage());
        }
        
        return "redirect:/admin/import";
    }
}
