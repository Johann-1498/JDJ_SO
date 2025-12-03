@echo off
echo ========================================
echo COMPILANDO SIMULADOR SO - VERSION CORREGIDA
echo ========================================
echo.

echo Limpiando archivos anteriores...
del *.class 2>nul
del Memoria\*.class 2>nul
del Nucleo\*.class 2>nul
del Procesos\*.class 2>nul

echo.
echo Compilando con compatibilidad Java 8...
echo.

rem Usar --release 8 que es mejor que -source 1.8 -target 1.8
javac --release 8 -cp . Memoria/AlgoritmosReemplazo.java
if errorlevel 1 (
    echo Error compilando AlgoritmosReemplazo.java
    pause
    exit /b 1
)

javac --release 8 -cp . Memoria/MemoriaFisica.java
if errorlevel 1 (
    echo Error compilando MemoriaFisica.java
    pause
    exit /b 1
)

javac --release 8 -cp . Memoria/MMU.java
if errorlevel 1 (
    echo Error compilando MMU.java
    pause
    exit /b 1
)

javac --release 8 -cp . Memoria/TablaPaginas.java
if errorlevel 1 (
    echo Error compilando TablaPaginas.java
    pause
    exit /b 1
)

javac --release 8 -cp . Nucleo/CPU.java
if errorlevel 1 (
    echo Error compilando CPU.java
    pause
    exit /b 1
)

javac --release 8 -cp . Nucleo/Reloj.java
if errorlevel 1 (
    echo Error compilando Reloj.java
    pause
    exit /b 1
)

javac --release 8 -cp . Nucleo/Planificador.java
if errorlevel 1 (
    echo Error compilando Planificador.java
    pause
    exit /b 1
)

javac --release 8 -cp . Procesos/PCB.java
if errorlevel 1 (
    echo Error compilando PCB.java
    pause
    exit /b 1
)

javac --release 8 -cp . Procesos/HiloProceso.java
if errorlevel 1 (
    echo Error compilando HiloProceso.java
    pause
    exit /b 1
)

javac --release 8 -cp . Main.java
if errorlevel 1 (
    echo Error compilando Main.java
    echo.
    echo Intentando compilar con metodo alternativo...
    javac -source 1.8 -target 1.8 -cp . Main.java
    if errorlevel 1 (
        echo Error grave en Main.java. Revisar sintaxis.
        pause
        exit /b 1
    )
)

echo.
echo Creando estructura de directorios...
mkdir inputs 2>nul

echo.
if exist "inputs\procesos.txt" (
    echo Archivo de procesos encontrado.
) else (
    echo Creando archivo de procesos de ejemplo...
    echo # Formato: PID TiempoLlegada CPU(4) E/S(2) CPU(3) Prioridad Paginas > inputs\procesos.txt
    echo 1 0 CPU(4) E/S(2) CPU(3) 1 1,2,3 >> inputs\procesos.txt
    echo 2 1 CPU(6) E/S(3) CPU(2) 2 4,5 >> inputs\procesos.txt
    echo 3 2 CPU(5) E/S(1) CPU(4) 1 1,2,6,7 >> inputs\procesos.txt
    echo 4 3 CPU(3) E/S(2) CPU(2) 3 1,3,5 >> inputs\procesos.txt
)

echo.
echo ========================================
echo COMPILACION EXITOSA!
echo ========================================
echo.
echo Ejecutando simulador...
echo.

java -cp . Main

pause