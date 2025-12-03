<<<<<<< HEAD
import Memoria.MemoriaFisica;
import Nucleo.*;
=======
import memoria.MemoriaFisica;
import Nucleo.CPU;
import Nucleo.Planificador;
import Nucleo.Reloj;
>>>>>>> 146ef368bef9156d6835f054910c6e79b2af3ef5
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
<<<<<<< HEAD
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
=======
        System.out.println("=========================================");
        System.out.println("   SIMULADOR DE SISTEMA OPERATIVO v1.0   ");
        System.out.println("=========================================");

        // 1. Configuración (Podrías pedirla por Scanner scanner.nextLine() si quisieras interactividad)
        // Valores definidos para la prueba final
        int numMarcos = 4;        // Poca memoria para forzar fallos de página
        int tamanoMarco = 4096;
        String algoritmoCPU = "FCFS"; // Prueba cambiar a "SJF" o "RR" después
        String algoritmoMemoria = "FIFO";

        System.out.println("[CONFIG] Algoritmo CPU: " + algoritmoCPU);
        System.out.println("[CONFIG] Algoritmo Memoria: " + algoritmoMemoria);
        System.out.println("[CONFIG] Marcos de RAM: " + numMarcos);
        System.out.println("-----------------------------------------");

        // 2. Inicialización de Componentes
        Reloj reloj = new Reloj();
        CPU cpu = new CPU();
        MemoriaFisica memoria = new MemoriaFisica(numMarcos, tamanoMarco);
        Planificador planificador = new Planificador(memoria, algoritmoCPU);

        // 3. Carga de Procesos
        try {
            cargarProcesosDesdeArchivo("inputs/procesos.txt", cpu, planificador);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR CRITICO: No se encontró inputs/procesos.txt");
            System.exit(1);
        }

        System.out.println("-----------------------------------------");
        System.out.println("Iniciando simulación ahora...");
        System.out.println("-----------------------------------------\n");

        // 4. EJECUCIÓN DEL SISTEMA (Aquí ocurre la magia)
        long inicio = System.currentTimeMillis();
>>>>>>> 146ef368bef9156d6835f054910c6e79b2af3ef5
        
        System.out.println("✓ Reloj del sistema inicializado");
        System.out.println("✓ CPU inicializada");
        System.out.println("✓ Memoria física: " + numMarcos + " marcos de " + tamanoMarco + "KB");
        System.out.println("✓ Planificador: " + algoritmoPlan + 
                         (algoritmoPlan.equals("RR") ? " (Quantum=" + quantum + ")" : ""));
        System.out.println("✓ Reemplazo de páginas: " + algoritmoMem);
        
        // 3. Cargar procesos
        System.out.println("\n=== CARGA DE PROCESOS ===");
        System.out.print("Ruta del archivo de procesos (default 'inputs/procesos.txt'): ");
        String rutaArchivo = scanner.nextLine();
        if (rutaArchivo.isEmpty()) rutaArchivo = "inputs/procesos.txt";
        
        // Crear carpeta inputs si no existe
        File carpetaInputs = new File("inputs");
        if (!carpetaInputs.exists()) {
            carpetaInputs.mkdir();
        }
        
        // Crear archivo de procesos si no existe
        File archivoProcesos = new File(rutaArchivo);
        if (!archivoProcesos.exists()) {
            System.out.println("Creando archivo de procesos de ejemplo...");
            crearArchivoProcesosEjemplo(rutaArchivo);
        }
        
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
<<<<<<< HEAD
        
        // 5. Mostrar métricas finales
        System.out.println("\n=== MÉTRICAS FINALES ===");
        planificador.imprimirMetricas();
        memoria.imprimirEstadisticas();
        
        scanner.close();
=======

        long fin = System.currentTimeMillis();
        
        // 5. Reporte Final
        System.out.println("\n=========================================");
        System.out.println("       SIMULACIÓN FINALIZADA CON ÉXITO     ");
        System.out.println("=========================================");
        System.out.println("Tiempo total de simulación (real): " + (fin - inicio) + " ms");
        System.out.println("Estado final de la RAM:");
        memoria.imprimirEstado();
>>>>>>> 146ef368bef9156d6835f054910c6e79b2af3ef5
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
    
    private static void crearArchivoProcesosEjemplo(String ruta) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(ruta);
            writer.println("# Formato: PID TiempoLlegada CPU(4) E/S(2) CPU(3) Prioridad Paginas");
            writer.println("1 0 CPU(4) E/S(2) CPU(3) 1 1,2,3");
            writer.println("2 1 CPU(6) E/S(3) CPU(2) 2 4,5");
            writer.println("3 2 CPU(5) E/S(1) CPU(4) 1 1,2,6,7");
            writer.close();
            System.out.println("✓ Archivo creado: " + ruta);
        } catch (FileNotFoundException e) {
            System.err.println("Error creando archivo: " + e.getMessage());
        }
    }
    
    private static void cargarProcesosDesdeArchivo(String ruta, CPU cpu, Planificador planificador) 
            throws FileNotFoundException {
        File archivo = new File(ruta);
        Scanner scanner = new Scanner(archivo);
        
<<<<<<< HEAD
        System.out.println("Leyendo archivo: " + ruta);
        int contador = 0;

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty() || linea.startsWith("#")) {
                continue;
            }

            String[] partes = linea.split("\\s+");
            
            if (partes.length < 3) {
                System.err.println("Error: línea inválida: " + linea);
                continue;
            }
            
            try {
                int pid = Integer.parseInt(partes[0]);
                int llegada = Integer.parseInt(partes[1]);
                
                // Parsear ráfagas hasta encontrar números
                List<String> rafagas = new ArrayList<>();
                int i = 2;
                while (i < partes.length && !partes[i].matches("\\d+") && !partes[i].contains(",")) {
                    rafagas.add(partes[i]);
                    i++;
                }
                
                // Prioridad (opcional)
                int prioridad = 0;
                if (i < partes.length && partes[i].matches("\\d+")) {
                    prioridad = Integer.parseInt(partes[i]);
                    i++;
                }
                
                // Parsear páginas
                List<Integer> paginas = new ArrayList<>();
                if (i < partes.length) {
                    String[] pagsStr = partes[i].split(",");
                    for (String p : pagsStr) {
                        paginas.add(Integer.parseInt(p.trim()));
                    }
                }
                
                PCB pcb = new PCB(pid, llegada, rafagas, prioridad, paginas);
                HiloProceso proceso = new HiloProceso(pcb, cpu);
                
                planificador.agregarProceso(proceso);
                contador++;
                System.out.println("✓ Proceso " + pid + " cargado - " + rafagas.size() + " ráfagas, " + 
                                 paginas.size() + " páginas");
                
            } catch (NumberFormatException e) {
                System.err.println("Error parseando línea: " + linea);
            }
        }
        scanner.close();
        System.out.println("Total procesos cargados: " + contador);
    }
    
    private static void crearProcesosEjemplo(CPU cpu, Planificador planificador) {
        // Crear 3 procesos de ejemplo - USAR Arrays.asList() para Java 8
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
=======
        int cont = 0;
        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty() || linea.startsWith("#")) continue; 

            String[] partes = linea.split(" ");
            
            int pid = Integer.parseInt(partes[0]);
            int llegada = Integer.parseInt(partes[1]);
            int rafaga = Integer.parseInt(partes[2]);
            
            String[] pagsStr = partes[3].split(",");
            List<Integer> paginas = new ArrayList<>();
            for (String p : pagsStr) {
                paginas.add(Integer.parseInt(p));
            }

            PCB pcb = new PCB(pid, llegada, rafaga, paginas);
            HiloProceso proceso = new HiloProceso(pcb, cpu);
            planificador.agregarProceso(proceso);
            cont++;
        }
        scanner.close();
        System.out.println("[CARGADOR] Se han cargado " + cont + " procesos en cola de Listos.");
    }
}

>>>>>>> 146ef368bef9156d6835f054910c6e79b2af3ef5
