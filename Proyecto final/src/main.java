import Memoria.MemoriaFisica;
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
