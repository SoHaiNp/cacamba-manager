CREATE TABLE IF NOT EXISTS system_settings (
    id BIGSERIAL PRIMARY KEY,
    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    from_email VARCHAR(120),
    from_name VARCHAR(120),
    report_to_email VARCHAR(120),
    vencimento_time VARCHAR(5),
    relatorio_time VARCHAR(5),
    run_mon BOOLEAN NOT NULL DEFAULT TRUE,
    run_tue BOOLEAN NOT NULL DEFAULT TRUE,
    run_wed BOOLEAN NOT NULL DEFAULT TRUE,
    run_thu BOOLEAN NOT NULL DEFAULT TRUE,
    run_fri BOOLEAN NOT NULL DEFAULT TRUE,
    run_sat BOOLEAN NOT NULL DEFAULT TRUE,
    run_sun BOOLEAN NOT NULL DEFAULT TRUE,
    time_zone VARCHAR(50)
);

-- seed mínimo (apenas uma linha) se tabela estiver vazia
INSERT INTO system_settings (
    notifications_enabled, from_email, from_name, report_to_email,
    vencimento_time, relatorio_time, run_mon, run_tue, run_wed, run_thu, run_fri, run_sat, run_sun, time_zone
) SELECT TRUE, NULL, 'Gerenciador de Caçambas', NULL,
         '08:00', '09:00', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 'America/Sao_Paulo'
  WHERE NOT EXISTS (SELECT 1 FROM system_settings);


