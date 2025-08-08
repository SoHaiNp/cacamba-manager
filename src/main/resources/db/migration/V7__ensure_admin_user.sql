-- Garante a existência do usuário administrador padrão
-- Senha: admin123 (BCrypt)

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin') THEN
        UPDATE usuarios
        SET password = '$2a$10$N.zmdr9k7uOCQQydw4AdO.FbOFzFINKrQn6leFTr.lMrvA6n5GKn2',
            email = 'admin@cacamba.com',
            nome_completo = 'Administrador do Sistema',
            role = 'ADMIN',
            enabled = true,
            account_non_expired = true,
            account_non_locked = true,
            credentials_non_expired = true
        WHERE username = 'admin';
    ELSE
        INSERT INTO usuarios (username, password, email, nome_completo, role)
        VALUES ('admin', '$2a$10$N.zmdr9k7uOCQQydw4AdO.FbOFzFINKrQn6leFTr.lMrvA6n5GKn2', 'admin@cacamba.com', 'Administrador do Sistema', 'ADMIN');
    END IF;
END $$;


