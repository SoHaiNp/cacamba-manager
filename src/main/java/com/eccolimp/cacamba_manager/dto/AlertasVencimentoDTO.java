package com.eccolimp.cacamba_manager.dto;

import java.util.List;

public record AlertasVencimentoDTO(
    List<AluguelVencendoDTO> vencendoHoje,
    List<AluguelVencendoDTO> vencendoAmanha,
    List<AluguelVencendoDTO> vencendoProximosDias,
    int totalVencendo
) {
    public boolean temAlgumAlerta() {
        return totalVencendo > 0;
    }
    
    public boolean temVencendoHoje() {
        return !vencendoHoje.isEmpty();
    }
    
    public boolean temVencendoAmanha() {
        return !vencendoAmanha.isEmpty();
    }
    
    public boolean temVencendoProximosDias() {
        return !vencendoProximosDias.isEmpty();
    }
} 