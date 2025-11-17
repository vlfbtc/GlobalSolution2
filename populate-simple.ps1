# Script PowerShell para popular banco de dados com dados realistas
# Global Solution 2025 - JobConnect Platform

$BaseUrl = "http://localhost:8081/api"

# Funcao para fazer requisicoes POST
function Invoke-ApiPost {
    param(
        [string]$Endpoint,
        [object]$Data
    )
    
    $json = $Data | ConvertTo-Json -Depth 10
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/$Endpoint" -Method POST -Body $json -ContentType "application/json"
        Write-Host "OK - $Endpoint criado com sucesso" -ForegroundColor Green
        return $response
    }
    catch {
        Write-Host "ERRO - Falha ao criar $Endpoint : $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

Write-Host "Populando banco de dados com dados realistas..." -ForegroundColor Cyan
Write-Host "Adicionando cursos focados no Futuro do Trabalho..." -ForegroundColor Yellow

# Cursos de IA e Machine Learning
$cursos = @(
    @{ titulo = "Machine Learning Aplicado"; categoria = "IA e Machine Learning" },
    @{ titulo = "Deep Learning e Redes Neurais"; categoria = "IA e Machine Learning" },
    @{ titulo = "Prompt Engineering e ChatGPT"; categoria = "IA e Machine Learning" },
    @{ titulo = "Computer Vision"; categoria = "IA e Machine Learning" },
    @{ titulo = "Robotic Process Automation RPA"; categoria = "Automacao" },
    @{ titulo = "IoT e Sistemas Inteligentes"; categoria = "Automacao" },
    @{ titulo = "Automacao Industrial 4.0"; categoria = "Automacao" },
    @{ titulo = "Business Intelligence e Analytics"; categoria = "Dados" },
    @{ titulo = "Big Data e Cloud Computing"; categoria = "Dados" },
    @{ titulo = "Data Science para Iniciantes"; categoria = "Dados" },
    @{ titulo = "Power BI e Visualizacao"; categoria = "Dados" },
    @{ titulo = "ESG e Sustentabilidade Corporativa"; categoria = "Economia Verde" },
    @{ titulo = "Energias Renovaveis"; categoria = "Economia Verde" },
    @{ titulo = "Economia Circular"; categoria = "Economia Verde" },
    @{ titulo = "Transformacao Digital"; categoria = "Digital" },
    @{ titulo = "Ciberseguranca Empresarial"; categoria = "Digital" },
    @{ titulo = "Blockchain e Criptomoedas"; categoria = "Digital" },
    @{ titulo = "E-commerce e Marketing Digital"; categoria = "Digital" },
    @{ titulo = "Lideranca em Tempos de Mudanca"; categoria = "Soft Skills" },
    @{ titulo = "Comunicacao Remota"; categoria = "Soft Skills" },
    @{ titulo = "Pensamento Critico"; categoria = "Soft Skills" },
    @{ titulo = "Gestao de Projetos Ageis"; categoria = "Soft Skills" }
)

# Adicionar cursos
foreach ($curso in $cursos) {
    Invoke-ApiPost -Endpoint "cursos" -Data $curso
    Start-Sleep -Milliseconds 200
}

Write-Host "Adicionando vagas do Futuro do Trabalho..." -ForegroundColor Yellow

# Vagas focadas no futuro
$vagas = @(
    @{ titulo = "Especialista em IA Generativa"; descricao = "Desenvolver solucoes de inteligencia artificial generativa para automatizar processos empresariais e melhorar a experiencia do cliente."; empresaId = 1; cursosRecomendados = "Machine Learning Aplicado,Prompt Engineering e ChatGPT" },
    @{ titulo = "Cientista de Dados Senior"; descricao = "Analisar grandes volumes de dados para identificar padroes, criar modelos preditivos e apoiar decisoes estrategicas."; empresaId = 1; cursosRecomendados = "Business Intelligence e Analytics,Data Science para Iniciantes,Power BI e Visualizacao" },
    @{ titulo = "Engenheiro de Machine Learning"; descricao = "Projetar e implementar algoritmos de aprendizado de maquina para otimizar operacoes e criar produtos inovadores."; empresaId = 1; cursosRecomendados = "Machine Learning Aplicado,Deep Learning e Redes Neurais,Big Data e Cloud Computing" },
    @{ titulo = "Analista de RPA"; descricao = "Identificar processos para automacao e desenvolver robos de software para aumentar eficiencia operacional."; empresaId = 1; cursosRecomendados = "Robotic Process Automation RPA,Transformacao Digital,Gestao de Projetos Ageis" },
    @{ titulo = "Especialista em IoT"; descricao = "Desenvolver solucoes de Internet das Coisas para conectar dispositivos e gerar insights de negocio."; empresaId = 1; cursosRecomendados = "IoT e Sistemas Inteligentes,Automacao Industrial 4.0,Ciberseguranca Empresarial" },
    @{ titulo = "Consultor ESG"; descricao = "Assessorar empresas na implementacao de praticas sustentaveis e compliance com criterios ESG."; empresaId = 1; cursosRecomendados = "ESG e Sustentabilidade Corporativa,Economia Circular,Lideranca em Tempos de Mudanca" },
    @{ titulo = "Desenvolvedor Blockchain"; descricao = "Criar aplicacoes descentralizadas e implementar solucoes baseadas em blockchain para diversos setores."; empresaId = 1; cursosRecomendados = "Blockchain e Criptomoedas,Ciberseguranca Empresarial,Machine Learning Aplicado" },
    @{ titulo = "Arquiteto de Transformacao Digital"; descricao = "Liderar iniciativas de digitalizacao e modernizacao de processos empresariais."; empresaId = 1; cursosRecomendados = "Transformacao Digital,Lideranca em Tempos de Mudanca,Gestao de Projetos Ageis" },
    @{ titulo = "Especialista em Ciberseguranca"; descricao = "Proteger ativos digitais e implementar politicas de seguranca para ambientes hibridos de trabalho."; empresaId = 1; cursosRecomendados = "Ciberseguranca Empresarial,Transformacao Digital,Pensamento Critico" },
    @{ titulo = "Analista de Energias Renovaveis"; descricao = "Projetar e implementar solucoes de energia limpa para reduzir pegada de carbono corporativa."; empresaId = 1; cursosRecomendados = "Energias Renovaveis,ESG e Sustentabilidade Corporativa,Business Intelligence e Analytics" },
    @{ titulo = "Gerente de Projetos Ageis"; descricao = "Coordenar equipes remotas e hibridas usando metodologias ageis para entrega de projetos inovadores."; empresaId = 1; cursosRecomendados = "Gestao de Projetos Ageis,Lideranca em Tempos de Mudanca,Comunicacao Remota" },
    @{ titulo = "Especialista em People Analytics"; descricao = "Usar analise de dados para otimizar gestao de talentos e melhorar experiencia dos colaboradores."; empresaId = 1; cursosRecomendados = "Business Intelligence e Analytics,Data Science para Iniciantes,Lideranca em Tempos de Mudanca" },
    @{ titulo = "Desenvolvedor de Solucoes Cloud"; descricao = "Criar e manter infraestruturas em nuvem escalaveis e seguras para aplicacoes empresariais."; empresaId = 1; cursosRecomendados = "Big Data e Cloud Computing,Ciberseguranca Empresarial,Transformacao Digital" },
    @{ titulo = "UX Designer para IA"; descricao = "Projetar interfaces intuitivas para sistemas de inteligencia artificial e aprendizado de maquina."; empresaId = 1; cursosRecomendados = "Machine Learning Aplicado,Pensamento Critico,E-commerce e Marketing Digital" },
    @{ titulo = "Coordenador de Sustentabilidade"; descricao = "Implementar e monitorar iniciativas de sustentabilidade corporativa e economia circular."; empresaId = 1; cursosRecomendados = "ESG e Sustentabilidade Corporativa,Economia Circular,Gestao de Projetos Ageis" }
)

# Adicionar vagas
foreach ($vaga in $vagas) {
    Invoke-ApiPost -Endpoint "vagas" -Data $vaga
    Start-Sleep -Milliseconds 300
}

Write-Host "Dados realistas adicionados com sucesso!" -ForegroundColor Green
Write-Host "Verificando totais..." -ForegroundColor Cyan

# Verificar totais
try {
    $totalCursos = (Invoke-RestMethod -Uri "$BaseUrl/cursos" -Method GET).Count
    $totalVagas = (Invoke-RestMethod -Uri "$BaseUrl/vagas" -Method GET).Count
    
    Write-Host "Total de cursos: $totalCursos" -ForegroundColor Green
    Write-Host "Total de vagas: $totalVagas" -ForegroundColor Green
}
catch {
    Write-Host "Erro ao verificar totais: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "JobConnect Platform populada e pronta para uso!" -ForegroundColor Cyan