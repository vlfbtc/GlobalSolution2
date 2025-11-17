package com.example.gsapp

// Tipo de usuário logado
enum class UserType {
    EMPRESA,
    CANDIDATO
}

// Entidades principais (espelham o banco Oracle)
data class Empresa(
    val id: Int,
    val nome: String,
    val email: String,
    val senha: String
)

data class Candidato(
    val id: Int,
    val nome: String,
    val email: String,
    val senha: String,
    val curriculo: String?
)

data class Curso(
    val id: Int,
    val titulo: String,
    val categoria: String?
)

data class CursoComProgresso(
    val id: Int,
    val titulo: String,
    val categoria: String?,
    val progresso: Int = 0
)

data class Vaga(
    val id: Int,
    val titulo: String,
    val descricao: String?,
    val empresaId: Int,
    val cursosRecomendados: List<Curso>? = null
)

data class Candidatura(
    val id: Int,
    val vagaId: Int,
    val candidatoId: Int,
    val status: String,
    val dataAplicacao: String
)

// Para tela de "Minhas Candidaturas"
data class CandidaturaDetalhe(
    val vagaTitulo: String,
    val status: String,
    val dataAplicacao: String
)

// Para acompanhar curso do candidato
data class CursoEmAndamento(
    val id: Int,
    val cursoId: Int,
    val candidatoId: Int,
    val progresso: Int
)
