-- Corrigir senha do usuário administrador
-- Primeiro deletar o admin existente
DELETE FROM usuarios WHERE username = 'admin';

-- Recriar com hash correto
-- Senha: admin123 
-- Hash BCrypt: $2a$10$N.zmdr9k7uOCQQydw4AdO.FbOFzFINKrQn6leFTr.lMrvA6n5GKn2
INSERT INTO usuarios (username, password, email, nome_completo, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQQydw4AdO.FbOFzFINKrQn6leFTr.lMrvA6n5GKn2', 'admin@cacamba.com', 'Administrador do Sistema', 'ADMIN');