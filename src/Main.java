import Memoria.MemoriaFisica;
import Nucleo.CPU;
import Nucleo.Planificador;
import Nucleo.Reloj;
import Procesos.HiloProceso;
import Procesos.PCB;
import Procesos.Rafaga;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(" CONFIGURANDO SISTEMA OPERATIVO ");

        int numMarcos = 4;
        int tamanoMarco = 4096;
        String algoritmo = "FCFS"; // Prueba con RR o FCFS

        Reloj reloj = new Reloj();
        CPU cpu = new CPU(); // Se mantiene aunque ahora el Planificador simula el tiempo
        MemoriaFisica memoria = new MemoriaFisica(numMarcos, tamanoMarco);
        
        // CORRECCIÓN DE ERROR: Ahora pasamos 'reloj' al constructor
        Planificador planificador = new Planificador(memoria, algoritmo, reloj);

        try {
            cargarProcesosDesdeArchivo("src/inputs/procesos.txt", cpu, planificador);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: No se encontró el archivo src/inputs/procesos.txt");
            return;
        }
        
        planificador.iniciarSimulacion();
    }

    private static void cargarProcesosDesdeArchivo(String ruta, CPU cpu, Planificador planificador) throws FileNotFoundException {
        File archivo = new File(ruta);
        Scanner scanner = new Scanner(archivo);

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty() || linea.startsWith("#")) continue;

            // Formato Avanzado: PID Llegada CPU(4),E/S(2),CPU(3) Pags
            // Ejemplo: 1 0 CPU(5),E/S(2),CPU(3) 1,2,3
            String[] partes = linea.split(" ");
            
            int pid = Integer.parseInt(partes[0]);
            int llegada = Integer.parseInt(partes[1]);
            
            // Parsear Ráfagas
            Queue<Rafaga> rafagas = new LinkedList<>();
            String[] rafagasStr = partes[2].split(",");
            for (String r : rafagasStr) {
                // r es algo como "CPU(4)" o "E/S(2)"
                boolean esCpu = r.startsWith("CPU");
                int duracion = Integer.parseInt(r.substring(r.indexOf("(") + 1, r.indexOf(")")));
                rafagas.add(new Rafaga(esCpu ? Rafaga.Tipo.CPU : Rafaga.Tipo.ES, duracion));
            }

            // Parsear Páginas
            String[] pagsStr = partes[3].split(",");
            List<Integer> paginas = new ArrayList<>();
            for (String p : pagsStr) {
                paginas.add(Integer.parseInt(p));
            }

            // CORRECCIÓN ERROR: Usar constructor actualizado de PCB
            PCB pcb = new PCB(pid, llegada, rafagas, paginas);
            HiloProceso proceso = new HiloProceso(pcb, cpu);
            
            planificador.agregarProceso(proceso);
        }
        scanner.close();
    }
}