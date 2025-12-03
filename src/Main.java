import memoria.MemoriaFisica;
import Nucleo.CPU;
import Nucleo.Planificador;
import Nucleo.Reloj;
import Procesos.HiloProceso;
import Procesos.PCB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
        
        planificador.iniciarSimulacion();

        long fin = System.currentTimeMillis();
        
        // 5. Reporte Final
        System.out.println("\n=========================================");
        System.out.println("       SIMULACIÓN FINALIZADA CON ÉXITO     ");
        System.out.println("=========================================");
        System.out.println("Tiempo total de simulación (real): " + (fin - inicio) + " ms");
        System.out.println("Estado final de la RAM:");
        memoria.imprimirEstado();
    }

    private static void cargarProcesosDesdeArchivo(String ruta, CPU cpu, Planificador planificador) throws FileNotFoundException {
        File archivo = new File(ruta);
        Scanner scanner = new Scanner(archivo);
        
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

