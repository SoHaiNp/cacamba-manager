# Configuração de Banco de Dados

Este projeto está configurado para usar diferentes bancos de dados dependendo do perfil ativo.

## ✅ Situação Atual

**Status:** PostgreSQL funcionando corretamente no perfil default. Flyway ativado e migrações aplicadas com sucesso.

## Perfis Disponíveis

### 1. Perfil Padrão (sem perfil) - PostgreSQL ✅
**Comando:** `./mvnw spring-boot:run`
- **Banco:** PostgreSQL 15.13
- **Configuração:** `application.properties`
- **Uso:** Ambiente principal de desenvolvimento
- **Flyway:** Ativado com migrações automáticas
- **Status:** ✅ Funcionando

### 2. Perfil de Desenvolvimento - H2
**Comando:** `./mvnw spring-boot:run "-Dspring.profiles.active=dev"`
- **Banco:** H2 (em memória)
- **Configuração:** `application-dev.properties`
- **Uso:** Desenvolvimento rápido, testes
- **Flyway:** Desabilitado
- **Status:** ✅ Funcionando

### 3. Perfil PostgreSQL - PostgreSQL
**Comando:** `./mvnw spring-boot:run "-Dspring.profiles.active=postgres"`
- **Banco:** PostgreSQL
- **Configuração:** `application-postgres.properties`
- **Uso:** Ambiente de desenvolvimento alternativo
- **Flyway:** Ativado
- **Status:** ✅ Funcionando

### 4. Perfil de Produção - PostgreSQL
**Comando:** `./mvnw spring-boot:run "-Dspring.profiles.active=prod"`
- **Banco:** PostgreSQL
- **Configuração:** `application-prod.properties`
- **Uso:** Ambiente de produção
- **Flyway:** Ativado
- **Status:** ✅ Funcionando

## 🐘 Configuração do PostgreSQL

### Docker Compose
O PostgreSQL está configurado via Docker Compose:

```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    container_name: cacamba_db
    restart: unless-stopped
    environment:
      POSTGRES_DB: cacamba
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - dbdata:/var/lib/postgresql/data
```

### Para iniciar o banco:
```bash
docker-compose up -d
```

### Para parar o banco:
```bash
docker-compose down
```

## 🔧 Configurações Específicas

### Perfil Default (PostgreSQL)
- **URL:** `jdbc:postgresql://localhost:5432/cacamba?sslmode=disable`
- **Flyway:** Ativado com migrações automáticas
- **DDL:** `none` (Flyway gerencia o esquema)
- **Pool:** HikariCP configurado

### Perfil Dev (H2)
- **URL:** `jdbc:h2:mem:testdb`
- **Flyway:** Desabilitado
- **DDL:** `create-drop` (Hibernate gerencia o esquema)
- **Console:** Disponível em `/h2-console`

## 📊 Migrações do Banco

### Flyway (PostgreSQL)
- **Localização:** `src/main/resources/db/migration/`
- **Arquivo atual:** `V1__init.sql`
- **Status:** ✅ Aplicada com sucesso
- **Esquema:** BIGSERIAL para IDs (compatível com Long)

### Hibernate (H2)
- **DDL:** `create-drop`
- **Esquema:** Criado automaticamente pelo Hibernate

## 🚀 Deploy para Produção

### Variáveis de Ambiente
Para produção, configure as seguintes variáveis de ambiente:

```bash
export DATABASE_URL=jdbc:postgresql://seu-servidor:5432/cacamba
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha
export EMAIL_USERNAME=seu_email@gmail.com
export EMAIL_PASSWORD=sua_senha_app
```

### Comando de Deploy
```bash
./mvnw spring-boot:run "-Dspring.profiles.active=prod"
```

## 🔍 Troubleshooting

### Verificar status do PostgreSQL
```bash
docker ps | grep cacamba_db
```

### Verificar logs do PostgreSQL
```bash
docker logs cacamba_db
```

### Testar conexão
```bash
docker exec -it cacamba_db psql -U postgres -d cacamba
```

### Verificar migrações Flyway
```bash
./mvnw flyway:info
```

## 📝 Notas Importantes

1. **Perfil Default:** Agora usa PostgreSQL (não mais H2)
2. **Flyway:** Ativado no perfil default com suporte completo ao PostgreSQL
3. **Esquema:** BIGSERIAL para IDs, compatível com entidades JPA
4. **Dependências:** Flyway PostgreSQL adicionada ao pom.xml
5. **Configuração:** Todas as configurações estão alinhadas com a realidade atual 