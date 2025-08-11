package com.eccolimp.cacamba_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record ClienteDTO(
    Long id,
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 120, message = "Nome deve ter entre 2 e 120 caracteres")
    String nome,
    @NotBlank(message = "Contato é obrigatório")
    @Size(min = 10, max = 120, message = "Contato deve ter entre 10 e 120 caracteres")
    String contato,
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 120, message = "Email deve ter no máximo 120 caracteres")
    String email
) {}
