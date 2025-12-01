package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import java.util.LinkedList;
import java.util.Queue;

public class Planificador {
    private Queue<HiloProceso> colaListos;
    private MemoriaFisica memoria; // Referencia al módulo del Estudiante 3
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
            
            // EN LA RONDA 3 AGREGAREMOS LA VALIDACIÓN DE MEMORIA AQUÍ
            // Por ahora, asumimos que pasa directo:
            
            procesoActual.run(); // Ejecuta el hilo
        }
    }

    private HiloProceso obtenerSiguiente() {
        // Lógica FCFS por defecto (First Come, First Served)
        return colaListos.poll(); 
    }
}