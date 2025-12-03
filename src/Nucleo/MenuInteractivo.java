package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import java.util.*;

public class MenuInteractivo {
    private Planificador planificador;
    private MemoriaFisica memoria;
    private Scanner scanner;
    private boolean enSimulacion;
    
    public MenuInteractivo(Planificador planificador, MemoriaFisica memoria) {
        this.planificador = planificador;
        this.memoria = memoria;
        this.scanner = new Scanner(System.in);
        this.enSimulacion = false;
    }
    
    public void mostrarMenuPrincipal() {
        int opcion;
        
        do {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║        MENÚ PRINCIPAL - SIMULADOR       ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println("1. Configurar sistema");
            System.out.println("2. Ver estado actual");
            System.out.println("3. Ejecutar simulación completa");
            System.out.println("4. Ejecutar paso a paso");
            System.out.println("5. Cambiar algoritmo de memoria");
            System.out.println("6. Agregar proceso manual");
            System.out.println("7. Ver métricas");
            System.out.println("8. Ejecutar pruebas de memoria");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione opción: ");
            
            opcion = leerEntero(scanner, -1);
            
            switch (opcion) {
                case 1:
                    configurarSistema();
                    break;
                case 2:
                    verEstadoSistema();
                    break;
                case 3:
                    ejecutarSimulacionCompleta();
                    break;
                case 4:
                    ejecutarPasoAPaso();
                    break;
                case 5:
                    cambiarAlgoritmoMemoria();
                    break;
                case 6:
                    agregarProcesoManual();
                    break;
                case 7:
                    mostrarMetricas();
                    break;
                case 8:
                    ejecutarPruebasMemoria();
                    break;
                case 0:
                    System.out.println("Saliendo del simulador...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }
    
    private void configurarSistema() {
        System.out.println("\n--- CONFIGURACIÓN DEL SISTEMA ---");
        System.out.println("Función en desarrollo...");
    }
    
    private void verEstadoSistema() {
        System.out.println("\n--- ESTADO DEL SISTEMA ---");
        memoria.imprimirEstado();
        System.out.println("\nAlgoritmo actual: " + memoria.getAlgoritmoActual());
        System.out.println("Fallos de página: " + memoria.getFallosPagina());
        System.out.println("Reemplazos: " + memoria.getReemplazosRealizados());
    }
    
    private void ejecutarSimulacionCompleta() {
        System.out.println("\n--- EJECUTANDO SIMULACIÓN COMPLETA ---");
        enSimulacion = true;
        planificador.iniciarSimulacion();
        enSimulacion = false;
    }
    
    private void ejecutarPasoAPaso() {
        System.out.println("\n--- MODO PASO A PASO ---");
        System.out.println("Presione Enter para avanzar cada paso...");
        scanner.nextLine();
        
        System.out.println("Ejecutando paso 1...");
        scanner.nextLine();
        System.out.println("Ejecutando paso 2...");
        scanner.nextLine();
        System.out.println("Ejecutando paso 3...");
        
        System.out.println("Modo paso a paso finalizado");
    }
    
    private void cambiarAlgoritmoMemoria() {
        System.out.println("\n--- CAMBIAR ALGORITMO DE MEMORIA ---");
        System.out.println("1. FIFO (First In First Out)");
        System.out.println("2. LRU (Least Recently Used)");
        System.out.println("3. OPTIMO (Optimal)");
        System.out.print("Seleccione algoritmo: ");
        
        int opcion = leerEntero(scanner, 1);
        String nuevoAlgo = "";
        
        switch (opcion) {
            case 1: nuevoAlgo = "FIFO"; break;
            case 2: nuevoAlgo = "LRU"; break;
            case 3: nuevoAlgo = "OPTIMO"; break;
            default: nuevoAlgo = "FIFO";
        }
        
        memoria.cambiarAlgoritmo(nuevoAlgo);
        System.out.println("Algoritmo cambiado a: " + nuevoAlgo);
    }
    
    private void agregarProcesoManual() {
        System.out.println("\n--- AGREGAR PROCESO MANUAL ---");
        System.out.println("Función en desarrollo...");
    }
    
    private void mostrarMetricas() {
        System.out.println("\n--- MÉTRICAS DEL SISTEMA ---");
        planificador.imprimirMetricas();
        memoria.imprimirEstadisticas();
    }
    
    private void ejecutarPruebasMemoria() {
        System.out.println("\n--- EJECUTANDO PRUEBAS DE MEMORIA ---");
        
        MemoriaFisica memPrueba = new MemoriaFisica(3, 1024, "FIFO");
        
        System.out.println("\nPrueba 1: Secuencia básica");
        memPrueba.cargarPaginas(1, Arrays.asList(1, 2, 3));
        memPrueba.cargarPaginas(2, Arrays.asList(4, 5));
        
        System.out.println("\nPrueba 2: Reemplazo FIFO");
        memPrueba.cargarPaginas(3, Arrays.asList(6));
        
        System.out.println("\nPrueba completada");
    }
    
    private int leerEntero(Scanner sc, int defaultValue) {
        try {
            String input = sc.nextLine();
            if (input.isEmpty()) return defaultValue;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}