package Nucleo;

import Memoria.MemoriaFisica;
import Procesos.HiloProceso;
import Procesos.PCB;
<<<<<<< HEAD
import java.util.*;
=======
import Procesos.Rafaga;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c

public class Planificador {
    private LinkedList<HiloProceso> colaListos;
    private List<PCB> procesosTerminados; // Para el reporte final
    private MemoriaFisica memoria;
    private Reloj reloj;
<<<<<<< HEAD
    
    // Métricas
    private int tiempoTotalEjecucion;
    private Map<Integer, Integer> tiemposEspera;
    private Map<Integer, Integer> tiemposRetorno;
    private Map<Integer, Integer> tiemposLlegada;
    private Map<Integer, Integer> tiemposInicio;
    private List<HiloProceso> procesosTerminados;
    
    public Planificador(MemoriaFisica memoria, String algoritmo, int quantum, Reloj reloj) {
=======
    private String algoritmo;
    private int quantum = 3;

    // Constructor actualizado (Main debe coincidir con esto)
    public Planificador(MemoriaFisica memoria, String algoritmo, Reloj reloj) {
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
        this.memoria = memoria;
        this.algoritmo = algoritmo;
        this.reloj = reloj;
        this.colaListos = new LinkedList<>();
<<<<<<< HEAD
        this.colaBloqueados = new LinkedList<>();
        
        // Inicializar métricas
        this.tiempoTotalEjecucion = 0;
        this.tiemposEspera = new HashMap<>();
        this.tiemposRetorno = new HashMap<>();
        this.tiemposLlegada = new HashMap<>();
        this.tiemposInicio = new HashMap<>();
=======
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
        this.procesosTerminados = new ArrayList<>();
    }

    public void agregarProceso(HiloProceso proceso) {
<<<<<<< HEAD
        PCB pcb = proceso.getPcb();
        pcb.setEstado("LISTO");
        colaListos.add(proceso);
        tiemposLlegada.put(pcb.getPid(), reloj.getTiempo());
        tiemposEspera.put(pcb.getPid(), 0);
        
        log("Proceso P" + pcb.getPid() + " agregado a cola de listos");
=======
        colaListos.add(proceso);
        proceso.getPcb().setEstado("LISTO");
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
    }

    public void iniciarSimulacion() {
<<<<<<< HEAD
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
=======
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
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
        }
        imprimirReporteFinal();
    }
<<<<<<< HEAD
    
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
=======

    private void ordenarCola() {
        if (algoritmo.equals("SJF")) {
            // Ordenar por duración de la ráfaga actual de CPU
            Collections.sort(colaListos, (p1, p2) -> 
                p1.getPcb().getRafagaActual().getDuracion() - p2.getPcb().getRafagaActual().getDuracion());
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
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
<<<<<<< HEAD
        
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
=======
        System.out.println("-----------------------------------");
        System.out.printf("Promedios: Retorno=%.2f, Espera=%.2f%n", 
            promRetorno/procesosTerminados.size(), promEspera/procesosTerminados.size());
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
    }
}