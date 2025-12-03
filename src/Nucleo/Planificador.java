package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import Procesos.Rafaga;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Planificador {
    private LinkedList<HiloProceso> colaListos;
    private List<PCB> procesosTerminados; // Para el reporte final
    private MemoriaFisica memoria;
    private Reloj reloj;
    private String algoritmo;
    private int quantum = 3;

    // Constructor actualizado (Main debe coincidir con esto)
    public Planificador(MemoriaFisica memoria, String algoritmo, Reloj reloj) {
        this.memoria = memoria;
        this.algoritmo = algoritmo;
        this.reloj = reloj;
        this.colaListos = new LinkedList<>();
        this.procesosTerminados = new ArrayList<>();
    }

    public void agregarProceso(HiloProceso proceso) {
        colaListos.add(proceso);
        proceso.getPcb().setEstado("LISTO");
    }

    public void iniciarSimulacion() {
        System.out.println("--- Planificador Iniciado (" + algoritmo + ") ---");

        while (!colaListos.isEmpty()) {
            ordenarCola();
            HiloProceso procesoActual = colaListos.poll();
            PCB pcb = procesoActual.getPcb();

            // 1. Verificar Memoria
            if (!memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
                // Si falla, al final de la cola (simulación simple)
                colaListos.add(procesoActual);
                continue; 
            }

            // 2. Verificar tipo de ráfaga (CPU o E/S)
            Rafaga rafaga = pcb.getRafagaActual();
            
            if (rafaga.getTipo() == Rafaga.Tipo.ES) {
                // MANEJO DE E/S (Simulado)
                System.out.println(">>> P" + pcb.getPid() + " Bloqueado por E/S (" + rafaga.getDuracion() + "s)");
                pcb.setEstado("BLOQUEADO");
                // Simular paso del tiempo de E/S
                reloj.avanzarTiempo(rafaga.getDuracion());
                pcb.completarRafagaActual(); // Termina la E/S
                pcb.setEstado("LISTO");
                colaListos.add(procesoActual); // Vuelve a cola de listos
                
            } else {
                // MANEJO DE CPU
                int tiempoDisponible = (algoritmo.equals("RR")) ? quantum : rafaga.getDuracion();
                int tiempoEjecutar = Math.min(tiempoDisponible, rafaga.getDuracion());

                System.out.println(">>> P" + pcb.getPid() + " Ejecutando CPU por " + tiempoEjecutar + "s");
                pcb.setEstado("EJECUTANDO");
                
                // Simular ejecución
                try {
                    Thread.sleep(tiempoEjecutar * 100); 
                } catch (InterruptedException e) {}
                
                reloj.avanzarTiempo(tiempoEjecutar);
                pcb.agregarTiempoCPU(tiempoEjecutar);
                rafaga.decrementarDuracion(tiempoEjecutar);

                if (rafaga.getDuracion() <= 0) {
                    pcb.completarRafagaActual(); // Rafaga CPU terminada
                }

                if (pcb.haTerminado()) {
                    System.out.println("<<< P" + pcb.getPid() + " FINALIZADO.");
                    pcb.setEstado("TERMINADO");
                    pcb.registrarFin(reloj.getTiempo());
                    memoria.liberarProceso(pcb.getPid());
                    procesosTerminados.add(pcb);
                } else {
                    // Si no terminó (por Quantum o porque viene E/S)
                    colaListos.add(procesoActual);
                }
            }
        }
        imprimirReporteFinal();
    }

    private void ordenarCola() {
        if (algoritmo.equals("SJF")) {
            // Ordenar por duración de la ráfaga actual de CPU
            Collections.sort(colaListos, (p1, p2) -> 
                p1.getPcb().getRafagaActual().getDuracion() - p2.getPcb().getRafagaActual().getDuracion());
        }
    }

    private void imprimirReporteFinal() {
        System.out.println("\n=== REPORTE DE MÉTRICAS ===");
        System.out.println("PID | Llegada | Fin | Retorno | Espera");
        double promRetorno = 0, promEspera = 0;
        
        for (PCB p : procesosTerminados) {
            System.out.printf("%3d | %7d | %3d | %7d | %6d%n", 
                p.getPid(), p.getTiempoLlegada(), p.getTiempoLlegada() + p.getTiempoRetorno(), 
                p.getTiempoRetorno(), p.getTiempoEspera());
            promRetorno += p.getTiempoRetorno();
            promEspera += p.getTiempoEspera();
        }
        System.out.println("-----------------------------------");
        System.out.printf("Promedios: Retorno=%.2f, Espera=%.2f%n", 
            promRetorno/procesosTerminados.size(), promEspera/procesosTerminados.size());
    }
}