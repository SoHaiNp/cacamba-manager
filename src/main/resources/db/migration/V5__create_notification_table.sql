CREATE TABLE notification (
  id BIGSERIAL PRIMARY KEY,
  type VARCHAR(40) NOT NULL,
  title VARCHAR(180) NOT NULL,
  message VARCHAR(400) NOT NULL,
  aluguel_id BIGINT,
  read_flag BOOLEAN NOT NULL DEFAULT FALSE,
  event_date DATE NOT NULL DEFAULT CURRENT_DATE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_created_at ON notification(created_at);
CREATE INDEX idx_notification_read ON notification(read_flag);


