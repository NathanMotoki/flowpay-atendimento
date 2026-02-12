# 🏦 FlowPay - Sistema de Distribuição de Atendimentos

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18+-blue)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5+-blue)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)

> Sistema Full Stack enterprise-grade para gerenciamento inteligente de atendimentos em fintech, implementando distribuição automática, sistema de filas FIFO e monitoramento em tempo real.

---

## 📋 Sobre o Projeto

**Contexto:** Desafio técnico para vaga de **Desenvolvedor Sênior Full Stack** na Ubots.

**Problema:** A FlowPay precisa estruturar sua central de relacionamento com distribuição eficiente de atendimentos entre 3 times especializados (Cartões, Empréstimos, Outros Assuntos), respeitando o limite de 3 atendimentos simultâneos por atendente.

**Solução:** Sistema que automatiza completamente a distribuição, implementando regras de negócio complexas, filas de espera FIFO e redistribuição automática ao finalizar atendimentos.

---

## 🎯 Funcionalidades

### Core Business
- ✅ **Distribuição Inteligente**: Identifica automaticamente o time correto baseado no assunto
- ✅ **Controle de Capacidade**: Limite de 3 atendimentos simultâneos por atendente
- ✅ **Sistema de Filas FIFO**: Implementação thread-safe com `LinkedList` + `ConcurrentHashMap`
- ✅ **Redistribuição Automática**: Ao finalizar, o próximo da fila é automaticamente atribuído
- ✅ **Balanceamento de Carga**: Query customizada distribui do menos ocupado para o mais ocupado

### Qualidade & Robustez
- ✅ **Validações em Múltiplas Camadas**: Bean Validation + validações de negócio
- ✅ **Tratamento de Exceções Centralizado**: GlobalExceptionHandler com respostas padronizadas
- ✅ **Testes Unitários**: 25+ testes com JUnit 5 + Mockito
- ✅ **Logs Estruturados**: SLF4J + Lombok para rastreabilidade
- ✅ **Type Safety**: TypeScript no frontend + Records Java 17+ no backend

---

## 🏗️ Arquitetura

### Visão Geral
```
┌──────────────┐    HTTP/REST    ┌─────────────────┐
│   React +    │ ◄─────────────► │  Spring Boot    │
│  TypeScript  │                 │      API        │
└──────────────┘                 └────────┬────────┘
                                          │ JDBC
                                          ▼
                                 ┌─────────────────┐
                                 │   PostgreSQL    │
                                 └─────────────────┘
```

### Backend - Camadas
```
Controllers (REST API + Exception Handling)
     ↓
Services (Business Logic + Validation)
     ↓
Repositories (Data Access + Custom Queries)
     ↓
Database (PostgreSQL)
```

---

## 🛠️ Stack Tecnológica

### Backend
| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 17 | Records, Pattern Matching |
| Spring Boot | 3.5.10 | Framework base |
| Spring Data JPA | 3.5.8 | Persistência |
| PostgreSQL | 16 | Banco de dados |
| Lombok | 1.18.x | Redução boilerplate |
| JUnit 5 + Mockito | 5.x | Testes |

### Frontend
| Tecnologia | Versão | Uso |
|------------|--------|-----|
| React | 18+ | UI Library |
| TypeScript | 5+ | Type Safety |
| Vite | 5+ | Build Tool |
| Axios | 1.x | HTTP Client |
| Tailwind CSS | 3.x | Styling |

---

## 🚀 Como Rodar

### Pré-requisitos
```bash
java -version   # 17+
node -v         # 18+
docker --version
mvn -v          # 3.8+
```

### Setup Rápido

```bash
# 1. Clone
git clone <repositorio>
cd flowpay

# 2. Configure ambiente
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env

# 3. Suba PostgreSQL
docker compose up postgres -d

# 4. Backend (terminal 1)
cd backend
mvn spring-boot:run

# 5. Frontend (terminal 2)
cd frontend
npm install
npm run dev
```

### Acesse
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432

---

## 📡 API Endpoints

### Atendimentos
```http
POST   /api/atendimentos              # Criar
GET    /api/atendimentos              # Listar
GET    /api/atendimentos/{id}         # Buscar
PUT    /api/atendimentos/{id}/finalizar # Finalizar
```

### Atendentes
```http
POST   /api/atendentes                # Criar
GET    /api/atendentes                # Listar
```

### Dashboard
```http
GET    /api/dashboard/metricas        # Métricas
```

### Exemplo de Request
```bash
curl -X POST http://localhost:8080/api/atendimentos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteNome": "João Silva",
    "assunto": "Problemas com cartão bloqueado"
  }'
```

### Exemplo de Response
```json
{
  "id": 1,
  "clienteNome": "João Silva",
  "assunto": "Problemas com cartão bloqueado",
  "tipoTime": "CARTOES",
  "status": "EM_ATENDIMENTO",
  "atendenteId": 1,
  "atendenteNome": "Maria Silva",
  "dataHoraInicio": "2026-02-11T10:30:00"
}
```

---

## 🧪 Testes

```bash
cd backend

# Rodar todos
mvn test

# Com cobertura
mvn clean test jacoco:report

# Apenas uma classe
mvn test -Dtest=FilaServiceTest
```

### Cobertura
- **Total**: 26 testes
- **Coverage**: >80% (Services + Enums)
- **Frameworks**: JUnit 5, Mockito, AssertJ

---

## 📊 Modelo de Dados

```sql
┌─────────────────┐         ┌──────────────────┐
│   atendentes    │         │   atendimentos   │
├─────────────────┤         ├──────────────────┤
│ id (PK)         │◄────────│ id (PK)          │
│ nome            │ 1     N │ cliente_nome     │
│ tipo_time       │         │ assunto          │
└─────────────────┘         │ tipo_time        │
                            │ status           │
                            │ atendente_id (FK)│
                            │ data_hora_inicio │
                            │ data_hora_fim    │
                            └──────────────────┘
```

---

## 🎯 Decisões Técnicas

### 1. Records (Java 17+)
**Por quê:** Imutabilidade, menos boilerplate, type-safe

### 2. Filas em Memória
**Por quê:** Performance superior, adequado ao escopo, thread-safe

### 3. FIFO com LinkedList
**Por quê:** Operações O(1) para `offer()` e `poll()`

### 4. Global Exception Handler
**Por quê:** Respostas de erro padronizadas, código DRY

### 5. Bean Validation + Service Layer
**Por quê:** Validações simples no DTO, regras complexas no Service

### 6. TypeScript
**Por quê:** Type safety, menos bugs, melhor DX

---

## 🚧 Melhorias Futuras

### Curto Prazo
- [ ] WebSocket real-time no frontend
- [ ] Paginação nos endpoints
- [ ] Swagger/OpenAPI docs

### Médio Prazo
- [ ] Autenticação JWT + Spring Security
- [ ] Cache com Redis
- [ ] Testes de integração

### Longo Prazo
- [ ] Filas persistentes (RabbitMQ)
- [ ] Métricas (Prometheus/Grafana)
- [ ] CI/CD pipeline

---

## 💡 Diferenciais (Nível Sênior)

✅ Arquitetura em camadas (SOLID)  
✅ 25+ testes unitários (>80% coverage)  
✅ Exceções personalizadas + GlobalExceptionHandler  
✅ Validações em múltiplas camadas  
✅ Records Java 17+ (código moderno)  
✅ TypeScript (type safety)  
✅ Docker Compose (fácil setup)  
✅ Logs estruturados (SLF4J)  
✅ Código limpo e documentado  
✅ Decisões técnicas justificadas  

---

## 📚 Tecnologias & Patterns

**Patterns Aplicados:**
- Repository Pattern
- Service Layer Pattern
- DTO Pattern (Records)
- Builder Pattern (Lombok)
- Strategy Pattern (TipoTime)

**Boas Práticas:**
- SOLID Principles
- Clean Code
- DRY (Don't Repeat Yourself)
- Separation of Concerns
- Fail Fast

---

## 👨‍💻 Desenvolvedor

**Nathan Motoki**  
Desafio Técnico - Ubots (Vaga Sênior Full Stack)  
Fevereiro 2026

---

## 📄 Licença

Desenvolvido para fins de avaliação técnica.

---

## 🙏 Agradecimentos

Agradeço à **Ubots** pela oportunidade de demonstrar minhas habilidades técnicas neste desafio.
