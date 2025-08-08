-- Remove usuário admin seedado por migrations antigas, para padronizar bootstrap em runtime
-- Mantém apenas a criação via AdminBootstrapRunner

-- Apaga especificamente o admin padrão usado nas migrations (username/email conhecidos)
DELETE FROM usuarios 
WHERE username = 'admin' 
   OR email = 'admin@cacamba.com';


