-- Criar tabela de usuários para sistema de login
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    nome_completo VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP
);

-- Criar índices para melhor performance
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_enabled ON usuarios(enabled);

-- Inserir usuário administrador padrão
-- Senha: admin123 (hash BCrypt com strength 10)
INSERT INTO usuarios (username, password, email, nome_completo, role) VALUES 
('admin', '$2a$10$rTI3BvnQKBxwHy8fKh3b.eaV5Y9qZOX8k2fOGI4HFvK1DcB3LRJWa', 'admin@cacamba.com', 'Administrador do Sistema', 'ADMIN');