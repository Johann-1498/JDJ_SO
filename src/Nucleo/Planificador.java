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
    private Map<Integer, Integer> tiemposInicio;
    private List<String> logEjecucion;
    
    public Planificador(MemoriaFisica memoria, String algoritmo, int quantum, Reloj reloj) {
        this.memoria = memoria;
        this.algoritmo = algoritmo;
        this.quantum = quantum;
        this.reloj = reloj;
        this.colaListos = new LinkedList<>();
        this.colaBloqueados = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.semaforoCPU = new Semaphore(1);
        
        // Inicializar métricas
        this.tiempoTotalEjecucion = 0;
        this.tiemposEspera = new HashMap<>();
        this.tiemposRetorno = new HashMap<>();
        this.tiemposLlegada = new HashMap<>();
        this.tiemposInicio = new HashMap<>();
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
            
            log("Proceso P" + pcb.getPid() + " agregado a cola de listos (Llegada: t=" + reloj.getTiempo() + ")");
        } finally {
            lock.unlock();
        }
    }
    
    public void iniciarSimulacion() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   INICIANDO SIMULACIÓN - " + algoritmo + "   ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
        
        while (!colaListos.isEmpty() || !colaBloqueados.isEmpty()) {
            // 1. Mover procesos bloqueados a listos si terminaron E/S
            verificarBloqueados();
            
            // 2. Ordenar cola según algoritmo
            ordenarCola();
            
            // 3. Ejecutar proceso
            if (!colaListos.isEmpty()) {
                if (algoritmo.equals("RR")) {
                    ejecutarRoundRobin();
                } else {
                    ejecutarEstandar();
                }
            } else if (!colaBloqueados.isEmpty()) {
                // Si solo hay procesos bloqueados, avanzar tiempo mínimo
                int tiempoMinBloqueo = obtenerTiempoMinimoBloqueo();
                if (tiempoMinBloqueo > 0) {
                    log("Todos los procesos bloqueados. Avanzando " + tiempoMinBloqueo + " unidades...");
                    reloj.avanzarTiempo(tiempoMinBloqueo);
                    actualizarTiemposBloqueo(tiempoMinBloqueo);
                }
            }
        }
        
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     TODOS LOS PROCESOS TERMINADOS       ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
    
    private void ejecutarEstandar() {
        HiloProceso proceso = colaListos.poll();
        if (proceso == null) return;
        
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
            pcb.setTiempoInicioEjecucion(tiempoInicio);
            pcb.setEstado("EJECUTANDO");
            tiemposInicio.put(pcb.getPid(), tiempoInicio);
            
            log(">>> P" + pcb.getPid() + " INICIA ejecución en t=" + tiempoInicio + 
                " (Ráfaga: " + pcb.getRafagaActual() + ")");
            
            // Ejecutar el hilo
            proceso.start();
            proceso.join(); // Esperar a que termine
            
            // Calcular tiempo transcurrido
            int tiempoEjecucion = pcb.getTiempoRafagaActual();
            reloj.avanzarTiempo(tiempoEjecucion);
            
            // Actualizar métricas
            int tiempoFin = reloj.getTiempo();
            pcb.setTiempoFinEjecucion(tiempoFin);
            tiempoTotalEjecucion += tiempoEjecucion;
            tiemposRetorno.put(pcb.getPid(), tiempoFin - tiemposLlegada.get(pcb.getPid()));
            
            // Calcular tiempo de espera (tiempo entre llegada y primera ejecución)
            int tiempoEspera = tiempoInicio - tiemposLlegada.get(pcb.getPid());
            tiemposEspera.put(pcb.getPid(), tiempoEspera);
            
            log("<<< P" + pcb.getPid() + " TERMINA ráfaga en t=" + tiempoFin + 
                " (Ejecutó: " + tiempoEjecucion + " unidades)");
            
            // Verificar si el proceso tiene más ráfagas
            if (pcb.getEstado().equals("BLOQUEADO")) {
                log("P" + pcb.getPid() + " entra en E/S por " + 
                    pcb.getTiempoBloqueoRestante() + " unidades");
                colaBloqueados.add(proceso);
            } else if (pcb.getEstado().equals("TERMINADO")) {
                memoria.liberarProceso(pcb.getPid());
                log("P" + pcb.getPid() + " ha terminado completamente");
            } else {
                // Vuelve a la cola de listos
                colaListos.add(proceso);
            }
            
            semaforoCPU.release();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void ejecutarRoundRobin() {
        HiloProceso proceso = colaListos.poll();
        if (proceso == null) return;
        
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
            if (!tiemposInicio.containsKey(pcb.getPid())) {
                tiemposInicio.put(pcb.getPid(), tiempoInicio);
            }
            pcb.setEstado("EJECUTANDO");
            
            log(">>> P" + pcb.getPid() + " INICIA quantum en t=" + tiempoInicio + 
                " (Usará: " + tiempoTurno + "/" + tiempoNecesario + ")");
            
            // Simular ejecución
            Thread.sleep(tiempoTurno * 100); // 100ms por unidad
            reloj.avanzarTiempo(tiempoTurno);
            
            pcb.actualizarTiempoRestante(tiempoTurno);
            tiempoTotalEjecucion += tiempoTurno;
            
            // Verificar si terminó esta ráfaga
            int tiempoFin = reloj.getTiempo();
            
            if (pcb.getTiempoRestante() <= 0) {
                pcb.setEstado("TERMINADO");
                tiemposRetorno.put(pcb.getPid(), tiempoFin - tiemposLlegada.get(pcb.getPid()));
                
                log("<<< P" + pcb.getPid() + " TERMINA en t=" + tiempoFin);
                memoria.liberarProceso(pcb.getPid());
            } else if (pcb.getEstado().equals("BLOQUEADO")) {
                log("<<< P" + pcb.getPid() + " entra en E/S. Se bloquea por " + 
                    pcb.getTiempoBloqueoRestante() + " unidades");
                colaBloqueados.add(proceso);
            } else {
                pcb.setEstado("LISTO");
                log("<<< P" + pcb.getPid() + " Fin de quantum. Vuelve a cola (Faltan: " + 
                    pcb.getTiempoRestante() + ")");
                colaListos.add(proceso);
            }
            
            // Calcular tiempo de espera si es la primera vez
            if (!tiemposEspera.containsKey(pcb.getPid())) {
                int tiempoEspera = tiempoInicio - tiemposLlegada.get(pcb.getPid());
                tiemposEspera.put(pcb.getPid(), tiempoEspera);
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
                
                if (pcb.getEstado().equals("BLOQUEADO") && pcb.getTiempoBloqueoRestante() <= 0) {
                    // E/S completada
                    pcb.setEstado("LISTO");
                    colaListos.add(proceso);
                    iter.remove();
                    
                    log("P" + pcb.getPid() + " DESBLOQUEADO - E/S completada");
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void actualizarTiemposBloqueo(int tiempo) {
        lock.lock();
        try {
            for (HiloProceso proceso : colaBloqueados) {
                PCB pcb = proceso.getPcb();
                if (pcb.getEstado().equals("BLOQUEADO")) {
                    pcb.actualizarTiempoBloqueo(tiempo);
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    private int obtenerTiempoMinimoBloqueo() {
        int minTiempo = Integer.MAX_VALUE;
        for (HiloProceso proceso : colaBloqueados) {
            PCB pcb = proceso.getPcb();
            if (pcb.getEstado().equals("BLOQUEADO") && pcb.getTiempoBloqueoRestante() > 0) {
                minTiempo = Math.min(minTiempo, pcb.getTiempoBloqueoRestante());
            }
        }
        return (minTiempo == Integer.MAX_VALUE) ? 1 : minTiempo;
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
        
        if (procesosCompletados == 0) {
            System.out.println("No hay procesos completados para mostrar métricas.");
            return;
        }
        
        System.out.println("\n+------+------------+------------+------------+");
        System.out.println("| PID  | T. Espera  | T. Retorno | T. Respuesta|");
        System.out.println("+------+------------+------------+------------+");
        
        for (int pid : tiemposRetorno.keySet()) {
            int espera = tiemposEspera.getOrDefault(pid, 0);
            int retorno = tiemposRetorno.get(pid);
            int respuesta = tiemposInicio.getOrDefault(pid, 0) - tiemposLlegada.get(pid);
            
            tiempoEsperaTotal += espera;
            tiempoRetornoTotal += retorno;
            
            System.out.printf("| %4d | %10d | %10d | %10d |\n", 
                pid, espera, retorno, respuesta);
        }
        System.out.println("+------+------------+------------+------------+");
        
        double tiempoEsperaPromedio = tiempoEsperaTotal / procesosCompletados;
        double tiempoRetornoPromedio = tiempoRetornoTotal / procesosCompletados;
        
        double utilizacionCPU = 0;
        if (reloj.getTiempo() > 0) {
            utilizacionCPU = (tiempoTotalEjecucion * 100.0) / reloj.getTiempo();
        }
        
        System.out.printf("\nTiempo promedio de espera: %.2f\n", tiempoEsperaPromedio);
        System.out.printf("Tiempo promedio de retorno: %.2f\n", tiempoRetornoPromedio);
        System.out.printf("Utilización de CPU: %.1f%%\n", utilizacionCPU);
        System.out.println("Tiempo total del sistema: " + reloj.getTiempo());
        System.out.println("Procesos completados: " + procesosCompletados);
        System.out.println("Tiempo total de ejecución: " + tiempoTotalEjecucion);
    }
    
    private void log(String mensaje) {
        String entrada = "[t=" + reloj.getTiempo() + "] " + mensaje;
        logEjecucion.add(entrada);
        System.out.println(entrada);
    }
}