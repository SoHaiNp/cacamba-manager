package com.eccolimp.cacamba_manager.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cacamba", uniqueConstraints = @UniqueConstraint(name = "uk_codigo", columnNames = {"codigo"}))
public class Cacamba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String codigo;

    @Column(nullable = false)
    private Integer capacidadeM3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusCacamba status = StatusCacamba.DISPONIVEL;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCapacidadeM3() {
        return capacidadeM3;
    }

    public void setCapacidadeM3(Integer capacidadeM3) {
        this.capacidadeM3 = capacidadeM3;
    }

    public StatusCacamba getStatus() {
        return status;
    }

    public void setStatus(StatusCacamba status) {
        this.status = status;
    }
}
