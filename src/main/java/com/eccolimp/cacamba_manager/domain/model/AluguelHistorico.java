package com.eccolimp.cacamba_manager.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluguel_historico")
public class AluguelHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aluguel_id", nullable = false)
    private Long aluguelId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "cacamba_id", nullable = false)
    private Long cacambaId;

    @Column(nullable = false, length = 180)
    private String endereco;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false, length = 12)
    private String status;

    @Column(name = "data_arquivamento", nullable = false)
    private LocalDateTime dataArquivamento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAluguelId() { return aluguelId; }
    public void setAluguelId(Long aluguelId) { this.aluguelId = aluguelId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getCacambaId() { return cacambaId; }
    public void setCacambaId(Long cacambaId) { this.cacambaId = cacambaId; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataArquivamento() { return dataArquivamento; }
    public void setDataArquivamento(LocalDateTime dataArquivamento) { this.dataArquivamento = dataArquivamento; }
}


