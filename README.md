<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" />
  <img src="https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Tailwind%20CSS-4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" />
</p>

# 🎯 FlowPay — Sistema de Atendimento

Sistema completo de **gerenciamento de fila de atendimento** desenvolvido para a FlowPay. A aplicação distribui automaticamente os clientes para os atendentes corretos com base no assunto solicitado, organiza filas por time e oferece um dashboard em tempo real com métricas operacionais.

---

## 📑 Índice

- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar](#-como-executar)
  - [Com Docker (Recomendado)](#-com-docker-recomendado)
  - [Sem Docker (Manual)](#-sem-docker-manual)
- [Endpoints da API](#-endpoints-da-api)
- [Regras de Negócio](#-regras-de-negócio)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)

---

## ✨ Funcionalidades

| Recurso | Descrição |
|---------|-----------|
| 📞 **Novo Atendimento** | Cria um atendimento e identifica automaticamente o time responsável pelo assunto |
| 🔄 **Fila Inteligente** | Distribui clientes automaticamente para atendentes disponíveis ou enfileira quando nenhum está livre |
| 📊 **Dashboard em Tempo Real** | Métricas ao vivo com atualização a cada 5 segundos (atendimentos ativos, fila, disponibilidade) |
| 👥 **Gerenciamento de Atendentes** | Cadastro e remoção de atendentes com atribuição por time |
| ✅ **Finalizar Atendimento** | Encerra atendimento e libera o atendente para o próximo da fila automaticamente |
| 🌐 **WebSocket** | Suporte a atualizações em tempo real via STOMP/SockJS |

---

## 🏗 Arquitetura

```
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│                  │       │                  │       │                  │
│    Frontend      │◄─────►│    Backend       │◄─────►│   PostgreSQL     │
│  React + Vite    │ REST  │  Spring Boot     │  JPA  │      16          │
│  :5173           │  API  │  :8080           │       │  :5432           │
│                  │       │                  │       │                  │
└──────────────────┘       └──────────────────┘       └──────────────────┘
```

A aplicação utiliza uma arquitetura em **três camadas** totalmente containerizada com Docker Compose:

- **Frontend** — SPA em React 19 com TypeScript, estilizada com Tailwind CSS
- **Backend** — API REST com Spring Boot 3.5 e Java 21
- **Banco de Dados** — PostgreSQL 16 com persistência via volume Docker

---

## 🛠 Tecnologias

### Backend
- **Java 21** + **Spring Boot 3.5**
- Spring Data JPA / Hibernate
- Spring WebSocket (STOMP + SockJS)
- Bean Validation
- Lombok
- PostgreSQL Driver

### Frontend
- **React 19** + **TypeScript 5.9**
- Vite 7
- Tailwind CSS 4
- Axios

### Infraestrutura
- **Docker** + **Docker Compose**
- Multi-stage builds para imagens otimizadas

---

## 📋 Pré-requisitos

### Com Docker (Recomendado)
- [Docker](https://docs.docker.com/get-docker/) (v20+)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2+)

### Sem Docker
- [Java 21 (JDK)](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- [PostgreSQL 16](https://www.postgresql.org/download/)
- [Maven 3.9+](https://maven.apache.org/) (ou use o wrapper `mvnw` incluso)

---

## 🚀 Como Executar

### 🐳 Com Docker (Recomendado)

**Um único comando** sobe toda a aplicação:

```bash
docker compose up --build
```

Aguarde os containers iniciarem e acesse:

| Serviço     | URL                          |
|-------------|------------------------------|
| Frontend    | http://localhost:5173         |
| Backend API | http://localhost:8080/api     |
| PostgreSQL  | `localhost:5432`              |

Para rodar em segundo plano:

```bash
docker compose up --build -d
```

Para parar todos os containers:

```bash
docker compose down
```

Para parar e **remover os dados** do banco:

```bash
docker compose down -v
```

---

### 💻 Sem Docker (Manual)

#### 1. Banco de dados

Crie um banco PostgreSQL local:

```sql
CREATE DATABASE flowpay;
```

#### 2. Backend

```bash
cd backend

# Linux / macOS
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Windows
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

O perfil `dev` já está configurado para conectar em `localhost:5432/flowpay` com usuário `postgres`/`postgres`.

> A API ficará disponível em **http://localhost:8080**

#### 3. Frontend

```bash
cd frontend

npm install
npm run dev
```

> O frontend ficará disponível em **http://localhost:5173**

---

## 📡 Endpoints da API

### Atendimentos — `/api/atendimentos`

| Método | Rota                            | Descrição                        |
|--------|---------------------------------|----------------------------------|
| `POST` | `/api/atendimentos`             | Cria um novo atendimento         |
| `GET`  | `/api/atendimentos`             | Lista todos os atendimentos      |
| `GET`  | `/api/atendimentos/{id}`        | Busca atendimento por ID         |
| `PUT`  | `/api/atendimentos/{id}/finalizar` | Finaliza um atendimento       |

#### Criar atendimento — Exemplo

```bash
curl -X POST http://localhost:8080/api/atendimentos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteNome": "João Vitor",
    "assunto": "Problemas com cartão de crédito"
  }'
```

O sistema identifica automaticamente o time pelo assunto:
- Palavras como `cartão`, `cartao` → **Time Cartões**
- Palavras como `empréstimo`, `contratação` → **Time Empréstimos**
- Qualquer outro assunto → **Time Outros Assuntos**

### Atendentes — `/api/atendentes`

| Método | Rota                | Descrição                    |
|--------|---------------------|------------------------------|
| `POST` | `/api/atendentes`   | Cadastra um novo atendente   |
| `GET`  | `/api/atendentes`   | Lista todos os atendentes    |

#### Criar atendente — Exemplo

```bash
curl -X POST http://localhost:8080/api/atendentes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ana Paula",
    "tipoTime": "CARTOES"
  }'
```

Valores válidos para `tipoTime`: `CARTOES`, `EMPRESTIMOS`, `OUTROS_ASSUNTOS`

### Dashboard — `/api/dashboard`

| Método | Rota                      | Descrição                               |
|--------|---------------------------|-----------------------------------------|
| `GET`  | `/api/dashboard/metricas` | Retorna métricas gerais e por time      |

---

## 📏 Regras de Negócio

| Regra | Detalhamento |
|-------|--------------|
| **Limite por atendente** | Cada atendente pode ter no máximo **3 atendimentos simultâneos** |
| **Distribuição automática** | Ao criar um atendimento, o sistema busca um atendente disponível do time correspondente. Se houver, o atendimento inicia imediatamente |
| **Fila por time** | Se nenhum atendente do time está disponível, o atendimento entra na fila daquele time com status `AGUARDANDO` |
| **Liberação automática** | Ao finalizar um atendimento, o sistema verifica a fila do time e atribui automaticamente o próximo cliente ao atendente que ficou livre |
| **Identificação de time** | O assunto digitado pelo cliente é analisado por palavras-chave para direcionar ao time correto |
| **Dados iniciais** | Na primeira execução, 8 atendentes são criados automaticamente (3 Cartões, 3 Empréstimos, 2 Outros Assuntos) |

### Fluxo de um Atendimento

```
Cliente solicita atendimento
        │
        ▼
Sistema identifica o Time pelo assunto
        │
        ▼
Existe atendente disponível no time?
       / \
     Sim   Não
      │      │
      ▼      ▼
  Inicia   Entra na fila
 atendimento  (AGUARDANDO)
(EM_ATENDIMENTO)   │
      │            │
      ▼            │
  Finalizado ──────┘
      │     Próximo da fila
      ▼     é atribuído ao
   Concluído  atendente livre
```

---

## 📂 Estrutura do Projeto

```
atendimento-api/
├── docker-compose.yml              # Orquestração dos containers
│
├── backend/                         # API Spring Boot
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/flowpay/atendimento/
│       ├── AtendimentoApiApplication.java
│       ├── config/
│       │   ├── DataLoader.java          # Carga inicial de atendentes
│       │   ├── WebConfig.java           # Configuração CORS
│       │   └── WebSocketConfig.java     # Configuração WebSocket
│       ├── controller/
│       │   ├── AtendenteController.java
│       │   ├── AtendimentoController.java
│       │   └── DashboardController.java
│       ├── model/
│       │   ├── dto/
│       │   │   ├── request/             # DTOs de entrada
│       │   │   └── response/            # DTOs de saída
│       │   ├── entity/
│       │   │   ├── Atendente.java
│       │   │   └── Atendimento.java
│       │   └── enums/
│       │       ├── StatusAtendimento.java   # AGUARDANDO, EM_ATENDIMENTO, FINALIZADO
│       │       └── TipoTime.java            # CARTOES, EMPRESTIMOS, OUTROS_ASSUNTOS
│       ├── repository/
│       │   ├── AtendenteRepository.java
│       │   └── AtendimentoRepository.java
│       └── service/
│           ├── AtendenteService.java
│           ├── AtendimentoService.java
│           └── FilaService.java             # Gerenciamento de filas em memória
│
└── frontend/                        # SPA React
    ├── Dockerfile
    ├── package.json
    └── src/
        ├── App.tsx                          # Componente principal com navegação
        ├── components/
        │   ├── AtendentesList.tsx            # Lista de atendentes e status
        │   ├── AtendimentosEmAndamento.tsx   # Atendimentos ativos
        │   ├── FilaAtendimentos.tsx          # Visualização da fila
        │   ├── GerenciarAtendentes.tsx       # CRUD de atendentes
        │   ├── MetricsCard.tsx               # Card de métricas do dashboard
        │   └── NovoAtendimentoForm.tsx       # Formulário de novo atendimento
        ├── services/
        │   └── api.ts                       # Configuração Axios e serviços
        └── types/
            └── index.ts                     # Tipagens TypeScript
```

---

## ⚙ Variáveis de Ambiente

As variáveis abaixo podem ser configuradas no `docker-compose.yml` ou via arquivo `.env` na raiz do projeto:

| Variável          | Padrão      | Descrição                           |
|-------------------|-------------|-------------------------------------|
| `DB_HOST`         | `postgres`  | Host do banco de dados              |
| `DB_PORT`         | `5432`      | Porta do PostgreSQL                 |
| `DB_NAME`         | `flowpay`   | Nome do banco de dados              |
| `DB_USER`         | `postgres`  | Usuário do banco                    |
| `DB_PASSWORD`     | `postgres`  | Senha do banco                      |
| `JPA_DDL_AUTO`    | `update`    | Estratégia DDL do Hibernate         |
| `JPA_SHOW_SQL`    | `false`     | Exibir queries SQL no console       |
| `VITE_API_BASE_URL` | `http://localhost:8080/api` | URL base da API para o frontend |

---

## 🧪 Testes

```bash
cd backend

# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

---

## 📜 Licença

Este projeto foi desenvolvido como demonstração técnica para a **FlowPay**.

---

<p align="center">
  Feito com ☕ e 💻 por <strong>FlowPay Team</strong>
</p>
