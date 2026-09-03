-- Adicionar campos para regra D+1
ALTER TABLE aluguel ADD COLUMN data_entrega DATE;
ALTER TABLE aluguel ADD COLUMN prazo_dias INTEGER;

-- Comentário explicativo
COMMENT ON COLUMN aluguel.data_entrega IS 'Data de entrega da caçamba (regra D+1: dataInicio = dataEntrega + 1)';
COMMENT ON COLUMN aluguel.prazo_dias IS 'Prazo em dias do aluguel (regra D+1: dataFim = dataInicio + (prazo - 1))';
