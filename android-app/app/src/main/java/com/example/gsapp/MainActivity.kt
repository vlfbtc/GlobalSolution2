package com.example.gsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gsapp.ui.components.*
import com.example.gsapp.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GsappTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    var userType by remember { mutableStateOf(SessionManager.currentUserType) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        userType = SessionManager.currentUserType
    }

    if (userType == null) {
        UserTypeSelectionScreen(
            onEmpresa = {
                SessionManager.currentUserType = UserType.EMPRESA
                SessionManager.currentUserId = 1  // empresa de teste (seed)
                userType = UserType.EMPRESA
            },
            onCandidato = {
                SessionManager.currentUserType = UserType.CANDIDATO
                SessionManager.currentUserId = 1  // candidato de teste (seed)
                userType = UserType.CANDIDATO
            }
        )
    } else {
        when (userType) {
            UserType.EMPRESA -> EmpresaFlow(
                onLogout = { 
                    SessionManager.logout()
                    refreshTrigger++
                }
            )
            UserType.CANDIDATO -> CandidatoFlow(
                onLogout = { 
                    SessionManager.logout()
                    refreshTrigger++
                }
            )
            else -> {}
        }
    }
}

@Composable
fun UserTypeSelectionScreen(
    onEmpresa: () -> Unit,
    onCandidato: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Logo/Título
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        "JobConnect",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Conectando talentos a oportunidades",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Botão Empresa
            ElevatedButton(
                onClick = onEmpresa,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Sou uma Empresa",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botão Candidato
            OutlinedButton(
                onClick = onCandidato,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp
                )
            ) {
                Text(
                    "Sou um Candidato",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

enum class EmpresaScreen {
    HOME,
    NOVA_VAGA
}

@Composable
fun EmpresaFlow(onLogout: () -> Unit) {
    var screen by remember { mutableStateOf(EmpresaScreen.HOME) }
    var vagas by remember { mutableStateOf(emptyList<Vaga>()) }
    
    LaunchedEffect(Unit) {
        vagas = DatabaseHelper.listarVagas()
    }

    when (screen) {
        EmpresaScreen.HOME -> EmpresaHomeScreen(
            vagas = vagas.filter { it.empresaId == (SessionManager.currentUserId ?: -1) },
            onNovaVaga = { screen = EmpresaScreen.NOVA_VAGA },
            onLogout = onLogout
        )
        EmpresaScreen.NOVA_VAGA -> NovaVagaScreen(
            onVagaCriada = {
                // Usar corrotina para recarregar as vagas
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    vagas = DatabaseHelper.listarVagas()
                    screen = EmpresaScreen.HOME
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaHomeScreen(
    vagas: List<Vaga>,
    onNovaVaga: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Painel da Empresa",
                actions = {
                    TextButton(
                        onClick = onLogout
                    ) {
                        Text("Sair", color = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNovaVaga,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header com estatísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${vagas.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Vagas Ativas",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "12",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Candidaturas",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }

            if (vagas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Nenhuma vaga cadastrada",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Comece criando sua primeira vaga!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(vagas) { vaga ->
                        CustomCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        vaga.titulo,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        vaga.descricao ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                        if (!vaga.cursosRecomendados.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Cursos recomendados:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            vaga.cursosRecomendados.forEach { curso ->
                                StatusChip(
                                    text = curso.titulo,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                                }
                                StatusChip(
                                    text = "Ativa",
                                    color = Success
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaVagaScreen(onVagaCriada: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var cursosDisponiveis by remember { mutableStateOf(emptyList<Curso>()) }
    
    LaunchedEffect(Unit) {
        cursosDisponiveis = DatabaseHelper.listarCursos()
    }
    val cursosSelecionados = remember { mutableStateListOf<Curso>() }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Criar Nova Vaga",
                onNavigationClick = onVagaCriada
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Informações da Vaga",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título da vaga") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = descricao,
                            onValueChange = { descricao = it },
                            label = { Text("Descrição detalhada") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            maxLines = 5,
                            enabled = !isLoading
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Cursos Recomendados",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "${cursosSelecionados.size} selecionados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "Selecione os cursos que considera importantes para esta vaga:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(cursosDisponiveis) { curso ->
                val checked = cursosSelecionados.contains(curso)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        if (!isLoading) {
                            if (checked) cursosSelecionados.remove(curso)
                            else cursosSelecionados.add(curso)
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (checked) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                if (!isLoading) {
                                    if (isChecked) cursosSelecionados.add(curso)
                                    else cursosSelecionados.remove(curso)
                                }
                            },
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                curso.titulo,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                "Categoria: ${curso.categoria}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (titulo.isNotBlank() && descricao.isNotBlank()) {
                            isLoading = true
                            val empresaId = SessionManager.currentUserId ?: 1
                            // Usar coroutine scope para chamada assíncrona
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                val sucesso = DatabaseHelper.criarVaga(
                                    titulo = titulo,
                                    descricao = descricao,
                                    empresaId = empresaId,
                                    cursosRecomendados = cursosSelecionados.joinToString(",")
                                )
                                
                                if (sucesso) {
                                    onVagaCriada()
                                } else {
                                    // Handle error
                                }
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = titulo.isNotBlank() && descricao.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publicando...")
                        }
                    } else {
                        Text(
                            "Publicar Vaga",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

enum class CandidatoScreen {
    VAGAS,
    CURSOS,
    CURSO_DETALHE,
    CANDIDATURAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatoFlow(onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var cursoSelecionado by remember { mutableStateOf<Curso?>(null) }
    var showCursoDetalhe by remember { mutableStateOf(false) }
    var refreshCursos by remember { mutableStateOf(0) }
    
    val tabs = listOf("Vagas", "Cursos", "Candidaturas")

    if (showCursoDetalhe && cursoSelecionado != null) {
        CursoDetalheScreen(
            curso = cursoSelecionado!!,
            onBack = { 
                showCursoDetalhe = false
                cursoSelecionado = null
                refreshCursos++ // Força refresh da lista de cursos
            },
            onProgressUpdated = {
                refreshCursos++
            }
        )
    } else {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = "JobConnect",
                    actions = {
                        TextButton(
                            onClick = onLogout
                        ) {
                            Text("Sair", color = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavigationTabs(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                
                when (selectedTab) {
                    0 -> VagasTabContent()
                    1 -> CursosTabContent(
                        refreshTrigger = refreshCursos,
                        onCursoClick = { curso ->
                            cursoSelecionado = curso
                            showCursoDetalhe = true
                        }
                    )
                    2 -> CandidaturasTabContent()
                }
            }
        }
    }
}

@Composable
fun VagasTabContent() {
    var vagas by remember { mutableStateOf(emptyList<Vaga>()) }
    var showSnackbar by remember { mutableStateOf(false) }
    var candidaturasExistentes by remember { mutableStateOf(emptySet<Int>()) }
    var totalCandidaturas by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        vagas = DatabaseHelper.listarVagas()
        val candId = SessionManager.currentUserId ?: 1
        val candidaturas = DatabaseHelper.listarCandidaturasComVaga(candId)
        totalCandidaturas = candidaturas.size
        candidaturasExistentes = candidaturas.map { it.vagaTitulo.hashCode() }.toSet()
    }
    var snackbarMessage by remember { mutableStateOf("") }
    
    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                // Header com estatísticas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${vagas.size}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Vagas Disponíveis",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$totalCandidaturas",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Candidaturas",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }
            
            items(vagas) { vaga ->
                CustomCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                vaga.titulo,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            StatusChip(
                                text = "Nova",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            vaga.descricao ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!vaga.cursosRecomendados.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Cursos recomendados:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                vaga.cursosRecomendados.take(2).forEach { curso ->
                                    StatusChip(
                                        text = curso.titulo,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                if (vaga.cursosRecomendados.size > 2) {
                                    Text(
                                        "+${vaga.cursosRecomendados.size - 2}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val jaCandidatou = candidaturasExistentes.contains(vaga.titulo.hashCode())
                        
                        Row {
                            if (jaCandidatou) {
                                Button(
                                    onClick = { /* Não faz nada */ },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    )
                                ) {
                                    Text(
                                        "Já Candidatou",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val candId = SessionManager.currentUserId ?: 1
                                        // Usar coroutine scope para chamada assíncrona
                                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                            val sucesso = DatabaseHelper.candidatarSe(vaga.id, candId)
                                            if (sucesso) {
                                                candidaturasExistentes = candidaturasExistentes + vaga.titulo.hashCode()
                                                totalCandidaturas = totalCandidaturas + 1
                                                snackbarMessage = "Candidatura enviada com sucesso!"
                                            } else {
                                                snackbarMessage = "Erro ao enviar candidatura. Tente novamente."
                                            }
                                            showSnackbar = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Candidatar-se")
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(
                                onClick = { 
                                    // Implementar tela de detalhes da vaga
                                    snackbarMessage = "Detalhes da vaga: ${vaga.titulo}"
                                    showSnackbar = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Detalhes")
                            }
                        }
                    }
                }
            }
        }
        
        // Snackbar
        if (showSnackbar) {
            LaunchedEffect(showSnackbar) {
                kotlinx.coroutines.delay(3000)
                showSnackbar = false
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface
                    )
                ) {
                    Text(
                        text = snackbarMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun CursosTabContent(
    refreshTrigger: Int = 0,
    onCursoClick: (Curso) -> Unit
) {
    var cursosComProgresso by remember { mutableStateOf(emptyList<CursoComProgresso>()) }
    val candidatoId = SessionManager.currentUserId ?: 1
    
    LaunchedEffect(refreshTrigger) {
        cursosComProgresso = DatabaseHelper.listarCursosComProgressoObj(candidatoId)
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "📚 Desenvolva suas habilidades",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Complete os cursos para aumentar suas chances nas vagas!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        items(cursosComProgresso) { cursoProgresso ->
            val curso = Curso(
                id = cursoProgresso.id,
                titulo = cursoProgresso.titulo,
                categoria = cursoProgresso.categoria
            )
            
            CustomCard(
                onClick = { onCursoClick(curso) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            curso.titulo,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusChip(
                            text = curso.categoria ?: "Geral",
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ProgressBar(cursoProgresso.progresso)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        if (cursoProgresso.progresso == 100) {
                            StatusChip(
                                text = "Concluído",
                                color = Success
                            )
                        } else if (cursoProgresso.progresso > 0) {
                            StatusChip(
                                text = "Em andamento",
                                color = Warning
                            )
                        } else {
                            StatusChip(
                                text = "Não iniciado",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CandidaturasTabContent() {
    val candId = SessionManager.currentUserId ?: 1
    var candidaturas by remember { mutableStateOf(emptyList<CandidaturaDetalhe>()) }
    
    LaunchedEffect(Unit) {
        candidaturas = DatabaseHelper.listarCandidaturasComVaga(candId)
    }
    
    if (candidaturas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Nenhuma candidatura ainda",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Quando você se candidatar a vagas, elas aparecerão aqui",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(candidaturas) { candidatura ->
                CustomCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                candidatura.vagaTitulo,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Candidatura enviada em ${candidatura.dataAplicacao}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        StatusChip(
                            text = candidatura.status,
                            color = when (candidatura.status) {
                                "Aprovado" -> Success
                                "Rejeitado" -> Error
                                else -> Warning
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VagasListaScreen(
    vagas: List<Vaga>,
    onApply: (Vaga) -> Unit,
    onSelectVaga: (Vaga) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(vagas) { vaga ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(vaga.titulo, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(vaga.descricao ?: "")

                    if (!vaga.cursosRecomendados.isNullOrEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Cursos recomendados: " +
                                    vaga.cursosRecomendados.joinToString { it.titulo },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Row {
                        Button(onClick = { onApply(vaga) }) {
                            Text("Candidatar-se")
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = { onSelectVaga(vaga) }) {
                            Text("Detalhes")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CursosFeedScreen(
    cursos: List<Curso>,
    onIniciarCurso: (Curso) -> Unit,
    onAbrirDetalhes: (Curso) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(cursos) { curso ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(curso.titulo, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("Categoria: ${curso.categoria ?: "N/A"}")
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Button(onClick = { onIniciarCurso(curso) }) {
                            Text("Iniciar curso")
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = { onAbrirDetalhes(curso) }) {
                            Text("Detalhes")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoDetalheScreen(
    curso: Curso,
    onBack: () -> Unit,
    onProgressUpdated: (() -> Unit)? = null
) {
    val candId = SessionManager.currentUserId ?: 1
    var progresso by remember { mutableStateOf(0) }
    var vagasQuePedem by remember { mutableStateOf(0) }
    var totalVagas by remember { mutableStateOf(0) }
    
    LaunchedEffect(curso.id) {
        progresso = DatabaseHelper.buscarProgressoCurso(curso.id, candId)
        val estatisticas = DatabaseHelper.calcularPercentualVagasQuePedemCurso(curso.id)
        vagasQuePedem = estatisticas.first
        totalVagas = estatisticas.second
    }
    
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Detalhes do Curso",
                onNavigationClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header do curso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            curso.titulo,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StatusChip(
                            text = curso.categoria ?: "Geral",
                            color = Color.White,
                            modifier = Modifier
                        )
                    }
                }
            }

            item {
                // Progresso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Seu Progresso",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ProgressBar(progresso)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (progresso < 100) {
                            Button(
                                onClick = {
                                    // Usar coroutine scope para chamada assíncrona
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        if (progresso == 0) {
                                            DatabaseHelper.iniciarCurso(curso.id, candId)
                                            progresso = 50
                                        }
                                        DatabaseHelper.atualizarProgressoCurso(curso.id, candId, 100)
                                        progresso = 100
                                        onProgressUpdated?.invoke()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (progresso == 0) "Iniciar Curso" else "Marcar como Concluído"
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "✅ Curso Concluído!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Success,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Estatísticas de mercado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "💼 Relevância no Mercado",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (totalVagas > 0) {
                            val percentual = (vagasQuePedem * 100) / totalVagas
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Vagas que exigem este curso:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "$percentual%",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            LinearProgressIndicator(
                                progress = percentual / 100f,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                "$vagasQuePedem de $totalVagas vagas disponíveis exigem este curso",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                "Não há vagas cadastradas no momento para calcular a relevância.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                // Descrição e benefícios (mock)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "📋 Sobre este Curso",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            "Este curso foi desenvolvido para capacitar profissionais com as habilidades mais demandadas pelo mercado. Ao concluí-lo, você estará preparado para as principais oportunidades da área.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "🎯 O que você vai aprender:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val topicos = when (curso.categoria) {
                            "Programação" -> listOf(
                                "Conceitos fundamentais",
                                "Boas práticas de desenvolvimento",
                                "Projetos práticos",
                                "Debugging e testes"
                            )
                            "Design" -> listOf(
                                "Princípios de design",
                                "Ferramentas profissionais",
                                "User Experience",
                                "Prototipação"
                            )
                            else -> listOf(
                                "Fundamentos teóricos",
                                "Aplicação prática",
                                "Casos de uso reais",
                                "Certificação"
                            )
                        }
                        
                        topicos.forEach { topico ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "•",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    topico,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CandidaturasScreen(
    candidaturas: List<CandidaturaDetalhe>,
    modifier: Modifier = Modifier
) {
    if (candidaturas.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Você ainda não se candidatou a nenhuma vaga.")
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(candidaturas) { c ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(c.vagaTitulo, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Status: ${c.status}")
                        Text("Data: ${c.dataAplicacao}")
                    }
                }
            }
        }
    }
}
