# Instruções para Importação em Massa

Este documento explica como organizar e importar dados em massa no sistema de Gerenciamento de Caçambas.

## 📋 Preparação dos Arquivos CSV

### ⚠️ **IMPORTANTE**: Ordem de Importação
Para evitar erros, importe os arquivos na seguinte ordem:
1. **Clientes** (primeiro)
2. **Caçambas** (segundo) 
3. **Contratos** (por último)

**Motivo**: Os contratos dependem de clientes e caçambas já existentes no sistema.

---

## 📊 1. CSV de Clientes

### Estrutura das Colunas:
```csv
nome,contato,email
```

### Campos:
- **nome** (obrigatório): Nome completo do cliente (máximo 120 caracteres)
- **contato** (opcional): Telefone ou celular (máximo 120 caracteres)
- **email** (opcional): Email do cliente (máximo 120 caracteres)

### Exemplo:
```csv
nome,contato,email
João Silva,(11) 99999-9999,joao.silva@email.com
Maria Santos,(11) 88888-8888,maria.santos@email.com
Pedro Oliveira,(11) 77777-7777,pedro.oliveira@email.com
```

### Regras:
- O nome é obrigatório
- Contato e email são opcionais
- Se o email estiver vazio, será preenchido com "-"
- Não pode haver nomes duplicados

---

## 📦 2. CSV de Caçambas

### Estrutura das Colunas:
```csv
codigo,capacidadeM3
```

### Campos:
- **codigo** (obrigatório): Código único da caçamba (máximo 10 caracteres)
- **capacidadeM3** (obrigatório): Capacidade em metros cúbicos (número inteiro > 0)

### Exemplo:
```csv
codigo,capacidadeM3
C001,3
C002,6
C003,10
C004,3
C005,6
```

### Regras:
- O código deve ser único (não pode haver duplicatas)
- A capacidade deve ser um número inteiro maior que zero
- Todas as caçambas são criadas com status "DISPONIVEL"

---

## 📄 3. CSV de Contratos (Aluguéis)

### Estrutura das Colunas:
```csv
cliente_nome,cacamba_codigo,endereco,data_inicio,data_fim,valor_contrato,valor_troca,numero_trocas
```

### Campos:
- **cliente_nome** (obrigatório): Nome exato do cliente (deve existir no sistema)
- **cacamba_codigo** (obrigatório): Código exato da caçamba (deve existir no sistema)
- **endereco** (obrigatório): Endereço completo do aluguel (máximo 180 caracteres)
- **data_inicio** (obrigatório): Data de início no formato YYYY-MM-DD
- **data_fim** (obrigatório): Data de fim no formato YYYY-MM-DD
- **valor_contrato** (obrigatório): Valor do contrato no formato 0.00
- **valor_troca** (obrigatório): Valor por troca no formato 0.00
- **numero_trocas** (obrigatório): Número de trocas realizadas (número inteiro ≥ 0)

### Exemplo:
```csv
cliente_nome,cacamba_codigo,endereco,data_inicio,data_fim,valor_contrato,valor_troca,numero_trocas
João Silva,C001,Rua das Flores, 123 - Vila Madalena - São Paulo,2024-01-01,2024-01-31,150.00,50.00,0
Maria Santos,C002,Av Paulista, 456 - Bela Vista - São Paulo,2024-01-15,2024-02-15,200.00,75.00,0
```

### Regras:
- **cliente_nome** deve corresponder exatamente a um cliente existente
- **cacamba_codigo** deve corresponder exatamente a uma caçamba existente
- **data_inicio** deve ser anterior ou igual a **data_fim**
- **valor_contrato** e **valor_troca** devem ser números decimais positivos
- **numero_trocas** deve ser um número inteiro ≥ 0
- O sistema verificará se a caçamba está disponível no período solicitado
- **Total Troca** será calculado automaticamente: `valor_troca × numero_trocas`
- **Valor Total** será calculado automaticamente: `valor_contrato + total_trocas`

---

## 🚀 Como Importar

### 1. Acesse o Sistema
- Faça login como administrador
- Vá para **Admin** → **Importação em Massa**

### 2. Baixe os Templates
- Clique em "Baixar Template" para cada tipo de importação
- Use os templates como base para seus arquivos

### 3. Prepare seus Arquivos
- Abra o Excel com seus dados
- Organize as colunas conforme os templates
- Salve como CSV (UTF-8)

### 4. Importe na Ordem Correta
1. **Clientes**: Selecione o arquivo e clique em "Importar"
2. **Caçambas**: Selecione o arquivo e clique em "Importar"
3. **Contratos**: Selecione o arquivo e clique em "Importar"

### 5. Verifique os Resultados
- O sistema mostrará quantos registros foram importados com sucesso
- Verifique se não houve erros

---

## ⚠️ Problemas Comuns e Soluções

### Erro: "Cliente não encontrado"
- **Causa**: O nome do cliente no CSV não corresponde exatamente ao nome cadastrado
- **Solução**: Verifique se o nome está escrito exatamente igual (incluindo acentos e espaços)

### Erro: "Caçamba não encontrada"
- **Causa**: O código da caçamba no CSV não corresponde exatamente ao código cadastrado
- **Solução**: Verifique se o código está escrito exatamente igual (incluindo maiúsculas/minúsculas)

### Erro: "Caçamba não está disponível no período"
- **Causa**: A caçamba já está alugada no período solicitado
- **Solução**: Verifique as datas ou escolha outra caçamba disponível

### Erro: "Código já utilizado"
- **Causa**: Tentativa de importar uma caçamba com código duplicado
- **Solução**: Verifique se o código já existe no sistema ou altere o código

---

## 📝 Dicas Importantes

1. **Sempre teste com poucos registros primeiro**
2. **Verifique se não há espaços extras nas células**
3. **Use o formato de data correto (YYYY-MM-DD)**
4. **Mantenha backup dos seus dados originais**
5. **Verifique se os valores monetários estão no formato correto (0.00)**

---

## 🔧 Suporte

Se encontrar problemas durante a importação:
1. Verifique as mensagens de erro exibidas pelo sistema
2. Confirme se os dados estão no formato correto
3. Teste com um arquivo menor primeiro
4. Entre em contato com o suporte técnico se necessário
