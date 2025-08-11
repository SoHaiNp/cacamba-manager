-- Adiciona coluna de email para cliente, preenche a partir de contato (se vazio), normaliza para minúsculas e aplica unicidade

-- 1) Adicionar coluna (se não existir)
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS email VARCHAR(120);

-- 2) Backfill: se email estiver nulo ou vazio, copiar de contato
UPDATE cliente SET email = contato WHERE (email IS NULL OR email = '') AND contato IS NOT NULL;

-- 3) Normalizar para minúsculas
UPDATE cliente SET email = LOWER(email) WHERE email IS NOT NULL;

-- 4) Tornar NOT NULL
ALTER TABLE cliente ALTER COLUMN email SET NOT NULL;

-- 5) Garantir unicidade
ALTER TABLE cliente ADD CONSTRAINT uk_cliente_email UNIQUE (email);


