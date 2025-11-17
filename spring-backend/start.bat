@echo off
echo Iniciando Spring Boot API...
echo Java version:
java -version
echo.

cd /d "%~dp0"
echo Compilando projeto...
call mvnw.cmd clean compile -q

if %errorlevel% neq 0 (
    echo ERRO: Falha na compilacao
    pause
    exit /b 1
)

echo Iniciando aplicacao...
call mvnw.cmd spring-boot:run

pause