-- Adiciona flag de recebimento de notificações ao cliente
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS recebe_notificacoes BOOLEAN NOT NULL DEFAULT TRUE;

-- Normaliza: clientes sem email ou com email '-' não recebem por padrão
UPDATE cliente SET recebe_notificacoes = FALSE WHERE email IS NULL OR email = '' OR email = '-';


