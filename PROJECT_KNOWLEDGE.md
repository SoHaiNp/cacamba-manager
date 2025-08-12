## Admin Settings – Notificações

- Toggle "Ativar notificações" controla o envio automático (tarefas continuam agendadas, mas checam o flag antes de enviar). O botão "Testar envio" permanece ativo mesmo com o toggle desativado.
- Campos persistidos na tabela `system_settings`:
  - `notificationsEnabled`, `fromEmail`, `fromName`, `reportToEmail`
  - `vencimentoTime`, `relatorioTime`, `timeZone`
  - `runMon..runSun`
- Agendamento dinâmico:
  - `NotificationScheduler` recalcula crons quando as configurações são salvas (usa `TaskScheduler`).
  - Cron gerado de forma amigável a partir de horário (HH:mm) e dias da semana.
- Serviços:
  - `EmailService` usa `SystemSettingsService` para `from`/`from-name` e para checar se notificações estão habilitadas.
  - `NotificationService` resolve `reportToEmail` a partir das configurações.
- Página `admin/settings` agora envia POST com os campos e exibe lista de clientes com e-mail válido e filtro por nome.

Endpoints úteis:
- Testar envio: POST `/api/v1/notifications/test?email=<destino>`

