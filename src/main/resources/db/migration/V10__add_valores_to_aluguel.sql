-- Adiciona valores monetários e contador de trocas ao aluguel (compatível com H2)
ALTER TABLE aluguel ADD COLUMN IF NOT EXISTS valor_contrato NUMERIC(12,2) DEFAULT 0 NOT NULL;
ALTER TABLE aluguel ADD COLUMN IF NOT EXISTS valor_troca NUMERIC(12,2) DEFAULT 0 NOT NULL;
ALTER TABLE aluguel ADD COLUMN IF NOT EXISTS numero_trocas INTEGER DEFAULT 0 NOT NULL;

-- Remover DEFAULT depois para garantir obrigatoriedade em inserts futuros, se desejado

