# AlgaPosts - Sistema de Gerenciamento de Posts

Sistema distribuído para gerenciamento de posts com processamento assíncrono de texto utilizando mensageria RabbitMQ.

## 📋 Visão Geral

O AlgaPosts é um sistema composto por dois microsserviços que trabalham em conjunto para criar, processar e consultar posts de texto. O sistema utiliza comunicação assíncrona para processar o conteúdo dos posts em segundo plano, calculando estatísticas como contagem de palavras e valores estimados.

## 🏗️ Arquitetura

```
┌─────────────────┐    RabbitMQ     ┌─────────────────────┐
│   PostService   │ ←───────────→   │ TextProcessorService │
│     (8080)      │                 │       (8081)        │
└─────────────────┘                 └─────────────────────┘
         │
         ▼
    ┌─────────┐
    │   H2    │
    │Database │
    └─────────┘
```

### Microsserviços

#### 1. PostService (Porta 8080)
- **Responsabilidades:**
  - Exposição de API REST para criação e consulta de posts
  - Persistência dos dados em banco H2
  - Envio de posts para processamento via RabbitMQ
  - Recepção dos resultados do processamento
  
- **Endpoints:**
  - `POST /api/posts` - Cria um novo post
  - `GET /api/posts/{id}` - Consulta um post específico
  - `GET /api/posts?page=0&size=10` - Lista posts paginados

#### 2. TextProcessorService (Porta 8081)
- **Responsabilidades:**
  - Processamento assíncrono do conteúdo dos posts
  - Contagem de palavras no texto
  - Cálculo do valor estimado (R$ 0,10 por palavra)
  - Envio dos resultados processados de volta ao PostService

## 🔧 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.9**
- **Spring AMQP** (RabbitMQ)
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **Maven**
- **Docker Compose**

## 📦 Estrutura do Projeto

```
algaposts/
├── docker-compose.yml
├── microservices/
│   ├── post/                     # PostService
│   │   ├── src/main/java/com/algaposts/post/
│   │   │   ├── api/             # Controllers e DTOs
│   │   │   ├── domain/          # Entidades e Services
│   │   │   ├── infrastructure/  # Configurações RabbitMQ
│   │   │   └── mapper/          # Mapeadores
│   │   └── pom.xml
│   └── text_processor/          # TextProcessorService
│       ├── src/main/java/com/algaposts/text_processor/
│       │   ├── domain/          # Services e estratégias
│       │   └── infrastructure/  # Messaging e configurações
│       └── pom.xml
└── README.md
```

## 🚀 Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.6+
- Docker e Docker Compose

### 1. Subir o RabbitMQ

```bash
docker-compose up -d
```

Isso irá subir o RabbitMQ com:
- **URL Management:** http://localhost:15672
- **Usuário/Senha:** rabbitmq/rabbitmq
- **Porta AMQP:** 5672

### 2. Executar o TextProcessorService

```bash
cd microservices/text_processor
./mvnw spring-boot:run
```

### 3. Executar o PostService

```bash
cd microservices/post
./mvnw spring-boot:run
```

## 📨 Comunicação via Mensageria

### Filas e Exchanges

- **Exchange:** `post-processing-exchange.v1.e`
- **Fila de entrada:** `text-processor-service.post-processing.v1.q`
- **Fila de resultado:** `post-service.post-processing-result.v1.q`
- **DLQ:** `text-processor-service.post-processing.v1.dlq`

### Fluxo de Mensagens

1. **PostService** envia mensagem para processamento:
```json
{
  "postId": "uuid",
  "postBody": "texto do post"
}
```

2. **TextProcessorService** processa e envia resultado:
```json
{
  "postId": "uuid",
  "wordCount": 123,
  "calculatedValue": 12.30
}
```

## 🔍 Exemplos de Uso

### Criar um Post

```bash
curl -X POST http://localhost:8080/v1/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Meu Primeiro Post",
    "body": "Este é o conteúdo do meu primeiro post no sistema AlgaPosts.",
    "author": "João Silva"
  }'
```

**Resposta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Meu Primeiro Post",
  "body": "Este é o conteúdo do meu primeiro post no sistema AlgaPosts.",
  "author": "João Silva",
  "wordCount": null,
  "calculatedValue": null
}
```

### Consultar Post (após processamento)

```bash
curl http://localhost:8080/v1/api/posts/550e8400-e29b-41d4-a716-446655440000
```

**Resposta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Meu Primeiro Post",
  "body": "Este é o conteúdo do meu primeiro post no sistema AlgaPosts.",
  "author": "João Silva",
  "wordCount": 12,
  "calculatedValue": 1.20
}
```

### Listar Posts com Paginação

```bash
curl "http://localhost:8080/v1/api/posts?page=0&size=5"
```

**Resposta:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Meu Primeiro Post",
      "summary": "Este é o conteúdo do meu primeiro post no sistema AlgaPosts.",
      "author": "João Silva"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 5,
  "number": 0
}
```

## 🗃️ Modelos de Dados

### PostInput
```json
{
  "title": "string (obrigatório)",
  "body": "string (obrigatório, não vazio)",
  "author": "string (obrigatório)"
}
```

### PostOutput
```json
{
  "id": "UUID",
  "title": "string",
  "body": "string",
  "author": "string",
  "wordCount": "integer",
  "calculatedValue": "decimal"
}
```

### PostSummaryOutput
```json
{
  "id": "UUID",
  "title": "string",
  "summary": "string (primeiras 350 caracteres + '...')",
  "author": "string"
}
```

## ⚙️ Configurações

### PostService (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:file:~/algaposts-post-db
  rabbitmq:
    host: localhost
    port: 5672
    username: rabbitmq
    password: rabbitmq
```

### TextProcessorService (application.yml)
```yaml
server:
  port: 8081

spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: rabbitmq
    password: rabbitmq

text-processor:
  price-per-word: 0.10
```

## 🔄 Processamento Assíncrono

O sistema implementa processamento assíncrono com as seguintes características:

- **Retry automático:** até 3 tentativas com backoff exponencial
- **Dead Letter Queue:** mensagens com falha são redirecionadas para DLQ
- **Prefetch:** controle de fluxo com 4 mensagens por vez
- **Acknowledgment:** confirmação automática após processamento bem-sucedido

## 📊 Monitoramento

### RabbitMQ Management
- **URL:** http://localhost:15672
- **Usuário:** rabbitmq
- **Senha:** rabbitmq

### H2 Console (PostService)
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:file:~/algaposts-post-db`
- **Usuário:** sa
- **Senha:** (vazia)

## 🧪 Testando o Sistema Completo

1. **Suba toda a infraestrutura:**
```bash
docker-compose up -d
cd microservices/text_processor && ./mvnw spring-boot:run &
cd microservices/post && ./mvnw spring-boot:run &
```

2. **Crie um post:**
```bash
curl -X POST http://localhost:8080/v1/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Teste de Processamento",
    "body": "Este texto possui exatamente dez palavras para testar o processamento.",
    "author": "Usuário de Teste"
  }'
```

3. **Aguarde alguns segundos e consulte o resultado:**
```bash
curl http://localhost:8080/v1/api/posts/{id-retornado}
```

4. **Verifique que `wordCount` = 10 e `calculatedValue` = 1.00**

## 🐛 Troubleshooting

### Problemas Comuns

1. **RabbitMQ não conecta:**
   - Verifique se o Docker está rodando
   - Confirme se a porta 5672 está livre
   - Execute `docker-compose logs algaposts-rabbitmq`

2. **Post não é processado:**
   - Verifique os logs do TextProcessorService
   - Consulte o RabbitMQ Management para ver filas
   - Verifique se ambos os serviços estão rodando

3. **Erro 404 ao consultar post:**
   - Confirme se o ID está correto
   - Verifique se o post foi salvo no H2

### Logs Úteis

```bash
# PostService
tail -f logs/post-service.log

# TextProcessorService  
tail -f logs/text-processor-service.log
```

## 📝 Licença

Este projeto foi desenvolvido como parte do desafio AlgaWorks e é destinado para fins educacionais.

---

## Autor

Luis Paulo

<div>
  <a href="https://www.linkedin.com/in/luis-paulo-souza-a54358134/" target="_blank">
    <img src="https://img.shields.io/badge/-LinkedIn-%230077B5?style=for-the-badge&logo=linkedin&logoColor=white" target="_blank">
  </a>
</div>
