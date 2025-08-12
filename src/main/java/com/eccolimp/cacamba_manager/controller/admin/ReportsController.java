package com.eccolimp.cacamba_manager.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eccolimp.cacamba_manager.domain.model.Aluguel;
import com.eccolimp.cacamba_manager.domain.repository.AluguelRepository;
import com.eccolimp.cacamba_manager.domain.repository.spec.AluguelSpecifications;
import com.eccolimp.cacamba_manager.report.service.CsvReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final AluguelRepository aluguelRepository;
    private final CsvReportService csvReportService;

    @Transactional(readOnly = true)
    @GetMapping("/alugueis.csv")
    public ResponseEntity<byte[]> downloadAlugueisCsv(
        @RequestParam(name = "cliente", required = false) String cliente,
        @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        Specification<Aluguel> spec = (root, query, cb) -> cb.conjunction();
        spec = spec.and(AluguelSpecifications.clienteNomeLike(cliente));
        spec = spec.and(AluguelSpecifications.dataInicioBetween(dataInicio, null));
        spec = spec.and(AluguelSpecifications.dataFimBetween(null, dataFim));

        List<Aluguel> alugueis = aluguelRepository.findAll(spec);
        byte[] csv = csvReportService.gerarCsvAlugueis(alugueis);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=alugueis.csv");
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}


