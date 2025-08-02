-- Cliente
CREATE TABLE cliente (
  id        SERIAL PRIMARY KEY,
  nome      VARCHAR(120) NOT NULL,
  contato   VARCHAR(120)
);

-- Cacamba
CREATE TABLE cacamba (
  id               SERIAL PRIMARY KEY,
  codigo           VARCHAR(30)  NOT NULL,
  capacidade_m3    INTEGER      NOT NULL,
  status           VARCHAR(12)  NOT NULL,
  CONSTRAINT uk_codigo UNIQUE (codigo)
);

-- Aluguel
CREATE TABLE aluguel (
  id          SERIAL PRIMARY KEY,
  cliente_id  INTEGER NOT NULL REFERENCES cliente(id),
  cacamba_id  INTEGER NOT NULL REFERENCES cacamba(id),
  endereco    VARCHAR(180) NOT NULL,
  data_inicio DATE    NOT NULL,
  data_fim    DATE    NOT NULL
);

CREATE INDEX idx_data_fim ON aluguel(data_fim);
