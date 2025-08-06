# Gerenciador de Caçambas

Sistema de gerenciamento de aluguel de caçambas desenvolvido em Spring Boot com PostgreSQL.

## 🚀 Status do Projeto

✅ **PostgreSQL funcionando no perfil default**  
✅ **Flyway ativado com migrações automáticas**  
✅ **Aplicação rodando na porta 8080**  
✅ **Docker Compose configurado**  

## 🛠️ Tecnologias

- **Backend:** Spring Boot 3.5.4
- **Banco de Dados:** PostgreSQL 15.13 (default) / H2 (dev)
- **Migrações:** Flyway
- **ORM:** Hibernate/JPA
- **Frontend:** Thymeleaf
- **Email:** Spring Mail (Gmail)
- **Agendamento:** Quartz Scheduler

## 📋 Pré-requisitos

- Java 17+
- Maven 3.6+
- Docker e Docker Compose
- PostgreSQL 15+ (opcional, Docker fornece)

## 🚀 Como Executar

### 1. Iniciar o Banco de Dados
```bash
docker-compose up -d
```

### 2. Executar a Aplicação

#### Perfil Default (PostgreSQL) - Recomendado
```bash
./mvnw spring-boot:run
```

#### Perfil de Desenvolvimento (H2)
```bash
./mvnw spring-boot:run "-Dspring.profiles.active=dev"
```

#### Perfil de Produção
```bash
./mvnw spring-boot:run "-Dspring.profiles.active=prod"
```

### 3. Acessar a Aplicação
- **URL:** http://localhost:8080
- **Console H2 (dev):** http://localhost:8080/h2-console

## 📊 Funcionalidades

### Gestão de Clientes
- Cadastro, edição e listagem de clientes
- Informações de contato

### Gestão de Caçambas
- Cadastro de caçambas com código único
- Controle de status (disponível, alugada, etc.)
- Capacidade em metros cúbicos

### Gestão de Aluguéis
- Registro de aluguéis com cliente e caçamba
- Controle de datas de início e fim
- Status do aluguel (ativo, finalizado, etc.)

### Notificações
- Email automático para vencimentos
- Relatórios semanais
- Configuração de SMTP (Gmail)

## 🗄️ Estrutura do Banco

### Tabelas Principais
- **cliente:** Informações dos clientes
- **cacamba:** Cadastro de caçambas
- **aluguel:** Registro de aluguéis

### Migrações
- **V1__init.sql:** Criação inicial das tabelas
- **Esquema:** BIGSERIAL para IDs (compatível com Long)

## ⚙️ Configurações

### Perfis Disponíveis

1. **Default (PostgreSQL):** Ambiente principal
2. **Dev (H2):** Desenvolvimento rápido
3. **Postgres:** PostgreSQL alternativo
4. **Prod:** Produção com variáveis de ambiente

### Variáveis de Ambiente (Produção)
```bash
export DATABASE_URL=jdbc:postgresql://seu-servidor:5432/cacamba
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha
export EMAIL_USERNAME=seu_email@gmail.com
export EMAIL_PASSWORD=sua_senha_app
```

## 🔧 Comandos Úteis

### Verificar status do banco
```bash
docker ps | grep cacamba_db
```

### Ver logs do PostgreSQL
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

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/eccolimp/cacamba_manager/
│   │       ├── controller/     # Controllers REST e UI
│   │       ├── domain/         # Entidades e repositórios
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── mapper/        # Mappers MapStruct
│   │       ├── notification/  # Serviços de email
│   │       └── security/      # Configurações de segurança
│   └── resources/
│       ├── db/migration/      # Migrações Flyway
│       ├── templates/         # Templates Thymeleaf
│       └── application-*.properties
```

## 🐛 Troubleshooting

### Problema: PostgreSQL não conecta
1. Verificar se o container está rodando: `docker ps`
2. Verificar logs: `docker logs cacamba_db`
3. Reiniciar: `docker-compose restart`

### Problema: Flyway não executa
1. Verificar dependências no pom.xml
2. Verificar configurações no application.properties
3. Limpar e recompilar: `./mvnw clean compile`

### Problema: Email não envia
1. Verificar configurações SMTP
2. Verificar credenciais do Gmail
3. Verificar App Password do Gmail

## 📝 Notas de Desenvolvimento

- **Flyway:** Ativado no perfil default com suporte completo ao PostgreSQL
- **Esquema:** BIGSERIAL para IDs, compatível com entidades JPA
- **Dependências:** Flyway PostgreSQL adicionada ao pom.xml
- **Configuração:** Todas as configurações estão alinhadas com a realidade atual

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. 