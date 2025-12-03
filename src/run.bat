@echo off
echo Ejecutando Simulador de Sistema Operativo...
echo.

rem Verificar que estén compilados
if not exist Main.class (
    echo Los archivos no están compilados. Ejecutando compilación...
    call compilar.bat
)

echo.
echo Seleccione qué ejecutar:
echo 1. Simulador principal (Main)
echo 2. Prueba de memoria (TestMemoria)
echo 3. Prueba de algoritmos (TestAlgoritmosCompleto)
echo 4. Salir
echo.
set /p opcion="Opción: "

if "%opcion%"=="1" (
    java Main
) else if "%opcion%"=="2" (
    java Memoria.TestMemoria
) else if "%opcion%"=="3" (
    java Memoria.TestAlgoritmosCompleto
) else (
    echo Saliendo...
)

pause