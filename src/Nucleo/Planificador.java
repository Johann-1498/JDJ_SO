package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Collections;

public class Planificador {
    private LinkedList<HiloProceso> colaListos;
    private MemoriaFisica memoria;
    private String algoritmo;
    private int quantum = 3; // Configurable

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

            // 1. Ordenamiento (Solo afecta si es SJF)
            ordenarCola();

            // 2. Ejecución según el tipo de algoritmo
            if (algoritmo.equals("RR")) {
                ejecutarRoundRobin();
            } else {
                ejecutarEstandar(); // FCFS o SJF
            }
        }
        System.out.println("=== TODOS LOS PROCESOS TERMINADOS ===");
    }

    // FCFS y SJF (No expropiativos: ejecutan hasta terminar)
    private void ejecutarEstandar() {
        HiloProceso procesoActual = colaListos.poll();
        PCB pcb = procesoActual.getPcb();

        // Intentar cargar memoria
        if (memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
            // Si hay memoria, corre todo el hilo
            procesoActual.run();
            memoria.liberarProceso(pcb.getPid());
        } else {
            System.out.println("! P" + pcb.getPid() + " Bloqueado por Memoria (Vuelve a cola)");
            colaListos.add(procesoActual);
        }
    }

    // Round Robin (Expropiativo: corre por turnos)
    private void ejecutarRoundRobin() {
        HiloProceso procesoActual = colaListos.poll(); // Sacar el primero
        PCB pcb = procesoActual.getPcb();

        // 1. Verificar memoria (Si no hay RAM, no puede ni usar su turno)
        if (!memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
            System.out.println("! P" + pcb.getPid() + " Sin memoria. Salta turno.");
            colaListos.add(procesoActual);
            return;
        }

        // 2. Calcular tiempo real de este turno
        int tiempoNecesario = pcb.getTiempoRestante();
        int tiempoTurno = Math.min(quantum, tiempoNecesario);

        System.out.println(">>> (RR) P" + pcb.getPid() + " entra a CPU. (Restante: " + tiempoNecesario + " | Usará: "
                + tiempoTurno + ")");

        // 3. Simular ejecución (Directamente aquí o llamando a CPU)
        try {
            pcb.setEstado("EJECUTANDO");
            Thread.sleep(tiempoTurno * 100); // 1 unidad = 100ms

            // 4. Actualizar estado del proceso
            pcb.actualizarTiempoRestante(tiempoTurno);

            // 5. Decidir destino
            if (pcb.getTiempoRestante() <= 0) {
                // Terminó
                pcb.setEstado("TERMINADO");
                System.out.println("<<< P" + pcb.getPid() + " FINALIZÓ ejecución.");
                memoria.liberarProceso(pcb.getPid()); // ¡Importante liberar RAM!
            } else {
                // Se le acabó el tiempo, pero le falta trabajo
                pcb.setEstado("LISTO");
                System.out.println("<<< P" + pcb.getPid() + " Fin de Quantum. Vuelve a cola. (Le faltan "
                        + pcb.getTiempoRestante() + ")");
                colaListos.add(procesoActual); // Vuelve al final de la fila
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void ordenarCola() {
        if (algoritmo.equals("SJF")) {
            Collections.sort(colaListos, Comparator.comparingInt(p -> p.getPcb().getTiempoRafaga()));
            System.out.println("[SJF] Cola reordenada por duración.");

            // Nota: Para SJF real, deberíamos ordenar por 'tiempoRestante' si fuera
            // expropiativo,
            // pero para SJF simple, usar tiempoRafaga inicial está bien.
        }
    }
}