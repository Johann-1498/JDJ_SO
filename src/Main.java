import Memoria.MemoriaFisica;
import Nucleo.CPU;
import Nucleo.Planificador;
import Nucleo.Reloj;
<<<<<<< HEAD
import Memoria.MemoriaFisica;
import Sincronizacion.Sincronizador;
import IO.ProcesosParser;
import Comun.ConfiguracionSistema;
import Procesos.PCB;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SIMULADOR DE SO ===");
        
        try {
            // 1. Cargar configuración
            ConfiguracionSistema config = cargarConfiguracion(args);
            
            // 2. Cargar procesos desde archivo
            List<PCB> procesos = ProcesosParser.parsearArchivo("procesos.txt");
            
            // 3. Inicializar componentes del sistema
            Reloj reloj = new Reloj();
            CPU cpu = new CPU();
            MemoriaFisica memoria = new MemoriaFisica(
                config.MARCOS_MEMORIA, 
                config.TAMANIO_MARCO, 
                config.ALGORITMO_REEMPLAZO
            );
            
            Sincronizador sincronizador = new Sincronizador();
            sincronizador.setMemoria(memoria);
            
            System.out.println("\n=== SISTEMA INICIALIZADO ===");
            System.out.println("Hardware: CPU y Reloj listos");
            System.out.println("Memoria: " + config.MARCOS_MEMORIA + " marcos, algoritmo " + 
                             config.ALGORITMO_REEMPLAZO);
            System.out.println("Procesos: " + procesos.size() + " cargados");
            System.out.println("Planificación: " + config.ALGORITMO_PLANIF + 
                             (config.QUANTUM > 0 ? " (Quantum=" + config.QUANTUM + ")" : ""));
            
            // 4. Iniciar planificador (a implementar por Estudiante 2)
            iniciarPlanificador(procesos, cpu, memoria, sincronizador, config);
            
            // 5. Esperar finalización
            esperarFinalizacion();
            
            // 6. Generar reportes finales
            generarReportesFinales(procesos, memoria);
            
        } catch (Exception e) {
            System.err.println("Error en la simulación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static ConfiguracionSistema cargarConfiguracion(String[] args) {
        ConfiguracionSistema config = new ConfiguracionSistema();
        
        // Parsear argumentos de línea de comandos
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--marcos":
                    config.MARCOS_MEMORIA = Integer.parseInt(args[++i]);
                    break;
                case "--planif":
                    config.ALGORITMO_PLANIF = args[++i];
                    break;
                case "--reemplazo":
                    config.ALGORITMO_REEMPLAZO = args[++i];
                    break;
                case "--quantum":
                    config.QUANTUM = Integer.parseInt(args[++i]);
                    break;
                case "--config":
                    try {
                        config = ProcesosParser.leerConfiguracion(args[++i]);
                    } catch (Exception e) {
                        System.err.println("Error leyendo archivo de configuración: " + e.getMessage());
                    }
                    break;
            }
        }
        
        return config;
    }
    
    private static void iniciarPlanificador(List<PCB> procesos, CPU cpu, 
                                           MemoriaFisica memoria, 
                                           Sincronizador sincronizador,
                                           ConfiguracionSistema config) {
        System.out.println("\n=== INICIANDO PLANIFICADOR ===");
        
        // ESTO DEBE SER IMPLEMENTADO POR EL ESTUDIANTE 2
        // Se creará una instancia de PlanificadorCPU y se iniciará la simulación
        
        System.out.println("Planificador por implementar...");
        
        // Simulación básica mientras se implementa
        simularBasico(procesos, cpu, memoria, sincronizador);
    }
    
    private static void simularBasico(List<PCB> procesos, CPU cpu, 
                                     MemoriaFisica memoria, 
                                     Sincronizador sincronizador) {
        // Simulación temporal - reemplazar con planificador real
        for (PCB proceso : procesos) {
            try {
                System.out.println("\n--- Intentando ejecutar P" + proceso.getPid() + " ---");
                
                // Solicitar ejecución al sincronizador
                boolean aprobado = sincronizador.solicitarEjecucion(proceso);
                
                if (aprobado) {
                    // Ejecutar primera ráfaga de CPU
                    if (proceso.getRafagas().size() > 0) {
                        int duracion = proceso.getRafagas().get(0).getDuracion();
                        System.out.println("Ejecutando " + duracion + " unidades de CPU");
                        cpu.ejecutarRafaga(duracion);
                        
                        // Notificar fin de ráfaga
                        sincronizador.notificarFinRafaga(proceso.getPid(), duracion);
                    }
                    
                    // Liberar memoria del proceso
                    memoria.liberarProceso(proceso.getPid());
                }
                
            } catch (Exception e) {
                System.err.println("Error ejecutando P" + proceso.getPid() + ": " + e.getMessage());
            }
        }
    }
    
    private static void esperarFinalizacion() throws InterruptedException {
        System.out.println("\n=== ESPERANDO FINALIZACIÓN ===");
        // En implementación real, esperar a que todos los procesos terminen
        Thread.sleep(2000);
    }
    
    private static void generarReportesFinales(List<PCB> procesos, MemoriaFisica memoria) {
        System.out.println("\n=== REPORTES FINALES ===");
        
        // Reporte de memoria
        memoria.imprimirEstadisticas();
        
        // Reporte de procesos
        System.out.println("\n=== RESUMEN DE PROCESOS ===");
        for (PCB p : procesos) {
            p.calcularMetricas();
            System.out.printf("P%d: Espera=%d, Retorno=%d, CPU=%d%n",
                             p.getPid(), p.getTiempoEspera(), 
                             p.getTiempoRetorno(), p.getTiempoEnCPU());
        }
=======
import Procesos.HiloProceso;
import Procesos.PCB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(" CONFIGURANDO SISTEMA OPERATIVO ");

        // 1. Configuración de Hardware y Módulos
        int numMarcos = 4; // Puedes cambiar esto para probar con menos memoria
        int tamanoMarco = 4096; // 4KB
        String algoritmo = "FCFS"; // Cambiar a "SJF" o "RR" luego

        Reloj reloj = new Reloj();
        CPU cpu = new CPU();
        MemoriaFisica memoria = new MemoriaFisica(numMarcos, tamanoMarco);
        Planificador planificador = new Planificador(memoria, algoritmo);

        System.out.println("Hardware listo: RAM (" + numMarcos + " marcos), CPU inicializada.");

        // 2. Carga de Procesos desde archivo
        try {
            cargarProcesosDesdeArchivo("inputs/procesos.txt", cpu, planificador);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: No se encontró el archivo inputs/procesos.txt");
            return;
        }

        System.out.println("Procesos cargados en la cola.");

        // 3. Inicio de Simulación
        // NOTA: Aún no iniciamos la simulación (planificador.iniciarSimulacion())
        // porque esperamos a que el Estudiante 2 y 3 terminen su lógica interna en esta ronda.
        System.out.println("Sistema listo. Esperando señal de inicio (Ronda 3)...");
>>>>>>> 369e24daf1e09511f092d926340e850643d3a979
    }

    private static void cargarProcesosDesdeArchivo(String ruta, CPU cpu, Planificador planificador) throws FileNotFoundException {
        File archivo = new File(ruta);
        Scanner scanner = new Scanner(archivo);
        
        System.out.println("Leyendo archivo de procesos...");

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty() || linea.startsWith("#")) continue; // Saltar vacíos o comentarios

            // Formato esperado: PID TiempoLlegada TiempoRafaga Pagina1,Pagina2,Pagina3
            // Ejemplo: 1 0 3 1,2
            String[] partes = linea.split(" ");
            
            int pid = Integer.parseInt(partes[0]);
            int llegada = Integer.parseInt(partes[1]);
            int rafaga = Integer.parseInt(partes[2]);
            
            // Parsear páginas (separadas por comas)
            String[] pagsStr = partes[3].split(",");
            List<Integer> paginas = new ArrayList<>();
            for (String p : pagsStr) {
                paginas.add(Integer.parseInt(p));
            }

            // Crea objetos
            PCB pcb = new PCB(pid, llegada, rafaga, paginas);
            HiloProceso proceso = new HiloProceso(pcb, cpu);
            
            // Enviarlos al planificador
            planificador.agregarProceso(proceso);
            System.out.println(" -> Proceso " + pid + " cargado. Requiere páginas: " + paginas);
        }
        scanner.close();
    }
}
