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


## Relatórios CSV – LazyInitializationException em produção

- Contexto: Em produção, `spring.jpa.open-in-view=false` (ver `application-prod.properties`). O endpoint `/admin/reports/alugueis.csv` gerava CSV acessando `aluguel.cliente` e `aluguel.cacamba` (ambos LAZY) após a sessão fechar, causando `LazyInitializationException`.
- Sintoma observado no log: `Could not initialize proxy [Cliente#<id>] - no session` em `CsvReportService.gerarCsvAlugueis` ao acessar `cliente.getNome()`.
- Correção aplicada:
  - `AluguelRepository`: override de `findAll(Specification<Aluguel>)` com `@EntityGraph(attributePaths = {"cliente", "cacamba"})` para carregar relacionamentos necessários ao CSV.
  - `ReportsController.downloadAlugueisCsv`: anotado com `@Transactional(readOnly = true)` como defesa adicional.
- Impacto: CSV funciona em produção mantendo a boa prática de Open-In-View desativado.
