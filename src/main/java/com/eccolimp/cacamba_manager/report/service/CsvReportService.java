package com.eccolimp.cacamba_manager.report.service;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;

@Service
public class CsvReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat NUM_FMT;
    static {
        NumberFormat f = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        f.setMinimumFractionDigits(2);
        f.setMaximumFractionDigits(2);
        f.setGroupingUsed(false); // evita separador de milhar para facilitar somas no Excel
        NUM_FMT = f;
    }

    public byte[] gerarCsvAlugueis(List<Aluguel> alugueis) {
        StringBuilder sb = new StringBuilder();
        // BOM para Excel reconhecer UTF-8 e manter acentos corretamente
        sb.append('\uFEFF');
        // Cabeçalho
        sb.append(String.join(";",
            "ID",
            "Cliente",
            "Email",
            "Contato",
            "Caçamba",
            "Capacidade (m³)",
            "Endereço",
            "Data Início",
            "Data Fim",
            "Status",
            "Valor Contrato",
            "Valor Troca",
            "Nº Trocas",
            "Total Trocas",
            "Valor Total"
        )).append('\n');

        for (Aluguel a : alugueis) {
            var cliente = a.getCliente();
            var cacamba = a.getCacamba();
            var valorContrato = a.getValorContrato() == null ? java.math.BigDecimal.ZERO : a.getValorContrato();
            var valorTroca = a.getValorTroca() == null ? java.math.BigDecimal.ZERO : a.getValorTroca();
            int numTrocas = a.getNumeroTrocas() == null ? 0 : a.getNumeroTrocas();
            var totalTrocas = valorTroca.multiply(java.math.BigDecimal.valueOf(numTrocas));
            var valorTotal = valorContrato.add(totalTrocas);

            sb.append(escape(String.valueOf(a.getId()))).append(';')
              .append(escape(cliente != null ? cliente.getNome() : "")).append(';')
              .append(escape(cliente != null ? nullSafe(cliente.getEmail()) : "")).append(';')
              .append(escape(cliente != null ? nullSafe(cliente.getContato()) : "")).append(';')
              .append(escape(cacamba != null ? cacamba.getCodigo() : "")).append(';')
              .append(escape(cacamba != null ? String.valueOf(cacamba.getCapacidadeM3()) : "")).append(';')
              .append(escape(nullSafe(a.getEndereco()))).append(';')
              .append(escape(a.getDataInicio() != null ? a.getDataInicio().format(DATE_FMT) : "")).append(';')
              .append(escape(a.getDataFim() != null ? a.getDataFim().format(DATE_FMT) : "")).append(';')
              .append(escape(a.getStatus() != null ? a.getStatus().name() : "")).append(';')
              .append(escape(formatDecimal(valorContrato))).append(';')
              .append(escape(formatDecimal(valorTroca))).append(';')
              .append(escape(String.valueOf(numTrocas))).append(';')
              .append(escape(formatDecimal(totalTrocas))).append(';')
              .append(escape(formatDecimal(valorTotal)))
              .append('\n');
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String escape(String field) {
        String f = field == null ? "" : field;
        boolean mustQuote = f.contains(";") || f.contains("\n") || f.contains("\r") || f.contains("\"");
        if (mustQuote) {
            f = '"' + f.replace("\"", "\"\"") + '"';
        }
        return f;
    }

    private static String formatDecimal(java.math.BigDecimal value) {
        if (value == null) return "";
        return NUM_FMT.format(value);
    }
}


