package com.eccolimp.cacamba_manager.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluguel", indexes = @Index(name = "idx_data_fim", columnList = "data_fim"))
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cacamba cacamba;

    @Column(nullable = false, length = 180)
    private String endereco;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusAluguel status = StatusAluguel.ATIVO;

    @Column(name = "valor_contrato", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorContrato = BigDecimal.ZERO;

    @Column(name = "valor_troca", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorTroca = BigDecimal.ZERO;

    @Column(name = "numero_trocas", nullable = false)
    private Integer numeroTrocas = 0;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cacamba getCacamba() {
        return cacamba;
    }

    public void setCacamba(Cacamba cacamba) {
        this.cacamba = cacamba;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public BigDecimal getValorContrato() {
        return valorContrato;
    }

    public void setValorContrato(BigDecimal valorContrato) {
        this.valorContrato = valorContrato;
    }

    public BigDecimal getValorTroca() {
        return valorTroca;
    }

    public void setValorTroca(BigDecimal valorTroca) {
        this.valorTroca = valorTroca;
    }

    public Integer getNumeroTrocas() {
        return numeroTrocas;
    }

    public void setNumeroTrocas(Integer numeroTrocas) {
        this.numeroTrocas = numeroTrocas;
    }
}
