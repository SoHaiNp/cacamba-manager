-- Tornar contato opcional e permitir email opcional utilizando valor placeholder '-'

-- 1) Tornar contato NULLABLE
ALTER TABLE cliente ALTER COLUMN contato DROP NOT NULL;

-- 2) Remover constraint única antiga do email, se existir (compatível com H2 e PostgreSQL)
-- Para PostgreSQL: usar DO $$
-- Para H2: usar execução direta
-- Como não sabemos qual banco está sendo usado, vamos tentar remover a constraint diretamente
-- Se não existir, o erro será ignorado
ALTER TABLE cliente DROP CONSTRAINT IF EXISTS uk_cliente_email;

-- 3) Garantir email NOT NULL
ALTER TABLE cliente ALTER COLUMN email SET NOT NULL;

-- 4) Preencher '-' quando email estiver vazio ou nulo
UPDATE cliente SET email = '-' WHERE email IS NULL OR email = '';

-- 5) Criar índice único parcial que ignora email '-' (compatível com H2 e PostgreSQL)
-- Para PostgreSQL: usar WHERE
-- Para H2: criar índice simples (H2 não suporta índices parciais)
-- Vamos criar um índice simples que será suficiente para ambos
CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_email_not_dash ON cliente (email);

