import Memoria.MemoriaFisica;
import Nucleo.*;
import Procesos.HiloProceso;
import Procesos.PCB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  SIMULADOR DE SISTEMA OPERATIVO 2025  ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // 1. Configuración básica
        System.out.println("=== CONFIGURACIÓN INICIAL ===");
        
        System.out.print("Número de marcos de memoria (default 4): ");
        int numMarcos = leerEntero(scanner, 4);
        
        System.out.print("Tamaño de marco en KB (default 1024): ");
        int tamanoMarco = leerEntero(scanner, 1024);
        
        System.out.print("Algoritmo de planificación [FCFS|SJF|RR] (default FCFS): ");
        String algoritmoPlan = leerString(scanner, "FCFS").toUpperCase();
        
        System.out.print("Algoritmo de reemplazo [FIFO|LRU|OPTIMO] (default FIFO): ");
        String algoritmoMem = leerString(scanner, "FIFO").toUpperCase();
        
        int quantum = 3;
        if (algoritmoPlan.equals("RR")) {
            System.out.print("Quantum para RR (default 3): ");
            quantum = leerEntero(scanner, 3);
        }
        
        // 2. Inicializar módulos
        System.out.println("\n=== INICIALIZANDO MÓDULOS ===");
        
        Reloj reloj = new Reloj();
        CPU cpu = new CPU();
        MemoriaFisica memoria = new MemoriaFisica(numMarcos, tamanoMarco, algoritmoMem);
        Planificador planificador = new Planificador(memoria, algoritmoPlan, quantum, reloj);
        
        System.out.println("✓ Reloj del sistema inicializado");
        System.out.println("✓ CPU inicializada");
        System.out.println("✓ Memoria física: " + numMarcos + " marcos de " + tamanoMarco + "KB");
        System.out.println("✓ Planificador: " + algoritmoPlan + 
                         (algoritmoPlan.equals("RR") ? " (Quantum=" + quantum + ")" : ""));
        System.out.println("✓ Reemplazo de páginas: " + algoritmoMem);
        
        // 3. Cargar procesos
        System.out.println("\n=== CARGA DE PROCESOS ===");
        
        // Crear procesos de ejemplo (simplificado)
        crearProcesosEjemplo(cpu, planificador);
        
        // 4. Ejecutar simulación
        System.out.println("\n=== INICIANDO SIMULACIÓN ===");
        planificador.iniciarSimulacion();
        
        // 5. Mostrar resultados
        System.out.println("\n=== RESULTADOS FINALES ===");
        planificador.imprimirMetricas();
        memoria.imprimirEstadisticas();
        
        scanner.close();
    }
    
    private static int leerEntero(Scanner scanner, int defaultValue) {
        try {
            String input = scanner.nextLine();
            if (input.isEmpty()) return defaultValue;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private static String leerString(Scanner scanner, String defaultValue) {
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : input;
    }
    
    private static void crearProcesosEjemplo(CPU cpu, Planificador planificador) {
        System.out.println("Creando 4 procesos de ejemplo...");
        
        // Usar Arrays.asList para Java 8
        List<String> rafagas1 = Arrays.asList("CPU(4)", "E/S(2)", "CPU(3)");
        List<Integer> paginas1 = Arrays.asList(1, 2, 3);
        PCB pcb1 = new PCB(1, 0, rafagas1, 1, paginas1);
        planificador.agregarProceso(new HiloProceso(pcb1, cpu));
        
        List<String> rafagas2 = Arrays.asList("CPU(6)", "E/S(3)", "CPU(2)");
        List<Integer> paginas2 = Arrays.asList(4, 5);
        PCB pcb2 = new PCB(2, 1, rafagas2, 2, paginas2);
        planificador.agregarProceso(new HiloProceso(pcb2, cpu));
        
        List<String> rafagas3 = Arrays.asList("CPU(5)", "E/S(1)", "CPU(4)");
        List<Integer> paginas3 = Arrays.asList(1, 2, 6, 7);
        PCB pcb3 = new PCB(3, 2, rafagas3, 1, paginas3);
        planificador.agregarProceso(new HiloProceso(pcb3, cpu));
        
        List<String> rafagas4 = Arrays.asList("CPU(3)", "E/S(2)", "CPU(2)");
        List<Integer> paginas4 = Arrays.asList(1, 3, 5);
        PCB pcb4 = new PCB(4, 3, rafagas4, 3, paginas4);
        planificador.agregarProceso(new HiloProceso(pcb4, cpu));
        
        System.out.println("✓ 4 procesos de ejemplo creados");
    }
}