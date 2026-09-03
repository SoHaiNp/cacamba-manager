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


## Etapa 1 — Ordenação padrão e link "Ver todos" (Implementado)

### Objetivo
Listar contratos por vencimento ascendente (vencidos → hoje → futuros) com desempate por nome do cliente. O link "ver todos" na Home deve abrir `/ui/alugueis?situacao=vencendo`.

### Implementações Realizadas

#### 1. **Enum SituacaoVencimento**
- Adicionado novo valor `VENCENDO` para representar aluguéis que estão vencendo (vencidos, hoje e próximos dias)

#### 2. **AluguelSpecifications**
- Implementada nova specification `vencendo()` que combina:
  - Aluguéis vencidos (dataFim < hoje)
  - Aluguéis que vencem hoje (dataFim = hoje)  
  - Aluguéis que vencem nos próximos 7 dias (dataFim <= hoje + 7 dias)
- Filtra apenas aluguéis com status `ATIVO`

#### 3. **AluguelService**
- Modificado método `listarFiltrado()` para usar ordenação padrão:
  - **Primária**: `dataFim` ascendente (vencidos → hoje → futuros)
  - **Secundária**: `cliente.nome` ascendente (desempate alfabético)
- Adicionado case `VENCENDO` no switch que aplica a specification correspondente

#### 4. **Templates**
- **Dashboard (`dashboard/index.html`)**: Link "Ver todos" agora aponta para `/ui/alugueis?situacao=VENCENDO`
- **Lista de Aluguéis (`aluguel/list.html`)**: Adicionada opção "Vencendo" no filtro de situação

### Comportamento Resultante
- **Ordenação padrão**: Aluguéis aparecem ordenados por data de vencimento (mais antigos primeiro) e, em caso de empate, por nome do cliente
- **Link "Ver todos"**: Direciona para lista filtrada mostrando apenas aluguéis vencendo (vencidos + hoje + próximos 7 dias)
- **Filtro manual**: Usuários podem selecionar "Vencendo" no filtro de situação para ver a mesma lista

### Arquivos Modificados
- `src/main/java/com/eccolimp/cacamba_manager/dto/SituacaoVencimento.java`
- `src/main/java/com/eccolimp/cacamba_manager/domain/repository/spec/AluguelSpecifications.java`
- `src/main/java/com/eccolimp/cacamba_manager/domain/service/AluguelService.java`
- `src/main/resources/templates/dashboard/index.html`
- `src/main/resources/templates/aluguel/list.html`
- `PROJECT_KNOWLEDGE.md` - Documentação da implementação
- `PROJECT_OVERVIEW.md` - Diagrama UML da solução


## Correção de Erros de Compilação nos Testes

### Problemas Identificados
Após a implementação da Etapa 1, foram encontrados erros de compilação nos testes relacionados a:
- Imports faltando ou incorretos
- Tipos incompatíveis em `NovoAluguelRequest`
- Classes não encontradas (`EmailService`, `AlertasVencimentoDTO`)

### Correções Aplicadas

#### 1. **AluguelServiceTest.java**
- Corrigidos imports e tipos incompatíveis
- Substituído uso direto de `NovoAluguelRequest` por variáveis locais
- Ajustados nomes de métodos de teste para maior clareza

#### 2. **NotificationServiceTest.java**
- Adicionado import `ArgumentCaptor` do Mockito
- Corrigidos imports de DTOs e serviços
- Mantida funcionalidade dos testes de notificação

#### 3. **NotificationFlowTest.java**
- Verificado que os imports estavam corretos
- Mantida estrutura original do teste

### Resultado
- ✅ Compilação bem-sucedida (`./mvnw clean compile`)
- ✅ Aplicação executando corretamente (`./mvnw spring-boot:run`)
- ✅ Endpoint `/ui` respondendo com status 200
- ✅ Todos os testes passando

### Arquivos Corrigidos
- `src/test/java/com/eccolimp/cacamba_manager/AluguelServiceTest.java`
- `src/test/java/com/eccolimp/cacamba_manager/notification/NotificationServiceTest.java`
- `src/test/java/com/eccolimp/cacamba_manager/notification/NotificationFlowTest.java`


## Etapa 2 — Regra D+1 (dataInicio/dataFim) via Entity Listener (Implementado)

### Objetivo
Implementar a regra D+1 automaticamente via Entity Listener do JPA:
- `dataInicio = dataEntrega + 1`
- `dataFim = dataInicio + (prazo - 1)`

### Implementações Realizadas

#### 1. **Novos Campos na Entidade Aluguel**
- Adicionado campo `dataEntrega` (LocalDate) - data de entrega da caçamba
- Adicionado campo `prazoDias` (Integer) - prazo em dias do aluguel
- Ambos campos são opcionais para manter compatibilidade

#### 2. **AluguelLifecycle - Entity Listener**
- Criado listener `AluguelLifecycle` com anotações `@PrePersist` e `@PreUpdate`
- Método `applyDmais1()` aplica automaticamente a regra D+1
- Resolução flexível do prazo via reflection: `getPrazoDias()`, `getPrazo()`, `getDias()`
- Validações de segurança (null checks, prazo > 0)

#### 3. **Integração com JPA**
- Entidade `Aluguel` anotada com `@EntityListeners(AluguelLifecycle.class)`
- Listener executado automaticamente antes de persistir/atualizar
- Regra aplicada de forma transparente para o código de negócio

#### 4. **Migração de Banco**
- Criada migração `V15__add_data_entrega_and_prazo_dias.sql`
- Adiciona colunas `data_entrega` e `prazo_dias` à tabela `aluguel`
- Comentários explicativos sobre a regra D+1

### Comportamento Resultante
- **Automático**: Regra D+1 aplicada automaticamente via Entity Listener
- **Flexível**: Suporte a diferentes nomes de getters para prazo
- **Seguro**: Validações para evitar cálculos incorretos
- **Integrado**: Sistema adaptado para usar a regra D+1 no método `registrar()`

### Como Funciona na Prática
1. **Frontend**: Usuário informa `dataInicio` (ex: 27/08/2025) e `dias` (ex: 7)
2. **Backend**: Sistema calcula automaticamente:
   - `dataEntrega = dataInicio - 1` (ex: 26/08/2025)
   - `prazoDias = dias` (ex: 7)
3. **Entity Listener**: `AluguelLifecycle` aplica a regra D+1:
   - `dataInicio = dataEntrega + 1` (ex: 27/08/2025)
   - `dataFim = dataInicio + (prazo - 1)` (ex: 02/09/2025)
4. **Resultado**: Aluguel criado com datas corretas conforme regra D+1

### Arquivos Modificados/Criados
- `src/main/java/com/eccolimp/cacamba_manager/domain/AluguelLifecycle.java` (novo)
- `src/main/java/com/eccolimp/cacamba_manager/domain/model/Aluguel.java` (modificado)
- `src/main/java/com/eccolimp/cacamba_manager/domain/service/AluguelService.java` (modificado - método registrar)
- `src/main/resources/db/migration/V15__add_data_entrega_and_prazo_dias.sql` (novo)
- `PROJECT_KNOWLEDGE.md` - Documentação da implementação
