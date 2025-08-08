package com.eccolimp.cacamba_manager.security.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;


@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final Environment env;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // Só cria admin se NÃO existir nenhum ADMIN
        Boolean anyAdmin = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM usuarios WHERE role = 'ADMIN')",
                Boolean.class
        );
        if (Boolean.TRUE.equals(anyAdmin)) {
            return;
        }

        String username = getEnv("APP_ADMIN_USERNAME", "admin");
        String email = getEnv("APP_ADMIN_EMAIL", "admin@cacamba.com");
        String rawPassword = getEnv("APP_ADMIN_PASSWORD", null);

        if (rawPassword == null || rawPassword.isBlank()) {
            rawPassword = generateTempPassword();
            log.warn("[ADMIN BOOTSTRAP] Gerando senha temporária — altere imediatamente.");
        }

        Integer duplicates = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM usuarios WHERE username = ? OR email = ?",
                Integer.class,
                username, email
        );
        if (duplicates != null && duplicates > 0) {
            log.info("[ADMIN BOOTSTRAP] Usuário admin já existe por username/email. Não será recriado.");
            return;
        }

        String hashed = passwordEncoder.encode(rawPassword);
        jdbcTemplate.update(
                "INSERT INTO usuarios (username, password, email, nome_completo, role, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, 'ADMIN', true, true, true, true)",
                username, hashed, email, "Administrador do Sistema"
        );

        log.warn("[ADMIN BOOTSTRAP] Admin criado: username='{}', email='{}'. Senha temporária='{}'", username, email, rawPassword);
    }

    private String getEnv(String key, String defaultValue) {
        String value = env.getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private String generateTempPassword() {
        // Simples: 12 chars aleatórios alfanuméricos
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#%&*";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}


