package com.eccolimp.cacamba_manager.dto;

import java.time.LocalDate;

import com.eccolimp.cacamba_manager.domain.model.StatusAluguel;

public class FiltroAluguel {

    private StatusAluguel status;
    private String clienteNome;
    private String cacambaCodigo;
    private LocalDate dataInicioDe;
    private LocalDate dataInicioAte;
    private LocalDate dataFimDe;
    private LocalDate dataFimAte;
    private SituacaoVencimento situacao;
    private Boolean comAtraso;
    private String texto;

    public StatusAluguel getStatus() { return status; }
    public void setStatus(StatusAluguel status) { this.status = status; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getCacambaCodigo() { return cacambaCodigo; }
    public void setCacambaCodigo(String cacambaCodigo) { this.cacambaCodigo = cacambaCodigo; }

    public LocalDate getDataInicioDe() { return dataInicioDe; }
    public void setDataInicioDe(LocalDate dataInicioDe) { this.dataInicioDe = dataInicioDe; }

    public LocalDate getDataInicioAte() { return dataInicioAte; }
    public void setDataInicioAte(LocalDate dataInicioAte) { this.dataInicioAte = dataInicioAte; }

    public LocalDate getDataFimDe() { return dataFimDe; }
    public void setDataFimDe(LocalDate dataFimDe) { this.dataFimDe = dataFimDe; }

    public LocalDate getDataFimAte() { return dataFimAte; }
    public void setDataFimAte(LocalDate dataFimAte) { this.dataFimAte = dataFimAte; }

    public SituacaoVencimento getSituacao() { return situacao; }
    public void setSituacao(SituacaoVencimento situacao) { this.situacao = situacao; }

    public Boolean getComAtraso() { return comAtraso; }
    public void setComAtraso(Boolean comAtraso) { this.comAtraso = comAtraso; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}


