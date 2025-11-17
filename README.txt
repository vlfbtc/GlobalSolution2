=====================================================
GLOBAL SOLUTION 2025 - JOBCONNECT PLATFORM
DOCUMENTAÇÃO TÉCNICA COMPLETA
=====================================================

TEMA: Futuro do Trabalho - Conectando talentos a oportunidades

DESCRIÇÃO DO PROJETO:
--------------------
Plataforma completa para conectar candidatos com oportunidades de trabalho do futuro, 
focada em tecnologias emergentes como IA, automação, sustentabilidade e transformação 
digital. A solução é composta por um aplicativo Android nativo desenvolvido em Kotlin 
com Jetpack Compose, uma API REST robusta em Spring Boot 3.2.0 e banco de dados 
Oracle XE 21c.

ARQUITETURA DO SISTEMA:
---------------------
Android App (Kotlin + Jetpack Compose) ←→ Spring Boot API (REST) ←→ Oracle Database

┌─────────────────┐    HTTP/REST    ┌──────────────────┐    JDBC    ┌─────────────────┐
│   Android App   │ ←────────────→ │  Spring Boot API │ ←─────────→ │ Oracle Database │
│ (Kotlin/Compose)│                 │   (Java 17+)     │             │   (Oracle XE)   │
└─────────────────┘                 └──────────────────┘             └─────────────────┘

TECNOLOGIAS UTILIZADAS E JUSTIFICATIVAS:
=======================================

1. BACKEND - SPRING BOOT 3.2.0:
-------------------------------
TECNOLOGIAS:
- Spring Boot 3.2.0 - Framework Java enterprise
- Spring Data JPA - Mapeamento objeto-relacional
- Hibernate 6.x - ORM avançado
- Oracle JDBC Driver - Conectividade oficial Oracle
- Maven 3.x - Gerenciamento de dependências
- Bean Validation API - Validação automática de dados

JUSTIFICATIVAS TÉCNICAS:
• PRODUTIVIDADE: Auto-configuração reduz 70% do código boilerplate
• PERFORMANCE: HikariCP pool de conexões com gerenciamento automático
• ESCALABILIDADE: Arquitetura preparada para microserviços
• MANUTENIBILIDADE: Injeção de dependência e padrões enterprise
• SEGURANÇA: Validação automática de dados e transações declarativas
• ORACLE INTEGRATION: Driver oficial garante compatibilidade total
• ENTERPRISE READY: Actuator endpoints para monitoramento

2. ANDROID - KOTLIN + JETPACK COMPOSE:
-------------------------------------
TECNOLOGIAS:
- Kotlin - Linguagem moderna, nulla-safe e interoperável
- Jetpack Compose - UI toolkit declarativo nativo
- Coroutines - Programação assíncrona não-bloqueante
- Material Design 3 - Design system atualizado do Google
- HTTP Client nativo - Cliente REST integrado

JUSTIFICATIVAS TÉCNICAS:
• PERFORMANCE: Compilação nativa Android, 100% compatível
• PRODUTIVIDADE: Sintaxe Kotlin reduz 40% do código vs Java
• UI MODERNA: Compose elimina XML, interface reativa declarativa
• CONCORRÊNCIA: Coroutines simplificam chamadas de rede assíncronas
• UX/UI: Material Design 3 garante consistência visual
• MANUTENÇÃO: Null safety reduz crashes em produção

3. BANCO DE DADOS - ORACLE XE 21c:
---------------------------------
TECNOLOGIAS:
- Oracle Database XE 21c - Sistema enterprise gratuito
- Docker - Containerização para facilitar deployment
- JPA/Hibernate - Mapeamento automático de entidades
- SQL Scripts - Inicialização automática de schema

JUSTIFICATIVAS TÉCNICAS:
• ROBUSTEZ: Engine enterprise-grade com alta disponibilidade
• PERFORMANCE: Otimizações avançadas para workloads complexos
• ESCALABILIDADE: Suporta crescimento futuro sem refatoração
• COMPATIBILIDADE: Padrão da indústria, conhecimento disseminado
• DESENVOLVIMENTO: Containerização facilita configuração local
• TRANSAÇÕES: ACID compliance garante integridade dos dados

INSTRUÇÕES DE EXECUÇÃO:
======================

PRÉ-REQUISITOS:
--------------
- Java 17 ou superior
- Docker Desktop
- Android Studio (para desenvolvimento mobile)
- Git

PASSO 1: CONFIGURAR BANCO DE DADOS ORACLE
-----------------------------------------
cd oracle
docker-compose up -d

VERIFICAÇÃO:
- Container Oracle XE rodando na porta 1521
- Schema VAGAS_CURSOS criado automaticamente
- Tabelas e dados seed inseridos via scripts SQL

PASSO 2: INICIAR BACKEND SPRING BOOT
------------------------------------
cd spring-backend

# Usando Maven wrapper (recomendado):
./mvnw.cmd spring-boot:run

# Ou usando Maven instalado:
mvn spring-boot:run

# Ou usando VS Code task:
Ctrl+Shift+P → "Tasks: Run Task" → "Spring Boot: Run"

VERIFICAÇÃO:
- API rodando em http://localhost:8081
- Endpoint de health: http://localhost:8081/api/health
- Endpoint de informações: http://localhost:8081/api/info

PASSO 3: EXECUTAR APLICATIVO ANDROID
-----------------------------------
cd android-app

# Build do projeto:
./gradlew clean assembleDebug

# Instalação no emulador/device:
./gradlew installDebug

# Ou usar Android Studio:
- Abrir projeto android-app
- Sync Gradle
- Run 'app'

VERIFICAÇÃO:
- App conecta automaticamente à API (10.0.2.2:8081 no emulador)
- Interface funcional com navegação entre Vagas/Cursos/Candidaturas

ENDPOINTS PRINCIPAIS DA API:
===========================

HEALTH & INFO:
--------------
GET  /api/health          - Status da aplicação e banco
GET  /api/info            - Informações da API

CURSOS:
-------
GET  /api/cursos                        - Listar todos os cursos
GET  /api/cursos/{id}                   - Buscar curso por ID
GET  /api/cursos/com-progresso/{candId} - Cursos com progresso do candidato
POST /api/cursos                        - Criar novo curso
PUT  /api/cursos/{id}                   - Atualizar curso
DELETE /api/cursos/{id}                 - Deletar curso

VAGAS:
------
GET  /api/vagas              - Listar todas as vagas
GET  /api/vagas/{id}         - Buscar vaga por ID
GET  /api/vagas/completo     - Listar vagas com cursos recomendados
POST /api/vagas              - Criar nova vaga
PUT  /api/vagas/{id}         - Atualizar vaga
DELETE /api/vagas/{id}       - Deletar vaga

CANDIDATURAS:
------------
GET  /api/candidaturas/candidato/{id} - Candidaturas de um candidato
GET  /api/candidaturas/vaga/{id}      - Candidaturas de uma vaga
POST /api/candidaturas                - Criar nova candidatura
PUT  /api/candidaturas/{id}           - Atualizar status
DELETE /api/candidaturas/{id}         - Remover candidatura

PROGRESSO DE CURSOS:
-------------------
POST /api/progresso/iniciar           - Iniciar curso
PUT  /api/progresso/atualizar         - Atualizar progresso
GET  /api/progresso/{candId}/{cursoId} - Buscar progresso específico

FUNCIONALIDADES IMPLEMENTADAS:
=============================

MOBILE APP (Android):
---------------------
✓ Seleção de tipo de usuário (Empresa/Candidato)
✓ Interface para empresas criarem vagas
✓ Visualização de vagas por candidatos
✓ Sistema de candidaturas com feedback
✓ Catálogo de cursos por categoria
✓ Sistema de progresso de cursos (0–100%, usado principalmente 0 / 50 / 100)
✓ Navegação por tabs (Vagas/Cursos/Candidaturas)
✓ Design Material 3 responsivo
✓ Gerenciamento de sessão simples

BACKEND API:
-----------
✓ CRUD completo para Vagas, Cursos e Candidaturas
✓ Sistema de relacionamento Many-to-Many (Vagas ↔ Cursos)
✓ Endpoints de progresso de cursos com HashMap merging
✓ Validação automática de dados (Bean Validation)
✓ Pool de conexões HikariCP configurado
✓ Transações declarativas (@Transactional)
✓ Health checks e métricas (Spring Actuator)
✓ CORS configurado para desenvolvimento

DATABASE:
---------
✓ Schema Oracle com sequences e constraints
✓ Relacionamentos bem definidos com chaves estrangeiras
✓ Dados seed automáticos para demonstração
✓ Índices e otimizações de performance
✓ Containerização Docker para facilitar setup

VANTAGENS DA ARQUITETURA ESCOLHIDA:
==================================

1. SEPARAÇÃO DE RESPONSABILIDADES:
- Frontend mobile nativo para melhor UX
- Backend stateless facilita escalabilidade
- Banco enterprise para garantir consistência

2. TECNOLOGIAS MADURAS:
- Stack Spring Boot amplamente adotada
- Kotlin/Compose futuro do desenvolvimento Android
- Oracle database padrão enterprise

3. FACILIDADE DE MANUTENÇÃO:
- Código limpo com injeção de dependências
- ORM elimina SQL manual e bugs de mapeamento
- Compose reduz complexidade de UI

4. PERFORMANCE:
- Pool de conexões otimizado
- Coroutines para não bloquear UI
- Queries JPA otimizadas automaticamente

5. ESCALABILIDADE FUTURA:
- API REST pode servir múltiplos clientes
- Arquitetura preparada para microserviços
- Database Oracle suporta alta carga

ESTRUTURA DETALHADA DOS DIRETÓRIOS:
==================================

GlobalSolution2/
├── README.md                    # Documentação do usuário
├── README.txt                   # Esta documentação técnica
├── populate-simple.ps1          # Script auxiliar de dados
├── oracle/                      # Configuração do banco
│   ├── docker-compose.yml      # Container Oracle XE
│   └── db/
│       ├── 01_schema.sql       # Criação de tabelas e sequences
│       └── 02_seed.sql         # Dados iniciais para teste
├── spring-backend/             # API REST Spring Boot
│   ├── pom.xml                 # Dependências Maven
│   ├── mvnw.cmd               # Maven wrapper Windows
│   ├── src/main/java/com/example/vagas/
│   │   ├── VagasApiApplication.java     # Classe principal
│   │   ├── controller/         # Controllers REST
│   │   │   ├── VagaController.java
│   │   │   ├── CursoController.java
│   │   │   ├── CandidaturaController.java
│   │   │   ├── ProgressoController.java
│   │   │   └── HealthController.java
│   │   ├── entity/            # Entidades JPA
│   │   │   ├── Vaga.java
│   │   │   ├── Curso.java
│   │   │   ├── Candidatura.java
│   │   │   └── CursoCandidato.java
│   │   └── repository/        # Repositórios Spring Data
│   │       ├── VagaRepository.java
│   │       ├── CursoRepository.java
│   │       ├── CandidaturaRepository.java
│   │       └── CursoCandidatoRepository.java
│   └── src/main/resources/
│       └── application.properties      # Configurações da aplicação
└── android-app/               # Aplicativo Android
    ├── build.gradle.kts       # Configuração Gradle principal
    └── app/
        ├── build.gradle.kts   # Configurações do módulo app
        └── src/main/java/com/example/gsapp/
            ├── MainActivity.kt      # Activity principal com Compose
            ├── DatabaseHelper.kt    # Cliente HTTP para comunicar com API
            ├── models.kt           # Data classes Kotlin
            ├── SessionManager.kt   # Gerenciamento simples de sessão
            └── ui/                 # Componentes de interface
