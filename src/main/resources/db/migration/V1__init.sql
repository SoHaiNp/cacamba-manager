-- Cliente
CREATE TABLE cliente (
  id        BIGSERIAL PRIMARY KEY,
  nome      VARCHAR(120) NOT NULL,
  contato   VARCHAR(120) NOT NULL
);

-- Cacamba
CREATE TABLE cacamba (
  id               BIGSERIAL PRIMARY KEY,
  codigo           VARCHAR(10)  NOT NULL,
  capacidadem3     INTEGER      NOT NULL,
  status           VARCHAR(12)  NOT NULL,
  CONSTRAINT uk_codigo UNIQUE (codigo)
);

-- Aluguel
CREATE TABLE aluguel (
  id          BIGSERIAL PRIMARY KEY,
  cliente_id  BIGINT NOT NULL REFERENCES cliente(id),
  cacamba_id  BIGINT NOT NULL REFERENCES cacamba(id),
  endereco    VARCHAR(180) NOT NULL,
  data_inicio DATE    NOT NULL,
  data_fim    DATE    NOT NULL,
  status      VARCHAR(12) NOT NULL DEFAULT 'ATIVO'
);

CREATE INDEX idx_data_fim ON aluguel(data_fim);
