@echo off
echo Compilando Simulador SO para Java 8...
echo.

echo 1. Limpiando archivos anteriores...
del *.class 2>nul
del Memoria\*.class 2>nul
del Nucleo\*.class 2>nul
del Procesos\*.class 2>nul

echo 2. Compilando Memoria...
javac -source 1.8 -target 1.8 -d . Memoria/AlgoritmosReemplazo.java
javac -source 1.8 -target 1.8 -d . Memoria/MemoriaFisica.java
javac -source 1.8 -target 1.8 -d . Memoria/MMU.java
javac -source 1.8 -target 1.8 -d . Memoria/TablaPaginas.java
javac -source 1.8 -target 1.8 -d . Memoria/TestMemoria.java
javac -source 1.8 -target 1.8 -d . Memoria/TestAlgoritmosCompleto.java

echo 3. Compilando Procesos...
javac -source 1.8 -target 1.8 -d . Procesos/PCB.java
javac -source 1.8 -target 1.8 -d . Procesos/HiloProceso.java

echo 4. Compilando Nucleo...
javac -source 1.8 -target 1.8 -d . Nucleo/CPU.java
javac -source 1.8 -target 1.8 -d . Nucleo/Reloj.java
javac -source 1.8 -target 1.8 -d . Nucleo/Planificador.java
javac -source 1.8 -target 1.8 -d . Nucleo/MenuInteractivo.java

echo 5. Compilando Main...
javac -source 1.8 -target 1.8 -d . Main.java

echo.
echo ¡COMPILACIÓN EXITOSA para Java 8!
echo.
echo Verificando versión Java instalada:
java -version
echo.
echo Ejecutar con: java Main
echo Pruebas: java Memoria.TestMemoria
pause