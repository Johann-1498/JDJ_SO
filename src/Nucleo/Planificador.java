package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
import java.util.*;

public class Planificador {
    private LinkedList<HiloProceso> colaListos;
    private LinkedList<HiloProceso> colaBloqueados;
    private MemoriaFisica memoria;
    private String algoritmo;
    private int quantum;
    private Reloj reloj;
    
    // Métricas
    private int tiempoTotalEjecucion;
    private Map<Integer, Integer> tiemposEspera;
    private Map<Integer, Integer> tiemposRetorno;
    private Map<Integer, Integer> tiemposLlegada;
    private Map<Integer, Integer> tiemposInicio;
    private List<HiloProceso> procesosTerminados;
    
    public Planificador(MemoriaFisica memoria, String algoritmo, int quantum, Reloj reloj) {
        this.memoria = memoria;
        this.algoritmo = algoritmo;
        this.quantum = quantum;
        this.reloj = reloj;
        this.colaListos = new LinkedList<>();
        this.colaBloqueados = new LinkedList<>();
        
        // Inicializar métricas
        this.tiempoTotalEjecucion = 0;
        this.tiemposEspera = new HashMap<>();
        this.tiemposRetorno = new HashMap<>();
        this.tiemposLlegada = new HashMap<>();
        this.tiemposInicio = new HashMap<>();
        this.procesosTerminados = new ArrayList<>();
    }
    
    public void agregarProceso(HiloProceso proceso) {
        PCB pcb = proceso.getPcb();
        pcb.setEstado("LISTO");
        colaListos.add(proceso);
        tiemposLlegada.put(pcb.getPid(), reloj.getTiempo());
        tiemposEspera.put(pcb.getPid(), 0);
        
        log("Proceso P" + pcb.getPid() + " agregado a cola de listos");
    }
    
    public void iniciarSimulacion() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   INICIANDO SIMULACIÓN - " + algoritmo + "   ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
        
        int ciclos = 0;
        int maxCiclos = 1000; // Límite de seguridad
        
        while ((!colaListos.isEmpty() || !colaBloqueados.isEmpty()) && ciclos < maxCiclos) {
            ciclos++;
            
            // 1. Actualizar procesos bloqueados
            actualizarBloqueados();
            
            // 2. Mover procesos de bloqueados a listos si terminaron E/S
            moverBloqueadosAListos();
            
            // 3. Ordenar cola según algoritmo
            ordenarCola();
            
            // 4. Ejecutar proceso si hay alguno listo
            if (!colaListos.isEmpty()) {
                if (algoritmo.equals("RR")) {
                    ejecutarRoundRobin();
                } else {
                    ejecutarFCFS();
                }
            } else if (!colaBloqueados.isEmpty()) {
                // Si todos están bloqueados, avanzar el tiempo mínimo de bloqueo
                int tiempoMinimo = calcularTiempoMinimoBloqueo();
                if (tiempoMinimo > 0) {
                    log("Todos los procesos bloqueados. Avanzando " + tiempoMinimo + " unidades...");
                    reloj.avanzarTiempo(tiempoMinimo);
                    actualizarTodosLosBloqueados(tiempoMinimo);
                } else {
                    // Si tiempoMinimo es 0 o negativo, salir del bucle
                    break;
                }
            }
        }
        
        if (ciclos >= maxCiclos) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║  ADVERTENCIA: LÍMITE DE CICLOS ALCANZADO║");
            System.out.println("╚══════════════════════════════════════════╝");
        }
        
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     SIMULACIÓN TERMINADA                ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
    
    private void ejecutarFCFS() {
        HiloProceso proceso = colaListos.poll();
        if (proceso == null) return;
        
        PCB pcb = proceso.getPcb();
        
        // Cargar páginas del proceso
        if (!memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
            log("! P" + pcb.getPid() + " BLOQUEADO - Sin memoria suficiente");
            pcb.setEstado("BLOQUEADO");
            colaBloqueados.add(proceso);
            return;
        }
        
        // Registrar inicio de ejecución
        if (pcb.getTiempoInicioEjecucion() == -1) {
            pcb.setTiempoInicioEjecucion(reloj.getTiempo());
            tiemposInicio.put(pcb.getPid(), reloj.getTiempo());
        }
        
        pcb.setEstado("EJECUTANDO");
        int tiempoInicio = reloj.getTiempo();
        
        // Determinar tiempo de ejecución
        int tiempoEjecucion;
        if (pcb.esRafagaCPU()) {
            tiempoEjecucion = pcb.getTiempoEjecucionRestanteEnRafaga();
            log(">>> P" + pcb.getPid() + " INICIA CPU en t=" + tiempoInicio + 
                " (Duración: " + tiempoEjecucion + ")");
        } else {
            tiempoEjecucion = 0;
        }
        
        // Avanzar tiempo
        reloj.avanzarTiempo(tiempoEjecucion);
        tiempoTotalEjecucion += tiempoEjecucion;
        
        // Ejecutar la ráfaga CPU
        pcb.ejecutarCPU(tiempoEjecucion);
        
        int tiempoFin = reloj.getTiempo();
        log("<<< P" + pcb.getPid() + " TERMINA CPU en t=" + tiempoFin);
        
        // Actualizar tiempo de espera si es la primera ejecución
        if (!tiemposEspera.containsKey(pcb.getPid())) {
            int tiempoEspera = tiempoInicio - tiemposLlegada.get(pcb.getPid());
            tiemposEspera.put(pcb.getPid(), tiempoEspera);
        }
        
        // Manejar siguiente estado
        if (pcb.getEstado().equals("TERMINADO")) {
            pcb.setTiempoFinEjecucion(tiempoFin);
            tiemposRetorno.put(pcb.getPid(), tiempoFin - tiemposLlegada.get(pcb.getPid()));
            memoria.liberarProceso(pcb.getPid());
            procesosTerminados.add(proceso);
            log("✓ P" + pcb.getPid() + " HA TERMINADO COMPLETAMENTE");
        } else if (pcb.getEstado().equals("BLOQUEADO")) {
            log("P" + pcb.getPid() + " entra en E/S por " + pcb.getTiempoBloqueoRestante() + " unidades");
            colaBloqueados.add(proceso);
        } else if (pcb.getEstado().equals("LISTO")) {
            colaListos.add(proceso);
        }
    }
    
    private void ejecutarRoundRobin() {
        HiloProceso proceso = colaListos.poll();
        if (proceso == null) return;
        
        PCB pcb = proceso.getPcb();
        
        // Cargar páginas del proceso
        if (!memoria.cargarPaginas(pcb.getPid(), pcb.getPaginasRequeridas())) {
            log("! P" + pcb.getPid() + " BLOQUEADO - Sin memoria suficiente");
            pcb.setEstado("BLOQUEADO");
            colaBloqueados.add(proceso);
            return;
        }
        
        // Registrar inicio de ejecución
        if (pcb.getTiempoInicioEjecucion() == -1) {
            pcb.setTiempoInicioEjecucion(reloj.getTiempo());
            tiemposInicio.put(pcb.getPid(), reloj.getTiempo());
        }
        
        pcb.setEstado("EJECUTANDO");
        int tiempoInicio = reloj.getTiempo();
        
        // Determinar tiempo de ejecución (quantum o lo que queda)
        int tiempoEjecucion;
        if (pcb.esRafagaCPU()) {
            int tiempoNecesario = pcb.getTiempoEjecucionRestanteEnRafaga();
            tiempoEjecucion = Math.min(quantum, tiempoNecesario);
            log(">>> P" + pcb.getPid() + " INICIA quantum en t=" + tiempoInicio + 
                " (Usará: " + tiempoEjecucion + "/" + tiempoNecesario + ")");
        } else {
            tiempoEjecucion = 0;
        }
        
        // Avanzar tiempo
        reloj.avanzarTiempo(tiempoEjecucion);
        tiempoTotalEjecucion += tiempoEjecucion;
        
        // Ejecutar la ráfaga CPU
        pcb.ejecutarCPU(tiempoEjecucion);
        
        int tiempoFin = reloj.getTiempo();
        
        // Actualizar tiempo de espera si es la primera ejecución
        if (!tiemposEspera.containsKey(pcb.getPid())) {
            int tiempoEspera = tiempoInicio - tiemposLlegada.get(pcb.getPid());
            tiemposEspera.put(pcb.getPid(), tiempoEspera);
        }
        
        // Manejar siguiente estado
        if (pcb.getEstado().equals("TERMINADO")) {
            pcb.setTiempoFinEjecucion(tiempoFin);
            tiemposRetorno.put(pcb.getPid(), tiempoFin - tiemposLlegada.get(pcb.getPid()));
            memoria.liberarProceso(pcb.getPid());
            procesosTerminados.add(proceso);
            log("<<< P" + pcb.getPid() + " TERMINA en t=" + tiempoFin);
        } else if (pcb.getEstado().equals("BLOQUEADO")) {
            log("<<< P" + pcb.getPid() + " entra en E/S. Se bloquea por " + 
                pcb.getTiempoBloqueoRestante() + " unidades");
            colaBloqueados.add(proceso);
        } else {
            pcb.setEstado("LISTO");
            log("<<< P" + pcb.getPid() + " Fin de quantum. Vuelve a cola");
            colaListos.add(proceso);
        }
    }
    
    private void actualizarBloqueados() {
        Iterator<HiloProceso> iter = colaBloqueados.iterator();
        while (iter.hasNext()) {
            HiloProceso proceso = iter.next();
            PCB pcb = proceso.getPcb();
            
            if (pcb.getEstado().equals("TERMINADO")) {
                iter.remove();
            }
        }
    }
    
    private void moverBloqueadosAListos() {
        Iterator<HiloProceso> iter = colaBloqueados.iterator();
        while (iter.hasNext()) {
            HiloProceso proceso = iter.next();
            PCB pcb = proceso.getPcb();
            
            if (pcb.getEstado().equals("LISTO")) {
                colaListos.add(proceso);
                iter.remove();
                log("P" + pcb.getPid() + " DESBLOQUEADO - E/S completada");
            }
        }
    }
    
    private void actualizarTodosLosBloqueados(int tiempo) {
        for (HiloProceso proceso : colaBloqueados) {
            PCB pcb = proceso.getPcb();
            pcb.avanzarBloqueo(tiempo);
        }
    }
    
    private int calcularTiempoMinimoBloqueo() {
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
        if (algoritmo.equals("SJF")) {
            Collections.sort(colaListos, Comparator.comparingInt(p -> 
                p.getPcb().getTiempoRafagaTotal()));
        }
    }
    
    public void imprimirMetricas() {
        System.out.println("\n=== MÉTRICAS DE PLANIFICACIÓN ===");
        System.out.println("Algoritmo utilizado: " + algoritmo);
        if (algoritmo.equals("RR")) {
            System.out.println("Quantum: " + quantum);
        }
        
        int procesosCompletados = procesosTerminados.size();
        
        if (procesosCompletados == 0) {
            System.out.println("No hay procesos completados.");
            System.out.println("Procesos en cola de listos: " + colaListos.size());
            System.out.println("Procesos en cola de bloqueados: " + colaBloqueados.size());
            return;
        }
        
        double tiempoEsperaTotal = 0;
        double tiempoRetornoTotal = 0;
        
        System.out.println("\n+------+------------+------------+------------+");
        System.out.println("| PID  | T. Espera  | T. Retorno | T. Respuesta|");
        System.out.println("+------+------------+------------+------------+");
        
        for (HiloProceso proceso : procesosTerminados) {
            PCB pcb = proceso.getPcb();
            int pid = pcb.getPid();
            
            int espera = tiemposEspera.getOrDefault(pid, 0);
            int retorno = tiemposRetorno.getOrDefault(pid, 0);
            int respuesta = tiemposInicio.getOrDefault(pid, 0) - tiemposLlegada.getOrDefault(pid, 0);
            
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
        System.out.println("Procesos pendientes: " + (colaListos.size() + colaBloqueados.size()));
    }
    
    private void log(String mensaje) {
        System.out.println("[t=" + reloj.getTiempo() + "] " + mensaje);
    }
}