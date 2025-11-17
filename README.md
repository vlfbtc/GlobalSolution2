# Global Solution 2025 - JobConnect Platform
## Tema: Futuro do Trabalho

Plataforma completa para conectar candidatos com oportunidades de trabalho do futuro, focada em tecnologias emergentes como IA, automação, sustentabilidade e transformação digital.

## Arquitetura

```
Android App (Kotlin + Jetpack Compose) ←→ Spring Boot API ←→ Oracle Database
```

## Tecnologias Utilizadas

### Backend (Spring Boot 3.2.0):
- **Spring Boot** - Framework Java robusto
- **Spring Data JPA** - Mapeamento objeto-relacional  
- **Hibernate** - ORM avançado
- **Oracle JDBC Driver** - Conectividade com Oracle
- **Maven** - Gerenciamento de dependências
- **Validation API** - Validação de dados

### Android (Kotlin):
- **Kotlin** - Linguagem moderna e segura
- **Jetpack Compose** - UI toolkit declarativo
- **Coroutines** - Programação assíncrona
- **Material Design 3** - Design system atualizado
- **Retrofit/HTTP** - Cliente HTTP para APIs

### Banco de Dados:
- **Oracle XE 21c** - Sistema enterprise
- **Docker** - Containerização do banco
- **JPA/Hibernate** - Mapeamento automático

## Configuração e Execução

### 1. Banco de Dados Oracle
```bash
cd oracle
docker-compose up -d
```
*O banco será inicializado automaticamente com as tabelas e dados*

### 2. Backend Spring Boot

#### Pré-requisitos:
- Java 17+ 
- Maven (ou usar wrapper incluído)

#### Execução:
```bash
cd spring-backend
./mvnw.cmd spring-boot:run
```

A API estará disponível em: `http://localhost:8081/api`

#### Endpoints Principais:
- `GET /api/health` - Status da aplicação e banco
- `GET /api/cursos` - Listar todos os cursos
- `GET /api/cursos/com-progresso/{candidatoId}` - Cursos com progresso do candidato
- `GET /api/vagas` - Listar todas as vagas
- `POST /api/vagas` - Criar nova vaga
- `POST /api/candidaturas` - Candidatar-se a vaga
- `POST /api/progresso/iniciar` - Iniciar curso
- `PUT /api/progresso/atualizar` - Atualizar progresso do curso

### 3. Android App

#### Configuração:
1. Certifique-se de que o backend está rodando na porta 8081
2. O app está configurado para usar `10.0.2.2:8081` (localhost do emulador)

#### Build e Execução:
```bash
cd android-app
./gradlew clean assembleDebug
./gradlew installDebug
```

## Funcionalidades Implementadas

### 📱 **Funcionalidades do App Android**
1. **Visualização de Vagas** - Lista completa com detalhes
2. **Catálogo de Cursos** - Explorar cursos por categoria
3. **Sistema de Progress Tracking** - Acompanhar progresso dos cursos
4. **Candidaturas** - Aplicar para vagas de interesse
5. **Interface Moderna** - Design Material 3 responsivo

### 🔧 **Recursos Técnicos Avançados**
- **ORM Automático** - Mapeamento JPA/Hibernate
- **Validação de Dados** - Bean Validation integrada
- **Progress Tracking** - Endpoint especializado com HashMap merging
- **Relacionamentos Complexos** - Many-to-Many entre Vagas e Cursos
- **API RESTful** - Padrões REST bem definidos
- **Fallback de Dados** - Mock data para demonstrações offline

## Estrutura do Projeto

```
GlobalSolution2/
├── android-app/              # Aplicativo Android Kotlin
│   ├── app/src/main/java/com/example/gsapp/
│   │   ├── MainActivity.kt   # Interface principal Compose
│   │   ├── DatabaseHelper.kt # Cliente HTTP para API
│   │   ├── models.kt         # Data classes (Vaga, Curso, etc.)
│   │   └── SessionManager.kt # Gerenciamento de sessão
│   └── build.gradle.kts     # Configuração Gradle
├── spring-backend/          # API Spring Boot 3.2.0
│   ├── src/main/java/com/example/vagas/
│   │   ├── controller/      # Controllers REST
│   │   ├── entity/         # Entidades JPA
│   │   ├── repository/     # Repositórios Spring Data
│   │   └── VagasApiApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml             # Dependências Maven
├── oracle/                 # Banco de dados
│   ├── docker-compose.yml # Container Oracle XE
│   └── db/                # Scripts SQL iniciais
└── populate-simple.ps1    # Script de população de dados
```

## Vantagens da Arquitetura Spring Boot

### ✅ **Benefícios Técnicos:**
1. **Conectividade Oracle Simplificada** - Driver oficial integrado
2. **ORM Robusto** - JPA/Hibernate para mapeamento automático  
3. **Pool de Conexões** - Gerenciamento automático via HikariCP
4. **Validação Automática** - Bean Validation integrada
5. **Transações Declarativas** - `@Transactional` automático
6. **API RESTful** - Padrões industriais
7. **Enterprise Ready** - Preparado para produção