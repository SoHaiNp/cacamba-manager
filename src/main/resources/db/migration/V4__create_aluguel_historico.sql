-- Tabela de histórico de aluguéis cancelados/arquivados
CREATE TABLE IF NOT EXISTS aluguel_historico (
  id                 BIGSERIAL PRIMARY KEY,
  aluguel_id         BIGINT      NOT NULL,
  cliente_id         BIGINT      NOT NULL,
  cacamba_id         BIGINT      NOT NULL,
  endereco           VARCHAR(180) NOT NULL,
  data_inicio        DATE        NOT NULL,
  data_fim           DATE        NOT NULL,
  status             VARCHAR(12) NOT NULL,
  data_arquivamento  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_aluguel_historico_aluguel_id ON aluguel_historico(aluguel_id);


