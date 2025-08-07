package com.eccolimp.cacamba_manager.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username ou email é obrigatório")
    private String login; // Pode ser username ou email

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    private Boolean rememberMe = false;
}