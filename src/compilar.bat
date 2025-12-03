@echo off
echo ========================================
echo COMPILADOR SIMULADOR SO - Java 8 Compatible
echo ========================================
echo.

echo Limpiando archivos anteriores...
del *.class 2>nul
del Memoria\*.class 2>nul
del Nucleo\*.class 2>nul
del Procesos\*.class 2>nul

echo.
echo Buscando Java 8...
where /r "C:\Program Files\Java" java.exe 2>nul | findstr /i "1.8 jdk8" > java8_path.txt
set /p JAVA8_PATH=<java8_path.txt
del java8_path.txt

if "%JAVA8_PATH%"=="" (
    echo Java 8 no encontrado. Usando Java por defecto.
    set JAVA_HOME="C:\Program Files\Java\jdk-21"
) else (
    echo Java 8 encontrado en: %JAVA8_PATH%
    for %%i in ("%JAVA8_PATH%") do set JAVA_HOME=%%~dpi
)

echo.
echo Configurando entorno...
set PATH=%JAVA_HOME%bin;%PATH%

echo.
echo Verificando versiones...
java -version
javac -version

echo.
echo Compilando simulador...
echo.

rem Compilar con compatibilidad Java 8
javac -source 1.8 -target 1.8 -cp . Memoria/AlgoritmosReemplazo.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Memoria/MemoriaFisica.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Memoria/MMU.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Memoria/TablaPaginas.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Nucleo/CPU.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Nucleo/Reloj.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Nucleo/Planificador.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Procesos/PCB.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Procesos/HiloProceso.java
if errorlevel 1 goto error

javac -source 1.8 -target 1.8 -cp . Main.java
if errorlevel 1 goto error

echo.
echo ========================================
echo COMPILACION EXITOSA!
echo ========================================
echo.
echo Ejecutando simulador...
echo.

java -cp . Main

pause
exit /b 0

:error
echo.
echo ========================================
echo ERROR EN LA COMPILACION
echo ========================================
echo.
pause
exit /b 1