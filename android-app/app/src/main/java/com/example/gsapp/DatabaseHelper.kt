package com.example.gsapp

import kotlinx.coroutines.*
import java.net.URL
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DatabaseHelper {

    // URL base da API - conectando ao backend Spring Boot local
    // Para emulador Android: use 10.0.2.2, para dispositivo real: IP da máquina
    private const val BASE_URL = "http://10.0.2.2:8081/api"
    
    private val gson = Gson()
    
    private fun logRequest(method: String, url: String, body: String? = null) {
        println("DatabaseHelper: $method $url")
        if (body != null) println("Body: $body")
    }
    
    private fun logResponse(response: String) {
        println("DatabaseHelper Response: $response")
    }

    // ---------------- CURSOS ----------------

    suspend fun listarCursos(): List<Curso> = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/cursos")
            val type = object : TypeToken<List<Curso>>() {}.type
            gson.fromJson(response, type) ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao listar cursos: ${e.message}")
            emptyList()
        }
    }

    suspend fun listarCursosComProgresso(candidatoId: Int): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/cursos/com-progresso/$candidatoId")
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(response, type) ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao listar cursos com progresso: ${e.message}")
            emptyList()
        }
    }

    suspend fun listarCursosComProgressoObj(candidatoId: Int): List<CursoComProgresso> = withContext(Dispatchers.IO) {
        try {
            val cursosMap = listarCursosComProgresso(candidatoId)
            cursosMap.map { mapa ->
                CursoComProgresso(
                    id = (mapa["id"] as Double).toInt(),
                    titulo = mapa["titulo"] as String,
                    categoria = mapa["categoria"] as? String,
                    progresso = (mapa["progresso"] as Double).toInt()
                )
            }
        } catch (e: Exception) {
            println("Erro ao converter cursos com progresso: ${e.message}")
            emptyList()
        }
    }

    suspend fun buscarCursoPorId(cursoId: Int): Curso? = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/cursos/$cursoId")
            gson.fromJson(response, Curso::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun criarCurso(titulo: String, descricao: String, categoria: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "titulo" to titulo,
                "descricao" to descricao,
                "categoria" to categoria
            )
            val response = makePostRequest("$BASE_URL/cursos", gson.toJson(payload))
            response.isNotEmpty()
        } catch (e: Exception) {
            println("Erro ao criar curso: ${e.message}")
            false
        }
    }

    // ---------------- VAGAS ----------------

    suspend fun listarVagas(): List<Vaga> = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/vagas")
            val type = object : TypeToken<List<Vaga>>() {}.type
            gson.fromJson(response, type) ?: emptyList()
        } catch (e: Exception) {
            println("Erro ao listar vagas: ${e.message}")
            emptyList()
        }
    }

    suspend fun buscarVagasPorEmpresa(empresaId: Int): List<Vaga> = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/vagas/empresa/$empresaId")
            val type = object : TypeToken<List<Vaga>>() {}.type
            gson.fromJson(response, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun criarVaga(titulo: String, descricao: String, empresaId: Int, cursosRecomendados: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "titulo" to titulo,
                "descricao" to descricao,
                "empresaId" to empresaId,
                "cursosRecomendados" to cursosRecomendados
            )
            val response = makePostRequest("$BASE_URL/vagas", gson.toJson(payload))
            // Backend retorna o objeto criado, então consideramos sucesso se não houve exceção
            response.isNotEmpty()
        } catch (e: Exception) {
            println("Erro ao criar vaga: ${e.message}")
            false
        }
    }

    // ---------------- CANDIDATURAS ----------------

    suspend fun candidatarSe(vagaId: Int, candidatoId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "vagaId" to vagaId,
                "candidatoId" to candidatoId
            )
            val response = makePostRequest("$BASE_URL/candidaturas", gson.toJson(payload))
            // Backend retorna o objeto criado, então consideramos sucesso se não houve exceção
            response.isNotEmpty()
        } catch (e: Exception) {
            println("Erro ao candidatar: ${e.message}")
            false
        }
    }

    suspend fun listarCandidaturasComVaga(candidatoId: Int): List<CandidaturaDetalhe> = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/candidaturas/candidato/$candidatoId")
            val type = object : TypeToken<List<CandidaturaDetalhe>>() {}.type
            gson.fromJson(response, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------------- CURSO x CANDIDATO ----------------

    suspend fun iniciarCurso(cursoId: Int, candidatoId: Int) = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "cursoId" to cursoId,
                "candidatoId" to candidatoId
            )
            makePostRequest("$BASE_URL/progresso/iniciar", gson.toJson(payload))
        } catch (e: Exception) {
            println("Erro ao iniciar curso: ${e.message}")
        }
    }

    suspend fun atualizarProgressoCurso(cursoId: Int, candidatoId: Int, novoProgresso: Int) = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "cursoId" to cursoId,
                "candidatoId" to candidatoId,
                "progresso" to novoProgresso
            )
            makePutRequest("$BASE_URL/progresso/atualizar", gson.toJson(payload))
        } catch (e: Exception) {
            println("Erro ao atualizar progresso: ${e.message}")
        }
    }

    suspend fun buscarProgressoCurso(cursoId: Int, candidatoId: Int): Int = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/progresso/$candidatoId/$cursoId")
            val result = gson.fromJson(response, Map::class.java)
            (result["progresso"] as? Double)?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    // Retorna (vagasQuePedem, totalVagas)
    suspend fun calcularPercentualVagasQuePedemCurso(cursoId: Int): Pair<Int, Int> = withContext(Dispatchers.IO) {
        try {
            val response = makeGetRequest("$BASE_URL/cursos/$cursoId/estatisticas")
            val result = gson.fromJson(response, Map::class.java)
            val vagasQuePedem = (result["vagasQuePedem"] as? Double)?.toInt() ?: 0
            val totalVagas = (result["totalVagas"] as? Double)?.toInt() ?: 0
            Pair(vagasQuePedem, totalVagas)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }

    // ---------------- MÉTODOS HTTP ----------------

    private suspend fun makeGetRequest(urlString: String): String = withContext(Dispatchers.IO) {
        logRequest("GET", urlString)
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val result = reader.readText()
                reader.close()
                result
            } else {
                val errorReader = BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream))
                val errorResponse = errorReader.readText()
                errorReader.close()
                throw Exception("HTTP Error $responseCode: $errorResponse")
            }
            
            logResponse(response)
            response
        } catch (e: Exception) {
            println("DatabaseHelper GET Error: ${e.message}")
            throw e
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun makePostRequest(urlString: String, jsonBody: String): String = withContext(Dispatchers.IO) {
        logRequest("POST", urlString, jsonBody)
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            // Enviar dados
            val writer = OutputStreamWriter(connection.outputStream, "UTF-8")
            writer.write(jsonBody)
            writer.flush()
            writer.close()
            
            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
                val result = reader.readText()
                reader.close()
                result
            } else {
                val errorReader = BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream))
                val errorResponse = errorReader.readText()
                errorReader.close()
                throw Exception("HTTP Error $responseCode: $errorResponse")
            }
            
            logResponse(response)
            response
        } catch (e: Exception) {
            println("DatabaseHelper POST Error: ${e.message}")
            throw e
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun makePutRequest(urlString: String, jsonBody: String): String = withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 10000
            
            // Enviar dados
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonBody)
            writer.flush()
            writer.close()
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                response
            } else {
                throw Exception("HTTP Error: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }

    // Métodos não utilizados mas mantidos para compatibilidade
    fun cadastrarEmpresa(nome: String, email: String, senha: String): Boolean = true
    fun cadastrarCandidato(nome: String, email: String, senha: String, curriculo: String?): Boolean = true
    fun listarCursosEmAndamento(candidatoId: Int): List<CursoEmAndamento> = emptyList()
}
