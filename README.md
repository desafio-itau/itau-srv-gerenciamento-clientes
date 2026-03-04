# Serviço de Gerenciamento de Clientes

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Conceito Geral do Microserviço](#-conceito-geral-do-microserviço)
- [Arquitetura e Integrações](#-arquitetura-e-integrações)
- [Configuração Inicial](#-configuração-inicial)
- [Domínio de Negócio](#-domínio-de-negócio)
- [Endpoints Principais](#-endpoints-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)

---

## 🎯 Visão Geral

O **Serviço de Gerenciamento de Clientes** é um microserviço responsável por gerenciar o ciclo de vida completo dos clientes que aderem ao produto de **investimento automático programado**. Este serviço atua como o ponto central de entrada para operações relacionadas a clientes, suas contas gráficas, acompanhamento de carteiras de investimento e análise de rentabilidade.

---

## 💡 Conceito Geral do Microserviço

### Propósito

Este microserviço é o **coração do sistema de investimento automático**, responsável por:

1. **Gestão de Adesões**: Permite que novos clientes se cadastrem no produto de investimento automático, definindo seu valor mensal de aporte.

2. **Administração de Contas Gráficas**: Cria e gerencia contas gráficas únicas para cada cliente no formato `ITAUFL00001`, `ITAUFL00002`, etc., onde os últimos 5 dígitos representam o ID do cliente de forma auto-incremental.

3. **Consulta de Carteiras**: Fornece uma visão consolidada da carteira de investimentos do cliente, incluindo:
   - Composição de ativos (ações)
   - Valores investidos vs valores atuais
   - Ganhos/perdas (P&L) por ativo e total
   - Percentual de participação de cada ativo na carteira
   - Rentabilidade percentual da carteira

4. **Análise de Rentabilidade**: Oferece relatórios detalhados sobre a performance dos investimentos, incluindo:
   - Histórico de aportes realizados
   - Evolução da carteira ao longo do tempo
   - Métricas de rentabilidade consolidadas

5. **Gestão de Snapshots**: Permite capturar "fotografias" periódicas do estado das carteiras dos clientes, possibilitando análises históricas e cálculos de rentabilidade temporal.

### Fluxo de Negócio

#### 1. Adesão ao Produto
```
Cliente → Adesão (POST /api/clientes/adesao)
    ↓
Validação de CPF (único)
    ↓
Criação do Cliente
    ↓
Geração de Conta Gráfica (ITAUFL00XXX)
    ↓
Cliente ativo no sistema
```

#### 2. Consulta de Carteira
```
Request (GET /api/clientes/{id}/carteira)
    ↓
Busca dados do Cliente e Conta Gráfica
    ↓
Integração com Serviço de Custódias (obtém posições de ativos)
    ↓
Integração com Serviço de Valores (obtém valores investidos/vendidos via Kafka)
    ↓
Cálculos de P&L, Rentabilidade e Composição
    ↓
Response com carteira consolidada
```

#### 3. Análise de Rentabilidade
```
Request (GET /api/clientes/{id}/rentabilidade)
    ↓
Busca Snapshots históricos do cliente
    ↓
Busca Histórico de Aportes (via Serviço de Valores/Kafka)
    ↓
Calcula evolução temporal da carteira
    ↓
Response com análise detalhada
```

### Regras de Negócio Importantes

- **Valor Mínimo de Aporte**: O cliente deve investir **no mínimo R$ 100,00 por mês**
- **CPF Único**: Cada CPF pode aderir ao produto apenas uma vez
- **Conta Gráfica Auto-incremental**: Gerada automaticamente no formato `ITAUFL + 5 dígitos zerofill do ID`
- **Status Ativo**: Clientes podem cancelar a adesão, mas suas posições em custódia são mantidas
- **Atualização de Valor Mensal**: Novos valores só são considerados a partir da próxima data de compra (dias 5, 15 ou 25)

---

## 🏗️ Arquitetura e Integrações

### Padrão Arquitetural

O serviço segue os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**:

```
Controller (API Layer)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Model (Domain Entities)
```

### Integrações Externas

#### 1. Serviço de Custódias (Feign Client)
- **Endpoint**: `/api/custodias/{clienteId}`
- **Propósito**: Obter as posições de custódia do cliente (ativos, quantidades, preços)
- **Comunicação**: REST via OpenFeign

#### 2. Serviço de Valores (Feign Client)
- **Endpoints**:
  - `/api/valores/{clienteId}` - Valores consolidados (investido/vendido)
  - `/api/valores?clienteId=X&data=Y` - Valores por data específica
  - `/api/valores/historico/{clienteId}` - Histórico de aportes
- **Propósito**: Consumir dados de operações financeiras processados via Kafka
- **Comunicação**: REST via OpenFeign

#### 3. Kafka (Integração Indireta)
O serviço **não consome diretamente do Kafka**, mas depende do **Serviço de Valores** que processa mensagens do tópico:
```json
{
  "tipo": "IR_DEDO_DURO",
  "clienteId": 1,
  "cpf": "12345678901",
  "ticker": "PETR4",
  "tipoOperacao": "COMPRA",
  "quantidade": 8,
  "precoUnitario": 35.00,
  "valorOperacao": 280.00,
  "dataOperacao": "2026-02-05T10:00:00Z"
}
```

### Banco de Dados

O serviço utiliza **PostgreSQL** com as seguintes entidades principais:

#### Tabela: `clientes`
```sql
- id (PK, auto-increment)
- nome
- cpf (UNIQUE)
- email
- valor_mensal (investimento mensal)
- ativo (boolean)
- data_adesao
```

#### Tabela: `contas_graficas`
```sql
- id (PK, auto-increment)
- cliente_id (FK → clientes)
- numero_conta (UNIQUE, formato: ITAUFL00001)
- tipo (ENUM: FILHOTE)
- data_criacao
```

#### Tabela: `snapshots_carteiras`
```sql
- id (PK, auto-increment)
- cliente_id
- data_snapshot
- valor_carteira
- valor_investido
- rentabilidade
- UNIQUE(cliente_id, data_snapshot)
```

---

## ⚙️ Configuração Inicial

### Pré-requisitos

1. **Java 17** ou superior
2. **Maven 3.8+**
3. **PostgreSQL 13+** rodando na porta 5433
4. **Common Library instalada localmente**

### ⚠️ Importante: Dependência da Common Library

Este projeto depende da biblioteca `common.library` versão `0.0.12` que deve estar **publicada no GitHub Packages** ou **instalada localmente** no seu repositório Maven.

```xml
<dependency>
    <groupId>com.itau</groupId>
    <artifactId>common.library</artifactId>
    <version>0.0.12</version>
</dependency>
```

**Se a biblioteca não estiver disponível**, você terá erros de compilação. Certifique-se de:

1. Ter as credenciais corretas para acessar o GitHub Packages, OU
2. Fazer o `mvn install` da common library localmente primeiro

A common library fornece:
- Exception handlers globais (`GlobalExceptionHandler`)
- Exceções customizadas (`NegocioException`, `RecursoNaoEncontradoException`)
- Controller genérico base (`ControllerGenerico`)
- Constantes e utilitários comuns

### Configuração de Variáveis de Ambiente

Crie o arquivo `env/.env` na raiz do projeto:

```bash
# Database
DESAFIO_ITAU_DB_NAME=desafio_itau_db
DESAFIO_ITAU_DB_USER=postgres
DESAFIO_ITAU_DB_PASSWORD=postgres

# External Services
ITAU_SRV_CUSTODIA_URL=http://localhost:8082/api/custodias
ITAU_SRV_VALORES_URL=http://localhost:8082/api/valores
```

### Configuração do GitHub Packages (se necessário)

Se você precisar acessar dependências do GitHub Packages, configure o `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>SEU_USERNAME</username>
      <password>SEU_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

### Portas Utilizadas

- **8080**: Aplicação principal
- **5433**: PostgreSQL
- **9092**: Kafka (usado por outros serviços)
- **8082**: Serviço de Custódias/Valores (dependência externa)

---

## 📦 Domínio de Negócio

### Entidades Principais

#### Cliente
Representa o investidor que aderiu ao produto de investimento automático.
- Contém dados cadastrais (nome, CPF, email)
- Define o valor mensal de aporte
- Pode estar ativo ou inativo

#### Conta Gráfica
Conta de investimento única associada ao cliente.
- Formato: `ITAUFL00001`, `ITAUFL00002`, etc.
- Tipo fixo: `FILHOTE`
- Gerada automaticamente na adesão

#### Snapshot de Carteira
Registro histórico do estado da carteira em uma data específica.
- Permite análises temporais
- Armazena valor da carteira, valor investido e rentabilidade
- Utilizado para gerar relatórios de evolução

### Services (Camada de Negócio)

#### ClienteService
- Adesão de novos clientes
- Cancelamento de adesão
- Alteração do valor mensal de investimento
- Listagem de clientes ativos

#### ContaGraficaService
- Criação de contas gráficas
- Geração de números de conta auto-incrementais
- Consulta de contas

#### CarteiraService
- Consulta consolidada da carteira do cliente
- Cálculos de P&L (Profit & Loss)
- Cálculos de rentabilidade
- Geração de snapshots periódicos

#### RentabilidadeService
- Análise detalhada de rentabilidade
- Histórico de aportes
- Evolução temporal da carteira

---

## 🔌 Endpoints Principais

### 1. Aderir ao Produto
```http
POST /api/clientes/adesao
Content-Type: application/json

{
  "nome": "João Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "valorMensal": 500.00
}
```

### 2. Cancelar Adesão
```http
POST /api/clientes/{clienteId}/saida
```

### 3. Alterar Valor Mensal
```http
PUT /api/clientes/{clienteId}/valor-mensal
Content-Type: application/json

{
  "novoValorMensal": 800.00
}
```

### 4. Listar Clientes Ativos
```http
GET /api/clientes
```

### 5. Consultar Carteira
```http
GET /api/clientes/{clienteId}/carteira
```

**Response Example:**
```json
{
  "clienteId": 1,
  "nome": "João Silva",
  "contaGrafica": "ITAUFL00001",
  "dataConsulta": "2026-03-01T10:30:00",
  "resumo": {
    "valorTotalInvestido": 5000.00,
    "valorAtualCarteira": 5750.50,
    "plTotal": 750.50,
    "rentabilidadePercentual": 15.01
  },
  "ativos": [
    {
      "ticker": "PETR4",
      "quantidade": 100,
      "precoMedio": 30.00,
      "cotacaoAtual": 35.00,
      "pl": 500.00,
      "plPercentual": 0.67,
      "composicaoCarteira": 0.60
    }
  ]
}
```

### 6. Gerar Snapshot
```http
POST /api/clientes/carteiras-snapshots?data=2026-03-01
```

### 7. Consultar Rentabilidade Detalhada
```http
GET /api/clientes/{clienteId}/rentabilidade
```

### 8. Consultar Conta Gráfica
```http
GET /api/contas-graficas/{id}
```

---

## 🛠️ Tecnologias Utilizadas

### Core
- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Data JPA** - Persistência de dados
- **Spring Validation** - Validação de dados de entrada

### Comunicação
- **Spring Cloud OpenFeign** - Cliente REST para comunicação entre microserviços
- **Spring Kafka** - Integração com Kafka (para consumo de eventos de operações)

### Banco de Dados
- **PostgreSQL** - Banco de dados relacional
- **Hibernate** - ORM

### Documentação
- **SpringDoc OpenAPI 3** - Documentação Swagger da API
- Acesse: `http://localhost:8080/swagger-ui.html`

### Qualidade de Código
- **JaCoCo** - Cobertura de testes unitários (mínimo 90%)
- **Lombok** - Redução de boilerplate

### Testes
- **Spring Boot Test** - Framework de testes
- **Mockito** - Mocks para testes unitários
- **H2 Database** - Banco em memória para testes

---

## 📊 Métricas e Qualidade

### Cobertura de Testes

O projeto está configurado com **JaCoCo** para garantir qualidade de código:

```bash
mvn clean verify
```

- **Cobertura mínima exigida**: 90%
- **Escopo da cobertura**: `controller`, `service`, `validator`, `mapper`
- **Exclusões**: `model`, `dto`, `repository`, `feign`, `exception`, `annotation`

### CI/CD

O projeto possui pipeline de CI/CD configurado que:
- Executa build Maven
- Roda testes unitários
- Valida cobertura de código (falha se < 90%)
- Pode ser executado manualmente em qualquer branch

---
