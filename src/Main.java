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
        System.out.println("║  SIMULADOR DE SISTEMA OPERATIVO 2025-B ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // 1. Configuración por consola
        System.out.println("=== CONFIGURACIÓN DEL SISTEMA ===");
        
        System.out.print("Número de marcos de memoria (default 4): ");
        int numMarcos = leerEntero(scanner, 4);
        
        System.out.print("Tamaño de marco en KB (default 4096): ");
        int tamanoMarco = leerEntero(scanner, 4096);
        
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
        System.out.print("Ruta del archivo de procesos (default 'src/inputs/procesos.txt'): ");
        String rutaArchivo = scanner.nextLine();
        if (rutaArchivo.isEmpty()) rutaArchivo = "src/inputs/procesos.txt";
        
        try {
            cargarProcesosDesdeArchivo(rutaArchivo, cpu, planificador);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: No se encontró el archivo " + rutaArchivo);
            System.out.println("Creando procesos de ejemplo...");
            crearProcesosEjemplo(cpu, planificador);
        }
        
        // 4. Iniciar simulación
        System.out.println("\n=== INICIANDO SIMULACIÓN ===");
        planificador.iniciarSimulacion();
        
        // 5. Mostrar métricas finales
        System.out.println("\n=== MÉTRICAS FINALES ===");
        planificador.imprimirMetricas();
        memoria.imprimirEstadisticas();
        
        scanner.close();
    }
    
    private static int leerEntero(Scanner scanner, int defaultValue) {
        String input = scanner.nextLine();
        if (input.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private static String leerString(Scanner scanner, String defaultValue) {
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : input;
    }
    
    private static void cargarProcesosDesdeArchivo(String ruta, CPU cpu, Planificador planificador) 
            throws FileNotFoundException {
        File archivo = new File(ruta);
        Scanner scanner = new Scanner(archivo);
        
        System.out.println("Leyendo archivo: " + ruta);
        int contador = 0;

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty() || linea.startsWith("#")) continue;

            String[] partes = linea.split(" ");
            
            // Nuevo formato: PID TiempoLlegada CPU(4) E/S(2) CPU(3) Prioridad Paginas
            int pid = Integer.parseInt(partes[0]);
            int llegada = Integer.parseInt(partes[1]);
            
            // Parsear ráfagas (CPU y E/S)
            List<String> rafagas = new ArrayList<>();
            for (int i = 2; i < partes.length; i++) {
                if (partes[i].matches("\\d+") || partes[i].contains(",")) {
                    break; // Fin de ráfagas
                }
                rafagas.add(partes[i]);
            }
            
            // Prioridad (opcional)
            int prioridad = 0;
            int indicePaginas = 2 + rafagas.size();
            if (indicePaginas < partes.length && partes[indicePaginas].matches("\\d+")) {
                prioridad = Integer.parseInt(partes[indicePaginas]);
                indicePaginas++;
            }
            
            // Parsear páginas
            List<Integer> paginas = new ArrayList<>();
            if (indicePaginas < partes.length) {
                String[] pagsStr = partes[indicePaginas].split(",");
                for (String p : pagsStr) {
                    paginas.add(Integer.parseInt(p));
                }
            }
            
            PCB pcb = new PCB(pid, llegada, rafagas, prioridad, paginas);
            HiloProceso proceso = new HiloProceso(pcb, cpu);
            
            planificador.agregarProceso(proceso);
            contador++;
            System.out.println("✓ Proceso " + pid + " cargado - " + rafagas.size() + " ráfagas, " + 
                             paginas.size() + " páginas");
        }
        scanner.close();
        System.out.println("Total procesos cargados: " + contador);
    }
    
    private static void crearProcesosEjemplo(CPU cpu, Planificador planificador) {
        // Crear 3 procesos de ejemplo
        // CAMBIAR: List.of() por Arrays.asList() para Java 8
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
        
        System.out.println("✓ 3 procesos de ejemplo creados");
    }
}