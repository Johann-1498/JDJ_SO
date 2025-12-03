@echo off
echo Limpiando archivos compilados...
del *.class 2>nul
del Memoria\*.class 2>nul
del Nucleo\*.class 2>nul
del Procesos\*.class 2>nul
echo Archivos limpiados.
pause