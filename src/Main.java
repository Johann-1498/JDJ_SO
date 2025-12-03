import Memoria.MemoriaFisica;
import Nucleo.*;
import Procesos.HiloProceso;
import Procesos.PCB;

import java.util.*;

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
        
        int quantum = 2;
        if (algoritmoPlan.equals("RR")) {
            System.out.print("Quantum para RR (default 2): ");
            quantum = leerEntero(scanner, 2);
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
        
        // Crear procesos de ejemplo SIMPLES para evitar problemas
        List<PCB> procesos = crearProcesosSimples(cpu, planificador);
        
        System.out.println("\nResumen de procesos cargados:");
        for (PCB pcb : procesos) {
            System.out.printf("  P%d: %d ráfagas, %d páginas, Tiempo total: %d, Prioridad: %d\n",
                pcb.getPid(), pcb.getRafagas().size(), pcb.getPaginasRequeridas().size(),
                pcb.getTiempoRafagaTotal(), pcb.getPrioridad());
        }
        
        // 4. Ejecutar simulación
        System.out.println("\n=== INICIANDO SIMULACIÓN ===");
        System.out.println("La simulación se ejecutará automáticamente...");
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        
        planificador.iniciarSimulacion();
        
        // 5. Mostrar resultados
        System.out.println("\n=== RESULTADOS FINALES ===");
        planificador.imprimirMetricas();
        memoria.imprimirEstadisticas();
        
        System.out.println("\n=== FIN DE LA SIMULACIÓN ===");
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
    
    private static List<PCB> crearProcesosSimples(CPU cpu, Planificador planificador) {
        System.out.println("Creando procesos simples...");
        
        List<PCB> procesos = new ArrayList<>();
        
        // Proceso 1: Simple, corto
        List<String> rafagas1 = Arrays.asList("CPU(2)", "E/S(1)", "CPU(1)");
        List<Integer> paginas1 = Arrays.asList(1, 2);
        PCB pcb1 = new PCB(1, 0, rafagas1, 1, paginas1);
        planificador.agregarProceso(new HiloProceso(pcb1, cpu));
        procesos.add(pcb1);
        
        // Proceso 2: Simple, corto
        List<String> rafagas2 = Arrays.asList("CPU(1)", "E/S(1)", "CPU(2)");
        List<Integer> paginas2 = Arrays.asList(3, 4);
        PCB pcb2 = new PCB(2, 0, rafagas2, 2, paginas2);
        planificador.agregarProceso(new HiloProceso(pcb2, cpu));
        procesos.add(pcb2);
        
        // Proceso 3: Simple, corto
        List<String> rafagas3 = Arrays.asList("CPU(3)", "E/S(2)", "CPU(1)");
        List<Integer> paginas3 = Arrays.asList(1, 5);
        PCB pcb3 = new PCB(3, 0, rafagas3, 1, paginas3);
        planificador.agregarProceso(new HiloProceso(pcb3, cpu));
        procesos.add(pcb3);
        
        // Proceso 4: Simple, corto
        List<String> rafagas4 = Arrays.asList("CPU(1)", "E/S(1)", "CPU(1)");
        List<Integer> paginas4 = Arrays.asList(2, 6);
        PCB pcb4 = new PCB(4, 0, rafagas4, 3, paginas4);
        planificador.agregarProceso(new HiloProceso(pcb4, cpu));
        procesos.add(pcb4);
        
        System.out.println("✓ " + procesos.size() + " procesos simples creados");
        return procesos;
    }
}