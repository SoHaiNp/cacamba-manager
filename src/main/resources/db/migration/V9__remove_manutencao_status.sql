-- Atualiza registros antigos: MANUTENCAO -> DISPONIVEL
UPDATE cacamba SET status = 'DISPONIVEL' WHERE status = 'MANUTENCAO';



