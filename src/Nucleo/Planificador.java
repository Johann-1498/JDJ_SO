package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import java.util.LinkedList;
import java.util.Queue;

public class Planificador {
    private Queue<HiloProceso> colaListos;
    private MemoriaFisica memoria; 
    private String algoritmo;

    public Planificador(MemoriaFisica memoria, String algoritmo) {
        this.memoria = memoria;
        this.algoritmo = algoritmo;
        this.colaListos = new LinkedList<>();
    }

    public void agregarProceso(HiloProceso proceso) {
        colaListos.add(proceso);
        proceso.getPcb().setEstado("LISTO");
    }

    public void iniciarSimulacion() {
        System.out.println("--- Planificador Iniciado (" + algoritmo + ") ---");
        
        while (!colaListos.isEmpty()) {
            HiloProceso procesoActual = obtenerSiguiente();
            PCB pcb = procesoActual.getPcb();

            System.out.println("\n[Planificador] Intentando ejecutar P" + pcb.getPid());
            // 1. Preguntamos a Memoria si tiene las páginas
            boolean memoriaLista = memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas());

            if (memoriaLista) {
                // 2. Si hay memoria, ejecutamos
                memoria.imprimirEstado(); // Ver cómo quedó la RAM
                procesoActual.run(); 
            } else {
                // 3. Si falla (simulación), lo bloqueamos o reintentamos
                System.out.println("! BLOQUEADO: P" + pcb.getPid() + " esperando memoria...");
            }
        }
    }

    private HiloProceso obtenerSiguiente() {
        // Lógica FCFS por defecto (First Come, First Served)
        return colaListos.poll(); 
    }
}