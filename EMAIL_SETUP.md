# 📧 Configuração do Sistema de Notificações por Email

## 🚀 Funcionalidades Implementadas

### ✅ **Notificações Automáticas**
- **Notificação de Vencimento**: Enviada automaticamente todos os dias às 8:00
- **Confirmação de Aluguel**: Enviada automaticamente quando um novo aluguel é registrado
- **Relatório Semanal**: Enviado automaticamente toda segunda-feira às 9:00

### ✅ **Templates HTML Profissionais**
- Design responsivo e moderno
- Cores personalizadas da empresa
- Informações detalhadas e organizadas
- Links de contato e instruções

## ⚙️ Configuração do Gmail

### 1. **Habilitar Autenticação de 2 Fatores**
1. Acesse sua conta Google
2. Vá em "Segurança" → "Verificação em duas etapas"
3. Ative a verificação em duas etapas

### 2. **Gerar Senha de App**
1. Ainda em "Segurança"
2. Clique em "Senhas de app"
3. Selecione "Email" como aplicativo
4. Copie a senha gerada (16 caracteres)

### 3. **Configurar Variáveis de Ambiente**
Crie um arquivo `.env` na raiz do projeto:

```bash
EMAIL_USERNAME=seu-email@gmail.com
EMAIL_PASSWORD=sua-senha-de-app-16-caracteres
```

Ou configure diretamente no `application.properties`:

```properties
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-de-app-16-caracteres
```

## 🧪 Testando o Sistema

### **1. Teste Manual via API**
```bash
# Testar envio de email
curl -X POST "http://localhost:8080/api/v1/notifications/test?email=destino@exemplo.com"

# Forçar notificações de vencimento
curl -X POST "http://localhost:8080/api/v1/notifications/vencimento/forcar"

# Forçar relatório semanal
curl -X POST "http://localhost:8080/api/v1/notifications/relatorio/forcar"
```

### **2. Teste via Postman**
- **POST** `http://localhost:8080/api/v1/notifications/test?email=seu-email@gmail.com`

## 📅 Agendamento de Tarefas

### **Cronograma Automático**
- **Notificações de Vencimento**: Todos os dias às 8:00
- **Relatório Semanal**: Toda segunda-feira às 9:00

### **Configuração de Horários**
Para alterar os horários, edite o arquivo `NotificationService.java`:

```java
// Notificações diárias às 8:00
@Scheduled(cron = "0 0 8 * * ?")

// Relatório semanal às 9:00 de segunda
@Scheduled(cron = "0 0 9 ? * MON")
```

## 🔧 Configurações Avançadas

### **Desabilitar Notificações**
```properties
app.notification.email.enabled=false
```

### **Alterar Email Remetente**
```properties
app.notification.email.from=outro-email@gmail.com
app.notification.email.from-name=Nome da Empresa
```

### **Configurar Email de Destino do Relatório**
Defina o email que receberá o relatório semanal:
```properties
app.notification.email.report-to=gerente@empresa.com
```

## 📧 Tipos de Email

### **1. Notificação de Vencimento**
- **Assunto**: "URGENTE: Seu aluguel vence HOJE!"
- **Conteúdo**: Detalhes do aluguel, dias restantes, informações de contato
- **Prioridade**: Alta (vermelho para urgente)

### **2. Confirmação de Aluguel**
- **Assunto**: "Confirmação de Aluguel - Caçamba #CÓDIGO"
- **Conteúdo**: Detalhes completos do aluguel, instruções importantes
- **Prioridade**: Normal (verde para sucesso)

### **3. Relatório Semanal**
- **Assunto**: "Relatório Semanal - Aluguéis Ativos"
- **Conteúdo**: Estatísticas, resumo da semana, ações recomendadas
- **Prioridade**: Informativo (azul)

## 🛠️ Solução de Problemas

### **Erro: "Authentication failed"**
- Verifique se a senha de app está correta
- Confirme se a autenticação de 2 fatores está ativa
- Teste o login no Gmail com a senha de app

### **Erro: "Connection timeout"**
- Verifique a conexão com a internet
- Confirme se o firewall não está bloqueando
- Teste a conectividade com smtp.gmail.com

### **Emails não estão sendo enviados**
- Verifique os logs da aplicação
- Confirme se `app.notification.email.enabled=true`
- Teste via endpoint `/api/v1/notifications/test`

### **Templates não estão sendo renderizados**
- Verifique se os arquivos HTML estão em `src/main/resources/templates/email/`
- Confirme se o Thymeleaf está configurado corretamente
- Verifique se as variáveis estão sendo passadas corretamente

## 📊 Monitoramento

### **Logs Importantes**
```bash
# Sucesso
"Notificação de vencimento enviada para email@exemplo.com - Aluguel #123"

# Erro
"Erro ao enviar notificação de vencimento para aluguel #123"

# Desabilitado
"Notificações por email desabilitadas"
```

### **Métricas**
- Total de emails enviados
- Taxa de sucesso/falha
- Tempo de entrega
- Emails rejeitados

## 🔒 Segurança

### **Boas Práticas**
- ✅ Use senhas de app (não senha da conta)
- ✅ Configure autenticação de 2 fatores
- ✅ Use variáveis de ambiente para credenciais
- ✅ Monitore logs de acesso
- ✅ Configure rate limiting se necessário

### **Configurações de Segurança**
```properties
# Timeout de conexão
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# TLS obrigatório
spring.mail.properties.mail.smtp.starttls.required=true
```

## 🎯 Próximos Passos

### **Melhorias Sugeridas**
1. **Sistema de Retry**: Reenviar emails que falharam
2. **Templates Dinâmicos**: Personalizar por cliente
3. **Notificações Push**: Integrar com navegador
4. **WhatsApp**: Adicionar canal alternativo
5. **Relatórios Avançados**: Métricas detalhadas

### **Integrações Futuras**
- Sistema de pagamento
- GPS para rastreamento
- App mobile
- Dashboard em tempo real

---

**📞 Suporte**: Para dúvidas ou problemas, entre em contato com a equipe de desenvolvimento. 