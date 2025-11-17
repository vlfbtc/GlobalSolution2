=====================================================
DOCUMENTAÇÃO TÉCNICA DE CLASSES E MÉTODOS
JOBCONNECT PLATFORM - GLOBAL SOLUTION 2025
=====================================================

ÍNDICE:
======
1. BACKEND SPRING BOOT - CLASSES E MÉTODOS
2. ANDROID APP - CLASSES E FUNÇÕES
3. ENTIDADES E MODELOS DE DADOS
4. FLUXOS DE DADOS E INTERAÇÕES

1. BACKEND SPRING BOOT - CLASSES E MÉTODOS:
==========================================

1.1 CLASSE PRINCIPAL:
--------------------

VagasApiApplication.java
========================
DESCRIÇÃO: Classe principal do Spring Boot, responsável por inicializar a aplicação
ANOTAÇÕES: @SpringBootApplication

MÉTODOS:
• main(String[] args) : void
  - Inicializa a aplicação Spring Boot
  - Exibe mensagens de confirmação no console
  - Configura servidor na porta 8081

1.2 CONTROLLERS (CAMADA DE APRESENTAÇÃO):
----------------------------------------

VagaController.java
==================
DESCRIÇÃO: Controller REST para gerenciar vagas de trabalho
ANOTAÇÕES: @RestController, @RequestMapping("/api/vagas"), @CrossOrigin

DEPENDÊNCIAS INJETADAS:
• @Autowired VagaRepository vagaRepository
• @Autowired CursoRepository cursoRepository

MÉTODOS PÚBLICOS:

• testConnection() : Map<String, Object>
  ENDPOINT: GET /api/vagas/test
  DESCRIÇÃO: Testa conexão com banco de dados Oracle
  RETORNA: Status da conexão e contagem de vagas
  TRATAMENTO: Exception handling com mensagens de erro

• listarVagas() : List<Map<String, Object>>
  ENDPOINT: GET /api/vagas
  DESCRIÇÃO: Lista todas as vagas em formato simplificado
  RETORNA: Lista com id, titulo, descricao, empresaId
  PROCESSAMENTO: Stream mapping para transformação de dados

• listarVagasCompleto() : List<Vaga>
  ENDPOINT: GET /api/vagas/completo
  DESCRIÇÃO: Lista todas as vagas com cursos recomendados
  RETORNA: Entidades Vaga completas com relacionamentos

• buscarVaga(@PathVariable Long id) : ResponseEntity<Vaga>
  ENDPOINT: GET /api/vagas/{id}
  DESCRIÇÃO: Busca vaga específica por ID
  PARÂMETROS: id - Identificador único da vaga
  RETORNA: ResponseEntity com vaga ou 404 Not Found

• criarVaga(@RequestBody Map<String, Object> vagaData) : ResponseEntity<Map<String, Object>>
  ENDPOINT: POST /api/vagas
  DESCRIÇÃO: Cria nova vaga. Aceita cursos recomendados em dois formatos:
    - Lista de objetos contendo "id" de cursos existentes
    - String de IDs separados por vírgula (ex: "1,2,3")
  PARÂMETROS: vagaData - JSON com campos: titulo, descricao, empresaId, cursosRecomendados (opcional)
  PROCESSAMENTO:
    - Salva vaga base
    - Normaliza entrada de cursosRecomendados
    - Carrega entidades Curso existentes e associa se encontrados
    - Salva atualização da vaga com relacionamento Many-to-Many
  RETORNA: JSON com success e vagaId
  TRATAMENTO: Exception handling com mensagem detalhada

• atualizarVaga(@PathVariable Long id, @RequestBody Vaga vagaDetails) : ResponseEntity<Vaga>
  ENDPOINT: PUT /api/vagas/{id}
  DESCRIÇÃO: Atualiza dados de vaga existente
  PARÂMETROS: 
    - id - Identificador da vaga
    - vagaDetails - Novos dados da vaga
  RETORNA: Vaga atualizada ou 404 Not Found

• deletarVaga(@PathVariable Long id) : ResponseEntity<?>
  ENDPOINT: DELETE /api/vagas/{id}
  DESCRIÇÃO: Remove vaga do sistema
  PARÂMETROS: id - Identificador da vaga
  RETORNA: 200 OK ou 404 Not Found

• listarVagasPorEmpresa(@PathVariable Long empresaId) : List<Vaga>
  ENDPOINT: GET /api/vagas/empresa/{empresaId}
  DESCRIÇÃO: Lista vagas de empresa específica
  PARÂMETROS: empresaId - ID da empresa
  RETORNA: Lista de vagas da empresa

• limparVagasInadequadas() : ResponseEntity<Map<String, Object>>
  ENDPOINT: GET /api/vagas/limpar-inadequadas
  DESCRIÇÃO: Endpoint utilitário para limpeza de dados
  RETORNA: Resultado da operação de limpeza

CursoController.java
===================
DESCRIÇÃO: Controller REST para gerenciar cursos profissionalizantes
ANOTAÇÕES: @RestController, @RequestMapping("/api/cursos"), @CrossOrigin

DEPENDÊNCIAS INJETADAS:
• @Autowired CursoRepository cursoRepository
• @Autowired VagaRepository vagaRepository
• @Autowired CursoCandidatoRepository cursoCandidatoRepository

MÉTODOS PÚBLICOS:

• listarCursos() : List<Curso>
  ENDPOINT: GET /api/cursos
  DESCRIÇÃO: Lista todos os cursos disponíveis
  RETORNA: Lista completa de entidades Curso

• buscarCurso(@PathVariable Long id) : ResponseEntity<Curso>
  ENDPOINT: GET /api/cursos/{id}
  DESCRIÇÃO: Busca curso específico por ID
  PARÂMETROS: id - Identificador único do curso
  RETORNA: ResponseEntity com curso ou 404 Not Found

• criarCurso(@RequestBody Curso curso) : Curso
  ENDPOINT: POST /api/cursos
  DESCRIÇÃO: Cria novo curso no sistema
  PARÂMETROS: curso - Entidade Curso com dados
  RETORNA: Curso criado com ID gerado
  VALIDAÇÃO: Bean Validation automática

• atualizarCurso(@PathVariable Long id, @RequestBody Curso cursoDetails) : ResponseEntity<Curso>
  ENDPOINT: PUT /api/cursos/{id}
  DESCRIÇÃO: Atualiza dados de curso existente
  PARÂMETROS:
    - id - Identificador do curso
    - cursoDetails - Novos dados do curso
  RETORNA: Curso atualizado ou 404 Not Found

• deletarCurso(@PathVariable Long id) : ResponseEntity<?>
  ENDPOINT: DELETE /api/cursos/{id}
  DESCRIÇÃO: Remove curso do sistema
  PARÂMETROS: id - Identificador do curso
  RETORNA: 200 OK ou 404 Not Found

• getEstatisticasCurso(@PathVariable Long id) : ResponseEntity<Map<String, Object>>
  ENDPOINT: GET /api/cursos/{id}/estatisticas
  DESCRIÇÃO: Calcula estatísticas de uso do curso
  PARÂMETROS: id - Identificador do curso
  PROCESSAMENTO:
    - Conta vagas que recomendam o curso
    - Calcula percentual em relação ao total
  RETORNA: JSON com estatísticas detalhadas

• listarCursosComProgresso(@PathVariable Long candidatoId) : List<Map<String, Object>>
  ENDPOINT: GET /api/cursos/com-progresso/{candidatoId}
  DESCRIÇÃO: Lista cursos com progresso do candidato
  PARÂMETROS: candidatoId - ID do candidato
  PROCESSAMENTO:
    - Busca todos os cursos
    - Para cada curso, verifica progresso do candidato
    - Merge de dados usando HashMap
  RETORNA: Lista com cursos e progresso individual

CandidaturaController.java
=========================
DESCRIÇÃO: Controller REST para gerenciar candidaturas a vagas
ANOTAÇÕES: @RestController, @RequestMapping("/api/candidaturas"), @CrossOrigin

DEPENDÊNCIAS INJETADAS:
• @Autowired CandidaturaRepository candidaturaRepository
• @Autowired VagaRepository vagaRepository

MÉTODOS PÚBLICOS:

• criarCandidatura(@RequestBody Map<String, Object> candidaturaData) : ResponseEntity<Map<String, Object>>
  ENDPOINT: POST /api/candidaturas
  DESCRIÇÃO: Registra nova candidatura de candidato a vaga
  PARÂMETROS: candidaturaData - JSON com vagaId e candidatoId
  PROCESSAMENTO:
    - Valida existência da vaga
    - Verifica se candidatura já existe
    - Cria nova candidatura com status "Pendente"
  RETORNA: JSON com status de sucesso/erro
  TRATAMENTO: Exception handling completo

• listarCandidaturasPorCandidato(@PathVariable Long candidatoId) : List<Map<String, Object>>
  ENDPOINT: GET /api/candidaturas/candidato/{candidatoId}
  DESCRIÇÃO: Lista candidaturas de um candidato com dados da vaga
  PARÂMETROS: candidatoId - ID do candidato
  PROCESSAMENTO:
    - Busca candidaturas por candidatoId
    - Para cada candidatura, busca dados da vaga
    - Monta response com título e descrição da vaga
  RETORNA: Lista com candidaturas e detalhes das vagas

• listarCandidaturasPorVaga(@PathVariable Long vagaId) : List<Candidatura>
  ENDPOINT: GET /api/candidaturas/vaga/{vagaId}
  DESCRIÇÃO: Lista todas as candidaturas para uma vaga específica
  PARÂMETROS: vagaId - ID da vaga
  RETORNA: Lista de entidades Candidatura

• atualizarStatusCandidatura(@PathVariable Long id, @RequestBody Map<String, String> statusData) : ResponseEntity<Candidatura>
  ENDPOINT: PUT /api/candidaturas/{id}
  DESCRIÇÃO: Atualiza status da candidatura (ex: "Aprovada", "Rejeitada")
  PARÂMETROS:
    - id - Identificador da candidatura
    - statusData - JSON com novo status
  RETORNA: Candidatura atualizada ou 404 Not Found

• deletarCandidatura(@PathVariable Long id) : ResponseEntity<?>
  ENDPOINT: DELETE /api/candidaturas/{id}
  DESCRIÇÃO: Remove candidatura do sistema
  PARÂMETROS: id - Identificador da candidatura
  RETORNA: 200 OK ou 404 Not Found

ProgressoController.java
========================
DESCRIÇÃO: Controller REST para gerenciar progresso de cursos dos candidatos
ANOTAÇÕES: @RestController, @RequestMapping("/api/progresso"), @CrossOrigin

DEPENDÊNCIAS INJETADAS:
• @Autowired CursoCandidatoRepository cursoCandidatoRepository

MÉTODOS PÚBLICOS:

• iniciarCurso(@RequestBody Map<String, Object> dados) : ResponseEntity<Map<String, Object>>
  ENDPOINT: POST /api/progresso/iniciar
  DESCRIÇÃO: Inicia um curso para um candidato (progresso = 0%)
  PARÂMETROS: dados - JSON com cursoId e candidatoId
  PROCESSAMENTO:
    - Verifica se curso já foi iniciado
    - Se não, cria registro CursoCandidato com progresso 0
  RETORNA: JSON com status de sucesso/erro
  TRATAMENTO: Exception handling com mensagens

• atualizarProgresso(@RequestBody Map<String, Object> dados) : ResponseEntity<Map<String, Object>>
  ENDPOINT: PUT /api/progresso/atualizar
  DESCRIÇÃO: Atualiza progresso de curso
  PARÂMETROS: dados - JSON com cursoId, candidatoId e progresso
  PROCESSAMENTO:
    - Busca registro existente
    - Valida faixa (0–100)
    - Atualiza campo progresso
    - Salva alterações
  RETORNA: JSON com status de sucesso/erro

• buscarProgresso(@PathVariable Long candidatoId, @PathVariable Long cursoId) : ResponseEntity<Map<String, Object>>
  ENDPOINT: GET /api/progresso/{candidatoId}/{cursoId}
  DESCRIÇÃO: Consulta progresso específico de um curso
  PARÂMETROS:
    - candidatoId - ID do candidato
    - cursoId - ID do curso
  RETORNA: JSON com progresso atual (0-100)

• listarProgressoPorCandidato(@PathVariable Long candidatoId) : List<CursoCandidato>
  ENDPOINT: GET /api/progresso/candidato/{candidatoId}
  DESCRIÇÃO: Lista todo progresso de cursos de um candidato
  PARÂMETROS: candidatoId - ID do candidato
  RETORNA: Lista de entidades CursoCandidato

HealthController.java
====================
DESCRIÇÃO: Controller para health checks e informações da aplicação
ANOTAÇÕES: @RestController, @RequestMapping("/api"), @CrossOrigin

DEPENDÊNCIAS INJETADAS:
• @Autowired DataSource dataSource

MÉTODOS PÚBLICOS:

• health() : ResponseEntity<Map<String, Object>>
  ENDPOINT: GET /api/health
  DESCRIÇÃO: Verifica status da aplicação e conectividade com banco
  PROCESSAMENTO:
    - Testa conexão com Oracle Database
    - Verifica se conexão está válida
    - Retorna status detalhado
  RETORNA: JSON com status, timestamp e informações do banco
  CÓDIGOS: 200 OK (sucesso) ou 503 Service Unavailable (erro)

• getApplicationInfo() : ResponseEntity<Map<String, Object>>
  ENDPOINT: GET /api/info
  DESCRIÇÃO: Fornece informações sobre a API e endpoints disponíveis
  RETORNA: JSON com:
    - Nome e versão da aplicação
    - Descrição dos serviços
    - Mapa de endpoints principais

1.3 REPOSITÓRIOS (CAMADA DE ACESSO A DADOS):
--------------------------------------------

VagaRepository.java
==================
DESCRIÇÃO: Interface de acesso a dados para entidades Vaga
HERANÇA: JpaRepository<Vaga, Long>
ANOTAÇÕES: @Repository

MÉTODOS AUTOMÁTICOS HERDADOS:
• save(Vaga vaga) : Vaga - Salva/atualiza vaga
• findById(Long id) : Optional<Vaga> - Busca por ID
• findAll() : List<Vaga> - Lista todas as vagas
• delete(Vaga vaga) : void - Remove vaga
• count() : long - Conta total de vagas

MÉTODOS CUSTOMIZADOS:

• findByEmpresaId(Long empresaId) : List<Vaga>
  DESCRIÇÃO: Busca vagas de empresa específica
  IMPLEMENTAÇÃO: Query method automática do Spring Data

• findByTituloContainingIgnoreCase(String titulo) : List<Vaga>
  DESCRIÇÃO: Busca vagas por título (busca parcial, case-insensitive)
  IMPLEMENTAÇÃO: Query method automática

• findByRecommendedCourse(Long cursoId) : List<Vaga>
  ANOTAÇÃO: @Query("SELECT v FROM Vaga v JOIN v.cursosRecomendados c WHERE c.id = :cursoId")
  DESCRIÇÃO: Busca vagas que recomendam curso específico
  PARÂMETROS: cursoId - ID do curso
  RETORNA: Lista de vagas que recomendam o curso

• countTotalVagas() : Long
  ANOTAÇÃO: @Query("SELECT COUNT(v) FROM Vaga v")
  DESCRIÇÃO: Conta total de vagas no sistema

• countVagasWithCourse(Long cursoId) : Long
  ANOTAÇÃO: @Query("SELECT COUNT(v) FROM Vaga v JOIN v.cursosRecomendados c WHERE c.id = :cursoId")
  DESCRIÇÃO: Conta quantas vagas recomendam curso específico
  PARÂMETROS: cursoId - ID do curso

CursoRepository.java
===================
DESCRIÇÃO: Interface de acesso a dados para entidades Curso
HERANÇA: JpaRepository<Curso, Long>
ANOTAÇÕES: @Repository

MÉTODOS AUTOMÁTICOS HERDADOS:
• save(Curso curso) : Curso - Salva/atualiza curso
• findById(Long id) : Optional<Curso> - Busca por ID
• findAll() : List<Curso> - Lista todos os cursos
• delete(Curso curso) : void - Remove curso

MÉTODOS CUSTOMIZADOS:

• findByTituloContainingIgnoreCase(String titulo) : List<Curso>
  DESCRIÇÃO: Busca cursos por título (busca parcial, case-insensitive)
  IMPLEMENTAÇÃO: Query method automática

• findByCategoria(String categoria) : List<Curso>
  DESCRIÇÃO: Busca cursos por categoria específica
  IMPLEMENTAÇÃO: Query method automática

• findAllOrderByTitulo() : List<Curso>
  ANOTAÇÃO: @Query("SELECT c FROM Curso c ORDER BY c.titulo")
  DESCRIÇÃO: Lista todos os cursos ordenados por título

CandidaturaRepository.java
=========================
DESCRIÇÃO: Interface de acesso a dados para entidades Candidatura
HERANÇA: JpaRepository<Candidatura, Long>
ANOTAÇÕES: @Repository

MÉTODOS CUSTOMIZADOS:

• findByCandidatoId(Long candidatoId) : List<Candidatura>
  DESCRIÇÃO: Busca candidaturas por candidato específico

• findByVagaId(Long vagaId) : List<Candidatura>
  DESCRIÇÃO: Busca candidaturas para vaga específica

• findByCandidatoIdAndVagaId(Long candidatoId, Long vagaId) : Optional<Candidatura>
  DESCRIÇÃO: Busca candidatura específica entre candidato e vaga
  USO: Evitar candidaturas duplicadas

• countByCandidatoId(Long candidatoId) : Long
  ANOTAÇÃO: @Query("SELECT COUNT(c) FROM Candidatura c WHERE c.candidatoId = :candidatoId")
  DESCRIÇÃO: Conta total de candidaturas de um candidato

CursoCandidatoRepository.java
============================
DESCRIÇÃO: Interface de acesso a dados para progresso de cursos
HERANÇA: JpaRepository<CursoCandidato, Long>

MÉTODOS CUSTOMIZADOS:

• findByCandidatoId(Long candidatoId) : List<CursoCandidato>
  DESCRIÇÃO: Busca progresso de todos os cursos de um candidato

• findByCandidatoIdAndCursoId(Long candidatoId, Long cursoId) : Optional<CursoCandidato>
  DESCRIÇÃO: Busca progresso específico de curso para candidato

1.4 ENTIDADES JPA (CAMADA DE MODELO):
------------------------------------

Vaga.java
=========
DESCRIÇÃO: Entidade JPA representando vaga de trabalho
TABELA: VAGA
ANOTAÇÕES: @Entity, @Table(name = "VAGA")

CAMPOS:

• @Id @GeneratedValue id : Long
  COLUNA: VAGA_ID
  DESCRIÇÃO: Chave primária, gerada por sequence
  GERAÇÃO: GenerationType.SEQUENCE com seq_vaga

• @NotBlank @Size(max = 100) titulo : String
  COLUNA: TITULO
  DESCRIÇÃO: Título da vaga, obrigatório
  VALIDAÇÃO: Não pode ser vazio, máximo 100 caracteres

• @Lob descricao : String
  COLUNA: DESCRICAO
  DESCRIÇÃO: Descrição detalhada da vaga (CLOB)

• empresaId : Long
  COLUNA: EMPRESA_ID
  DESCRIÇÃO: ID da empresa que criou a vaga, obrigatório

• @ManyToMany cursosRecomendados : Set<Curso>
  TABELA ASSOCIATIVA: VAGACURSO
  DESCRIÇÃO: Cursos recomendados para a vaga
  RELACIONAMENTO: Many-to-Many com Curso

CONSTRUTORES:
• Vaga() - Construtor padrão
• Vaga(String titulo, String descricao, Long empresaId) - Construtor com parâmetros

MÉTODOS:
• Getters e Setters padrão para todos os campos

Curso.java
==========
DESCRIÇÃO: Entidade JPA representando curso profissionalizante
TABELA: CURSO
ANOTAÇÕES: @Entity, @Table(name = "CURSO")

CAMPOS:

• @Id @GeneratedValue id : Long
  COLUNA: CURSO_ID
  DESCRIÇÃO: Chave primária, gerada por sequence
  GERAÇÃO: GenerationType.SEQUENCE com seq_curso

• @NotBlank @Size(max = 100) titulo : String
  COLUNA: TITULO
  DESCRIÇÃO: Nome do curso, obrigatório
  VALIDAÇÃO: Não pode ser vazio, máximo 100 caracteres

• @Size(max = 100) categoria : String
  COLUNA: CATEGORIA
  DESCRIÇÃO: Categoria do curso (ex: "Tecnologia", "Sustentabilidade")
  VALIDAÇÃO: Máximo 100 caracteres

CONSTRUTORES:
• Curso() - Construtor padrão
• Curso(String titulo, String categoria) - Construtor com parâmetros

MÉTODOS:
• Getters e Setters padrão para todos os campos

Candidatura.java
===============
DESCRIÇÃO: Entidade JPA representando candidatura a vaga
TABELA: CANDIDATURA
ANOTAÇÕES: @Entity, @Table(name = "CANDIDATURA")

CAMPOS:

• @Id @GeneratedValue id : Long
  COLUNA: CANDIDATURA_ID
  DESCRIÇÃO: Chave primária, gerada por sequence
  GERAÇÃO: GenerationType.SEQUENCE com seq_candidatura

• candidatoId : Long
  COLUNA: CANDIDATO_ID
  DESCRIÇÃO: ID do candidato, obrigatório

• vagaId : Long
  COLUNA: VAGA_ID
  DESCRIÇÃO: ID da vaga, obrigatório

• @Size(max = 50) status : String
  COLUNA: STATUS
  DESCRIÇÃO: Status da candidatura
  VALORES: "Pendente", "Aprovada", "Rejeitada"
  PADRÃO: "Pendente"

• dataAplicacao : LocalDate
  COLUNA: DATA_APLICACAO
  DESCRIÇÃO: Data da candidatura
  PADRÃO: LocalDate.now()

CONSTRUTORES:
• Candidatura() - Construtor padrão
• Candidatura(Long candidatoId, Long vagaId) - Construtor com IDs

MÉTODOS:
• Getters e Setters padrão para todos os campos

CursoCandidato.java
==================
DESCRIÇÃO: Entidade JPA para tracking de progresso de cursos
TABELA: CURSOCANDIDATO
ANOTAÇÕES: @Entity, @Table(name = "CURSOCANDIDATO")

CAMPOS:

• @Id @GeneratedValue id : Long
  COLUNA: ID
  DESCRIÇÃO: Chave primária, gerada por sequence
  GERAÇÃO: GenerationType.SEQUENCE com seq_curso_cand

• candidatoId : Long
  COLUNA: CANDIDATO_ID
  DESCRIÇÃO: ID do candidato, obrigatório

• cursoId : Long
  COLUNA: CURSO_ID
  DESCRIÇÃO: ID do curso, obrigatório

• progresso : Integer
  COLUNA: PROGRESSO
  DESCRIÇÃO: Percentual de progresso (0–100)
  PADRÃO: 0

CONSTRUTORES:
• CursoCandidato() - Construtor padrão
• CursoCandidato(Long candidatoId, Long cursoId, Integer progresso) - Construtor completo

MÉTODOS:
• Getters e Setters padrão para todos os campos

2. ANDROID APP - CLASSES E FUNÇÕES:
===================================

2.1 ATIVIDADE PRINCIPAL:
-----------------------

MainActivity.kt
===============
DESCRIÇÃO: Activity principal do app Android usando Jetpack Compose
HERANÇA: ComponentActivity()

MÉTODOS:

• onCreate(savedInstanceState: Bundle?)
  DESCRIÇÃO: Inicialização da activity
  PROCESSAMENTO: Configura tema e chama AppRoot()

2.2 FUNÇÕES COMPOSABLES PRINCIPAIS:
----------------------------------

@Composable AppRoot()
====================
DESCRIÇÃO: Função raiz da aplicação, gerencia estado global
RESPONSABILIDADES:
- Controla tipo de usuário logado (Empresa/Candidato)
- Gerencia sessão via SessionManager
- Navega entre fluxos principais

ESTADO:
• userType: UserType? - Tipo de usuário atual
• refreshTrigger: Int - Força refresh da UI

NAVEGAÇÃO:
- Se userType == null → UserTypeSelectionScreen
- Se EMPRESA → EmpresaFlow
- Se CANDIDATO → CandidatoFlow

@Composable UserTypeSelectionScreen()
====================================
DESCRIÇÃO: Tela de seleção inicial (Empresa ou Candidato)
PARÂMETROS:
• onEmpresa: () -> Unit - Callback para seleção Empresa
• onCandidato: () -> Unit - Callback para seleção Candidato

COMPONENTES:
- Card com logo e descrição
- Botão "Sou uma Empresa" (ElevatedButton)
- Botão "Sou um Candidato" (OutlinedButton)

@Composable EmpresaFlow()
========================
DESCRIÇÃO: Fluxo principal para empresas
PARÂMETROS: 
• onLogout: () -> Unit - Callback para logout

ESTADO:
• screen: EmpresaScreen - Tela atual (HOME, NOVA_VAGA)
• vagas: List<Vaga> - Lista de vagas da empresa

NAVEGAÇÃO:
- EmpresaScreen.HOME → EmpresaHomeScreen
- EmpresaScreen.NOVA_VAGA → NovaVagaScreen

@Composable EmpresaHomeScreen()
==============================
DESCRIÇÃO: Tela inicial para empresas, lista vagas criadas
PARÂMETROS:
• vagas: List<Vaga> - Vagas da empresa
• onNovaVaga: () -> Unit - Navegar para criar vaga
• onLogout: () -> Unit - Fazer logout

COMPONENTES:
- Scaffold com TopBar customizada
- Card com estatísticas das vagas
- FloatingActionButton para criar nova vaga
- LazyColumn com lista de vagas

@Composable NovaVagaScreen()
===========================
DESCRIÇÃO: Tela para criar nova vaga com cursos recomendados
PARÂMETROS: 
• onVagaCriada: () -> Unit - Callback após criação

ESTADO:
• titulo: String - Título da vaga
• descricao: String - Descrição da vaga
• cursosDisponiveis: List<Curso> - Cursos para seleção
• cursosSelecionados: MutableList<Curso> - Cursos selecionados
• isLoading: Boolean - Estado de loading

PROCESSAMENTO:
- LaunchedEffect carrega cursos disponíveis
- Validação de campos obrigatórios
- Chamada assíncrona para DatabaseHelper.criarVaga()

@Composable CandidatoFlow()
==========================
DESCRIÇÃO: Fluxo principal para candidatos
PARÂMETROS:
• onLogout: () -> Unit - Callback para logout

ESTADO:
• selectedTab: Int - Aba selecionada (0=Vagas, 1=Cursos, 2=Candidaturas)
• cursoSelecionado: Curso? - Curso para visualização detalhada
• showCursoDetalhe: Boolean - Flag para mostrar detalhes do curso
• refreshCursos: Int - Trigger para refresh da lista de cursos

COMPONENTES:
- NavigationTabs para navegação
- Conteúdo baseado na aba selecionada
- CursoDetalheScreen condicional

@Composable VagasTabContent()
============================
DESCRIÇÃO: Aba de vagas para candidatos
RESPONSABILIDADES:
- Lista todas as vagas disponíveis
- Permite candidatura com feedback
- Mostra estatísticas de candidaturas

ESTADO:
• vagas: List<Vaga> - Lista de vagas
• candidaturasExistentes: Set<Int> - IDs de vagas já candidatadas
• totalCandidaturas: Int - Total de candidaturas do usuário
• showSnackbar: Boolean - Controle de snackbar
• snackbarMessage: String - Mensagem do snackbar

PROCESSAMENTO:
- LaunchedEffect carrega vagas e candidaturas existentes
- Botão dinâmico (Candidatar-se/Já Candidatado)
- Coroutine scope para chamadas assíncronas

@Composable CursosTabContent()
=============================
DESCRIÇÃO: Aba de cursos com progresso
PARÂMETROS:
• refreshTrigger: Int - Trigger para refresh
• onCursoClick: (Curso) -> Unit - Callback para seleção de curso

ESTADO:
• cursosComProgresso: List<CursoComProgresso> - Cursos com progresso do candidato

PROCESSAMENTO:
- LaunchedEffect reativo ao refreshTrigger
- Carrega cursos com progresso via DatabaseHelper
- Cards com barra de progresso visual

@Composable CandidaturasTabContent()
===================================
DESCRIÇÃO: Aba de candidaturas do usuário
RESPONSABILIDADES:
- Lista candidaturas com detalhes das vagas
- Mostra status de cada candidatura

ESTADO:
• candidaturas: List<CandidaturaDetalhe> - Candidaturas com dados da vaga

PROCESSAMENTO:
- LaunchedEffect carrega candidaturas via DatabaseHelper
- Interface vazia quando não há candidaturas

@Composable CursoDetalheScreen()
===============================
DESCRIÇÃO: Tela detalhada de curso individual
PARÂMETROS:
• curso: Curso - Curso para exibir
• onBack: () -> Unit - Callback para voltar
• onProgressUpdated: (() -> Unit)? - Callback após atualização de progresso

ESTADO:
• progresso: Int - Progresso atual do curso
• estatisticas: Pair<Int, Int> - Estatísticas de uso do curso

COMPONENTES:
- TopBar com botão de voltar
- Card com informações do curso
- Barra de progresso visual
- Botão para iniciar/completar curso
- Estatísticas de mercado

PROCESSAMENTO:
- LaunchedEffect carrega progresso e estatísticas
- Coroutine scope para atualização de progresso

2.3 COMPONENTES UTILITÁRIOS:
---------------------------

@Composable CustomTopBar()
=========================
DESCRIÇÃO: TopBar customizada reutilizável
PARÂMETROS:
• title: String - Título da barra
• onNavigationClick: (() -> Unit)? - Callback do botão de navegação
• actions: @Composable RowScope.() -> Unit - Ações customizadas

@Composable NavigationTabs()
===========================
DESCRIÇÃO: Componente de navegação por abas
PARÂMETROS:
• tabs: List<String> - Lista de títulos das abas
• selectedTab: Int - Aba selecionada
• onTabSelected: (Int) -> Unit - Callback de seleção

2.4 CLIENTE HTTP:
----------------

DatabaseHelper.kt
=================
DESCRIÇÃO: Object singleton para comunicação HTTP com API Spring Boot
PADRÃO: Singleton Object

CONFIGURAÇÃO:
• BASE_URL: "http://10.0.2.2:8081/api" - URL da API (emulador Android)
• gson: Gson - Parser JSON

MÉTODOS DE LOGGING:

• logRequest(method: String, url: String, body: String?)
  DESCRIÇÃO: Log de requisições HTTP para debug

• logResponse(response: String)
  DESCRIÇÃO: Log de respostas HTTP para debug

MÉTODOS DE CURSOS:

• suspend fun listarCursos(): List<Curso>
  DESCRIÇÃO: Lista todos os cursos disponíveis
  IMPLEMENTAÇÃO: GET request para /cursos
  RETORNA: Lista de objetos Curso
  TRATAMENTO: Exception handling com lista vazia em caso de erro

• suspend fun listarCursosComProgresso(candidatoId: Int): List<Map<String, Any>>
  DESCRIÇÃO: Lista cursos com progresso do candidato
  IMPLEMENTAÇÃO: GET request para /cursos/com-progresso/{candidatoId}
  RETORNA: Lista de mapas com dados dos cursos + progresso

• suspend fun listarCursosComProgressoObj(candidatoId: Int): List<CursoComProgresso>
  DESCRIÇÃO: Versão tipada do método anterior
  PROCESSAMENTO: Converte Map para objetos CursoComProgresso
  RETORNA: Lista de objetos tipados

• suspend fun buscarCursoPorId(cursoId: Int): Curso?
  DESCRIÇÃO: Busca curso específico por ID
  IMPLEMENTAÇÃO: GET request para /cursos/{id}
  RETORNA: Objeto Curso ou null se não encontrado

• suspend fun criarCurso(titulo: String, descricao: String, categoria: String): Boolean
  DESCRIÇÃO: Cria novo curso
  IMPLEMENTAÇÃO: POST request para /cursos
  PAYLOAD: JSON com dados do curso
  RETORNA: Boolean indicando sucesso

MÉTODOS DE VAGAS:

• suspend fun listarVagas(): List<Vaga>
  DESCRIÇÃO: Lista todas as vagas disponíveis
  IMPLEMENTAÇÃO: GET request para /vagas
  RETORNA: Lista de objetos Vaga

• suspend fun criarVaga(titulo: String, descricao: String, empresaId: Int, cursosIds: List<Int>): Boolean
  DESCRIÇÃO: Cria nova vaga com cursos recomendados
  IMPLEMENTAÇÃO: POST request para /vagas
  PAYLOAD: JSON com dados da vaga + array de cursos
  PROCESSAMENTO: Associa cursos recomendados via relacionamento Many-to-Many
  RETORNA: Boolean indicando sucesso

MÉTODOS DE CANDIDATURAS:

• suspend fun candidatarSe(vagaId: Int, candidatoId: Int): Boolean
  DESCRIÇÃO: Registra candidatura a vaga
  IMPLEMENTAÇÃO: POST request para /candidaturas
  PAYLOAD: JSON com vagaId e candidatoId
  RETORNA: Boolean indicando sucesso

• suspend fun listarCandidaturasComVaga(candidatoId: Int): List<CandidaturaDetalhe>
  DESCRIÇÃO: Lista candidaturas com detalhes das vagas
  IMPLEMENTAÇÃO: GET request para /candidaturas/candidato/{id}
  RETORNA: Lista de objetos CandidaturaDetalhe

MÉTODOS DE PROGRESSO:

• suspend fun iniciarCurso(cursoId: Int, candidatoId: Int)
  DESCRIÇÃO: Inicia curso para candidato (progresso = 0%)
  IMPLEMENTAÇÃO: POST request para /progresso/iniciar
  PAYLOAD: JSON com cursoId e candidatoId

• suspend fun atualizarProgressoCurso(cursoId: Int, candidatoId: Int, novoProgresso: Int)
  DESCRIÇÃO: Atualiza progresso do curso
  IMPLEMENTAÇÃO: PUT request para /progresso/atualizar
  PAYLOAD: JSON com IDs + novo progresso
  VALORES: 0, 50, 100

• suspend fun buscarProgressoCurso(cursoId: Int, candidatoId: Int): Int
  DESCRIÇÃO: Consulta progresso atual
  IMPLEMENTAÇÃO: GET request para /progresso/{candidatoId}/{cursoId}
  RETORNA: Integer com progresso (0-100)

• suspend fun calcularPercentualVagasQuePedemCurso(cursoId: Int): Pair<Int, Int>
  DESCRIÇÃO: Calcula estatísticas de uso do curso
  IMPLEMENTAÇÃO: GET request para /cursos/{id}/estatisticas
  RETORNA: Par com (vagasQuePedem, totalVagas)

MÉTODOS HTTP PRIVADOS:

• suspend fun makeGetRequest(urlString: String): String
  DESCRIÇÃO: Executa requisição GET HTTP
  CONFIGURAÇÃO: Timeout 10s conexão, 15s leitura
  HEADERS: Content-Type e Accept application/json
  TRATAMENTO: Exception handling com códigos HTTP

• suspend fun makePostRequest(urlString: String, jsonBody: String): String
  DESCRIÇÃO: Executa requisição POST HTTP
  CONFIGURAÇÃO: doOutput = true para envio de dados
  ENCODING: UTF-8 para caracteres especiais
  CÓDIGOS: Aceita 200 OK e 201 CREATED

• suspend fun makePutRequest(urlString: String, jsonBody: String): String
  DESCRIÇÃO: Executa requisição PUT HTTP
  CONFIGURAÇÃO: Similar ao POST
  USO: Atualização de progresso de cursos

2.5 MODELOS DE DADOS:
--------------------

models.kt
=========
DESCRIÇÃO: Data classes Kotlin que espelham entidades do backend

ENUMS:

• enum class UserType
  VALORES: EMPRESA, CANDIDATO
  DESCRIÇÃO: Tipo de usuário logado na sessão

DATA CLASSES:

• data class Empresa
  CAMPOS: id: Int, nome: String, email: String, senha: String
  DESCRIÇÃO: Representa empresa cadastrada (não usado atualmente)

• data class Candidato
  CAMPOS: id: Int, nome: String, email: String, senha: String, curriculo: String?
  DESCRIÇÃO: Representa candidato cadastrado (não usado atualmente)

• data class Curso
  CAMPOS: id: Int, titulo: String, categoria: String?
  DESCRIÇÃO: Espelha entidade Curso do backend

• data class CursoComProgresso
  CAMPOS: id: Int, titulo: String, categoria: String?, progresso: Int
  DESCRIÇÃO: Curso com progresso do candidato (0-100%)
  USO: Exibição na aba de cursos

• data class Vaga
  CAMPOS: id: Int, titulo: String, descricao: String?, empresaId: Int, cursosRecomendados: List<Curso>?
  DESCRIÇÃO: Espelha entidade Vaga do backend

• data class Candidatura
  CAMPOS: id: Int, vagaId: Int, candidatoId: Int, status: String, dataAplicacao: String
  DESCRIÇÃO: Espelha entidade Candidatura (ordem dos campos adaptada ao modelo Kotlin)

• data class CandidaturaDetalhe
  CAMPOS: vagaTitulo: String, status: String, dataAplicacao: String
  DESCRIÇÃO: Modelo simplificado usado na UI; não inclui IDs adicionais

• data class CursoEmAndamento
  CAMPOS: cursoId: Int, titulo: String, progresso: Int
  DESCRIÇÃO: Curso em progresso (não usado atualmente)

2.6 GERENCIAMENTO DE SESSÃO:
---------------------------

SessionManager.kt
=================
DESCRIÇÃO: Object singleton para gerenciar sessão do usuário
PADRÃO: Singleton Object

PROPRIEDADES:

• var currentUserType: UserType?
  DESCRIÇÃO: Tipo de usuário logado (EMPRESA/CANDIDATO)
  VALOR INICIAL: null

• var currentUserId: Int?
  DESCRIÇÃO: ID do usuário logado
  VALOR INICIAL: null
  NOTA: Usa IDs fixos (1) para demonstração

MÉTODOS:

• fun logout()
  DESCRIÇÃO: Limpa dados da sessão
  PROCESSAMENTO: Define propriedades como null

3. ENTIDADES E MODELOS DE DADOS:
===============================

3.1 RELACIONAMENTOS DE BANCO:
-----------------------------

VAGA ←→ CURSO (Many-to-Many)
============================
TABELA ASSOCIATIVA: VAGACURSO
CAMPOS:
- VAGA_ID (FK para VAGA.VAGA_ID)
- CURSO_ID (FK para CURSO.CURSO_ID)

DESCRIÇÃO: Uma vaga pode recomendar múltiplos cursos, 
e um curso pode ser recomendado por múltiplas vagas

CANDIDATURA → VAGA (Many-to-One)
================================
CAMPO: CANDIDATURA.VAGA_ID (FK)
DESCRIÇÃO: Uma candidatura pertence a uma vaga específica

CURSOCANDIDATO → CURSO (Many-to-One)
===================================
CAMPO: CURSOCANDIDATO.CURSO_ID (FK)
DESCRIÇÃO: Progresso está associado a um curso específico

3.2 SEQUENCES ORACLE:
---------------------

• seq_vaga - Auto-incremento para VAGA.VAGA_ID
• seq_curso - Auto-incremento para CURSO.CURSO_ID  
• seq_candidatura - Auto-incremento para CANDIDATURA.CANDIDATURA_ID
• seq_curso_cand - Auto-incremento para CURSOCANDIDATO.ID

3.3 CONSTRAINTS E VALIDAÇÕES:
-----------------------------

NOT NULL CONSTRAINTS:
- VAGA.TITULO, VAGA.EMPRESA_ID
- CURSO.TITULO
- CANDIDATURA.CANDIDATO_ID, CANDIDATURA.VAGA_ID
- CURSOCANDIDATO.CANDIDATO_ID, CURSOCANDIDATO.CURSO_ID

SIZE CONSTRAINTS:
- VAGA.TITULO: máximo 100 caracteres
- CURSO.TITULO: máximo 100 caracteres
- CURSO.CATEGORIA: máximo 100 caracteres
- CANDIDATURA.STATUS: máximo 50 caracteres

4. FLUXOS DE DADOS E INTERAÇÕES:
===============================

4.1 FLUXO DE CRIAÇÃO DE VAGA:
----------------------------

1. EMPRESA seleciona "Nova Vaga" no app Android
2. NovaVagaScreen exibe formulário
3. LaunchedEffect carrega cursos via DatabaseHelper.listarCursos()
4. Empresa preenche título, descrição e seleciona cursos
5. Ao confirmar, app chama DatabaseHelper.criarVaga()
6. DatabaseHelper faz POST para /api/vagas com JSON
7. VagaController.criarVaga() recebe requisição
8. Controller cria entidade Vaga e associa cursos (Many-to-Many)
9. VagaRepository salva vaga e relacionamentos
10. Response JSON retorna sucesso/erro
11. App atualiza UI e retorna para lista de vagas

4.2 FLUXO DE CANDIDATURA:
------------------------

1. CANDIDATO visualiza vagas em VagasTabContent
2. LaunchedEffect carrega vagas e candidaturas existentes
3. Para cada vaga, verifica se já existe candidatura
4. Candidato clica "Candidatar-se" em vaga específica
5. App chama DatabaseHelper.candidatarSe() em coroutine
6. DatabaseHelper faz POST para /api/candidaturas
7. CandidaturaController.criarCandidatura() processa
8. Controller verifica se vaga existe e se candidatura é duplicada
9. Cria entidade Candidatura com status "Pendente"
10. CandidaturaRepository salva no banco
11. Response atualiza UI com snackbar de confirmação

4.3 FLUXO DE PROGRESSO DE CURSO:
-------------------------------

1. CANDIDATO navega para CursosTabContent
2. LaunchedEffect carrega cursos com progresso via API
3. CursoController.listarCursosComProgresso() combina dados:
   - Busca todos os cursos
   - Para cada curso, verifica progresso no CursoCandidatoRepository
   - Faz merge de dados em HashMap
4. App exibe cards com barra de progresso visual
5. Candidato clica em curso para ver detalhes
6. CursoDetalheScreen carrega progresso específico
7. Candidato clica "Iniciar Curso" ou "Marcar como Concluído"
8. App chama DatabaseHelper.iniciarCurso() ou atualizarProgressoCurso()
9. ProgressoController processa atualização
10. CursoCandidatoRepository persiste progresso
11. UI atualiza barra de progresso dinamicamente

4.4 FLUXO DE BUSCA COM PROGRESSO:
--------------------------------

BACKEND (CursoController.listarCursosComProgresso):
```java
// Para cada curso encontrado
for (Curso curso : cursos) {
    Map<String, Object> cursoMap = new HashMap<>();
    
    // Dados básicos do curso
    cursoMap.put("id", curso.getId());
    cursoMap.put("titulo", curso.getTitulo());
    cursoMap.put("categoria", curso.getCategoria());
    
    // Busca progresso específico do candidato
    Optional<CursoCandidato> progresso = 
        cursoCandidatoRepository.findByCandidatoIdAndCursoId(candidatoId, curso.getId());
    
    // Merge de dados: curso + progresso
    cursoMap.put("progresso", progresso.map(CursoCandidato::getProgresso).orElse(0));
    
    resultado.add(cursoMap);
}
```

ANDROID (DatabaseHelper.listarCursosComProgressoObj):
```kotlin
// Converte response JSON para objetos tipados
cursosMap.map { mapa ->
    CursoComProgresso(
        id = (mapa["id"] as Double).toInt(),
        titulo = mapa["titulo"] as String,
        categoria = mapa["categoria"] as? String,
        progresso = (mapa["progresso"] as Double).toInt()
    )
}
```

ESTE DOCUMENTO FORNECE UMA VISÃO COMPLETA E TÉCNICA DE TODAS AS CLASSES, 
MÉTODOS E INTERAÇÕES DO SISTEMA JOBCONNECT PLATFORM.

PARA MAIS INFORMAÇÕES, CONSULTE:
- README.txt: Documentação de execução
- Código fonte: Comentários inline detalhados