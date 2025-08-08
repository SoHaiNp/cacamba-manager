-- Adiciona coluna de data do evento às notificações existentes
ALTER TABLE notification
    ADD COLUMN IF NOT EXISTS event_date DATE;

-- Preenche valor para registros existentes com a data de criação
UPDATE notification SET event_date = CAST(created_at AS DATE) WHERE event_date IS NULL;

-- Garante NOT NULL e default para novos registros
ALTER TABLE notification
    ALTER COLUMN event_date SET NOT NULL,
    ALTER COLUMN event_date SET DEFAULT CURRENT_DATE;

-- Índice auxiliar (opcional) por data do evento
CREATE INDEX IF NOT EXISTS idx_notification_event_date ON notification(event_date);


