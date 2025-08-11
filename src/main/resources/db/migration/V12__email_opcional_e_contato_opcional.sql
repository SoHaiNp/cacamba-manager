-- Tornar contato opcional e permitir email opcional utilizando valor placeholder '-'

-- 1) Tornar contato NULLABLE
ALTER TABLE cliente ALTER COLUMN contato DROP NOT NULL;

-- 2) Remover constraint única antiga do email, se existir
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'cliente' AND c.conname = 'uk_cliente_email'
    ) THEN
        ALTER TABLE cliente DROP CONSTRAINT uk_cliente_email;
    END IF;
END $$;

-- 3) Garantir email NOT NULL
ALTER TABLE cliente ALTER COLUMN email SET NOT NULL;

-- 4) Preencher '-' quando email estiver vazio ou nulo
UPDATE cliente SET email = '-' WHERE email IS NULL OR email = '';

-- 5) Criar índice único parcial que ignora email '-'
CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_email_not_dash ON cliente (email) WHERE email <> '-';

