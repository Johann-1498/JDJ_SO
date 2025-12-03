package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Planificador {
    private LinkedList<HiloProceso> colaListos;
    private LinkedList<HiloProceso> colaBloqueados;
    private MemoriaFisica memoria;
    private String algoritmo;
    private int quantum;
    private Reloj reloj;
    private Lock lock;
    private Semaphore semaforoCPU;
    
    // Métricas
    private int tiempoTotalEjecucion;
    private Map<Integer, Integer> tiemposEspera;
    private Map<Integer, Integer> tiemposRetorno;
    private Map<Integer, Integer> tiemposLlegada;
    private List<String> logEjecucion;
    
    public Planificador(MemoriaFisica memoria, String algoritmo, int quantum, Reloj reloj) {
        this.memoria = memoria;
        this.algoritmo = algoritmo;
        this.quantum = quantum;
        this.reloj = reloj;
        this.colaListos = new LinkedList<>();
        this.colaBloqueados = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.semaforoCPU = new Semaphore(1); // Solo un proceso en CPU
        
        // Inicializar métricas
        this.tiempoTotalEjecucion = 0;
        this.tiemposEspera = new HashMap<>();
        this.tiemposRetorno = new HashMap<>();
        this.tiemposLlegada = new HashMap<>();
        this.logEjecucion = new ArrayList<>();
    }
    
    public void agregarProceso(HiloProceso proceso) {
        lock.lock();
        try {
            colaListos.add(proceso);
            PCB pcb = proceso.getPcb();
            pcb.setEstado("LISTO");
            tiemposLlegada.put(pcb.getPid(), reloj.getTiempo());
            tiemposEspera.put(pcb.getPid(), 0);
            
            log("Proceso P" + pcb.getPid() + " agregado a cola de listos");
        } finally {
            lock.unlock();
        }
    }
    
    public void iniciarSimulacion() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   INICIANDO SIMULACIÓN - " + algoritmo + "   ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
        
        while (!colaListos.isEmpty() || !colaBloqueados.isEmpty()) {
            // 1. Ordenar cola según algoritmo
            ordenarCola();
            
            // 2. Mover procesos bloqueados a listos si terminaron E/S
            verificarBloqueados();
            
            // 3. Ejecutar proceso
            if (!colaListos.isEmpty()) {
                if (algoritmo.equals("RR")) {
                    ejecutarRoundRobin();
                } else {
                    ejecutarEstandar();
                }
            } else if (!colaBloqueados.isEmpty()) {
                // Si solo hay procesos bloqueados, avanzar tiempo
                reloj.tic();
                log("Reloj: " + reloj.getTiempo() + " - Esperando procesos bloqueados...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
        }
        
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     TODOS LOS PROCESOS TERMINADOS       ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
    
    private void ejecutarEstandar() {
        HiloProceso proceso = colaListos.poll();
        PCB pcb = proceso.getPcb();
        
        try {
            semaforoCPU.acquire();
            
            // Verificar memoria
            if (!memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
                log("! P" + pcb.getPid() + " BLOQUEADO - Sin memoria suficiente");
                pcb.setEstado("BLOQUEADO");
                colaBloqueados.add(proceso);
                semaforoCPU.release();
                return;
            }
            
            // Ejecutar
            int tiempoInicio = reloj.getTiempo();
            pcb.setEstado("EJECUTANDO");
            
            log(">>> P" + pcb.getPid() + " INICIA ejecución en t=" + tiempoInicio + 
                " (Ráfaga: " + pcb.getRafagaActual() + ")");
            
            proceso.run();
            
            // Actualizar métricas
            int tiempoFin = reloj.getTiempo();
            tiempoTotalEjecucion += (tiempoFin - tiempoInicio);
            tiemposRetorno.put(pcb.getPid(), tiempoFin - tiemposLlegada.get(pcb.getPid()));
            
            log("<<< P" + pcb.getPid() + " TERMINA en t=" + tiempoFin);
            
            memoria.liberarProceso(pcb.getPid());
            semaforoCPU.release();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void ejecutarRoundRobin() {
        HiloProceso proceso = colaListos.poll();
        PCB pcb = proceso.getPcb();
        
        try {
            semaforoCPU.acquire();
            
            // Verificar memoria
            if (!memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
                log("! P" + pcb.getPid() + " BLOQUEADO - Sin memoria suficiente");
                pcb.setEstado("BLOQUEADO");
                colaBloqueados.add(proceso);
                semaforoCPU.release();
                return;
            }
            
            // Calcular tiempo de ejecución
            int tiempoNecesario = pcb.getTiempoRestante();
            int tiempoTurno = Math.min(quantum, tiempoNecesario);
            
            int tiempoInicio = reloj.getTiempo();
            pcb.setEstado("EJECUTANDO");
            
            log(">>> P" + pcb.getPid() + " INICIA quantum en t=" + tiempoInicio + 
                " (Usará: " + tiempoTurno + "/" + tiempoNecesario + ")");
            
            // Simular ejecución
            Thread.sleep(tiempoTurno * 100);
            reloj.setTiempo(reloj.getTiempo() + tiempoTurno);
            
            pcb.actualizarTiempoRestante(tiempoTurno);
            tiempoTotalEjecucion += tiempoTurno;
            
            // Verificar si terminó
            if (pcb.getTiempoRestante() <= 0) {
                pcb.setEstado("TERMINADO");
                int tiempoFin = reloj.getTiempo();
                tiemposRetorno.put(pcb.getPid(), tiempoFin - tiemposLlegada.get(pcb.getPid()));
                
                log("<<< P" + pcb.getPid() + " TERMINA en t=" + tiempoFin);
                memoria.liberarProceso(pcb.getPid());
            } else {
                pcb.setEstado("LISTO");
                log("<<< P" + pcb.getPid() + " Fin de quantum. Vuelve a cola (Faltan: " + 
                    pcb.getTiempoRestante() + ")");
                colaListos.add(proceso);
            }
            
            semaforoCPU.release();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void verificarBloqueados() {
        lock.lock();
        try {
            Iterator<HiloProceso> iter = colaBloqueados.iterator();
            while (iter.hasNext()) {
                HiloProceso proceso = iter.next();
                PCB pcb = proceso.getPcb();
                
                if (pcb.getEstado().equals("BLOQUEADO") && 
                    memoria.hayMemoriaDisponible(pcb.getPaginasRequeridas().size())) {
                    
                    pcb.setEstado("LISTO");
                    colaListos.add(proceso);
                    iter.remove();
                    
                    log("P" + pcb.getPid() + " DESBLOQUEADO - Memoria disponible");
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void ordenarCola() {
        lock.lock();
        try {
            if (algoritmo.equals("SJF")) {
                Collections.sort(colaListos, Comparator.comparingInt(p -> 
                    p.getPcb().getTiempoRafagaTotal()));
                log("[SJF] Cola ordenada por duración total");
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void imprimirMetricas() {
        System.out.println("\n=== MÉTRICAS DE PLANIFICACIÓN ===");
        System.out.println("Algoritmo utilizado: " + algoritmo);
        if (algoritmo.equals("RR")) {
            System.out.println("Quantum: " + quantum);
        }
        
        double tiempoEsperaTotal = 0;
        double tiempoRetornoTotal = 0;
        int procesosCompletados = tiemposRetorno.size();
        
        System.out.println("\n+------+------------+------------+------------+");
        System.out.println("| PID  | T. Espera  | T. Retorno | T. Respuesta|");
        System.out.println("+------+------------+------------+------------+");
        
        for (int pid : tiemposRetorno.keySet()) {
            int espera = tiemposEspera.getOrDefault(pid, 0);
            int retorno = tiemposRetorno.get(pid);
            int respuesta = espera; // Simplificado
            
            tiempoEsperaTotal += espera;
            tiempoRetornoTotal += retorno;
            
            System.out.printf("| %4d | %10d | %10d | %10d |\n", 
                pid, espera, retorno, respuesta);
        }
        System.out.println("+------+------------+------------+------------+");
        
        double tiempoEsperaPromedio = tiempoEsperaTotal / procesosCompletados;
        double tiempoRetornoPromedio = tiempoRetornoTotal / procesosCompletados;
        double utilizacionCPU = (tiempoTotalEjecucion * 100.0) / reloj.getTiempo();
        
        System.out.printf("\nTiempo promedio de espera: %.2f\n", tiempoEsperaPromedio);
        System.out.printf("Tiempo promedio de retorno: %.2f\n", tiempoRetornoPromedio);
        System.out.printf("Utilización de CPU: %.1f%%\n", utilizacionCPU);
        System.out.println("Tiempo total del sistema: " + reloj.getTiempo());
        System.out.println("Procesos completados: " + procesosCompletados);
    }
    
    public void imprimirLogEjecucion() {
        System.out.println("\n=== LOG DE EJECUCIÓN ===");
        for (String entrada : logEjecucion) {
            System.out.println(entrada);
        }
    }
    
    private void log(String mensaje) {
        String entrada = "[t=" + reloj.getTiempo() + "] " + mensaje;
        logEjecucion.add(entrada);
        System.out.println(entrada);
    }
}