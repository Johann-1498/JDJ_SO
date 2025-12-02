package Sincronizacion;

import Procesos.PCB;
import Memoria.MemoriaFisica;
import java.util.*;
import java.util.concurrent.*;

public class Sincronizador {
    
    // Colas de comunicación entre módulos
    private BlockingQueue<Mensaje> colaMensajes;
    private Map<Integer, CompletableFuture<Boolean>> solicitudesMemoria;
    
    // Referencias a módulos principales
    private MemoriaFisica memoria;
    
    // Semáforos para control de concurrencia
    private Semaphore semaforoPlanificador;
    private Semaphore semaforoMemoria;
    private Semaphore semaforoCPU;
    
    // Estado del sistema
    private boolean sistemaActivo;
    
    public Sincronizador() {
        this.colaMensajes = new LinkedBlockingQueue<>();
        this.solicitudesMemoria = new ConcurrentHashMap<>();
        this.semaforoPlanificador = new Semaphore(1);
        this.semaforoMemoria = new Semaphore(1);
        this.semaforoCPU = new Semaphore(1);
        this.sistemaActivo = true;
        
        // Iniciar hilo procesador de mensajes
        new Thread(this::procesarMensajes).start();
    }
    
    public void setMemoria(MemoriaFisica memoria) {
        this.memoria = memoria;
    }
    
    /**
     * Planificador solicita ejecución de proceso
     * Bloquea hasta que memoria confirme páginas cargadas
     */
    public synchronized boolean solicitarEjecucion(PCB proceso) throws InterruptedException {
        System.out.println("[Sincronizador] Planificador solicita ejecutar P" + proceso.getPid());
        
        // Crear solicitud asíncrona para memoria
        CompletableFuture<Boolean> futuro = new CompletableFuture<>();
        solicitudesMemoria.put(proceso.getPid(), futuro);
        
        // Enviar mensaje a memoria
        Mensaje mensaje = new Mensaje(
            "PLANIFICADOR",
            "MEMORIA",
            "SOLICITAR_EJECUCION",
            proceso
        );
        colaMensajes.put(mensaje);
        
        // Esperar respuesta (bloqueante)
        boolean aprobado = futuro.get(10, TimeUnit.SECONDS);
        
        if (aprobado) {
            System.out.println("[Sincronizador] Memoria APROBÓ ejecución de P" + proceso.getPid());
        } else {
            System.out.println("[Sincronizador] Memoria RECHAZÓ ejecución de P" + proceso.getPid());
        }
        
        return aprobado;
    }
    
    /**
     * Memoria responde si proceso puede ejecutar
     */
    public void responderSolicitudMemoria(int pid, boolean aprobado, String razon) {
        CompletableFuture<Boolean> futuro = solicitudesMemoria.remove(pid);
        if (futuro != null) {
            futuro.complete(aprobado);
            
            Mensaje mensaje = new Mensaje(
                "MEMORIA",
                "PLANIFICADOR",
                aprobado ? "EJECUCION_APROBADA" : "EJECUCION_RECHAZADA",
                Map.of("pid", pid, "razon", razon)
            );
            colaMensajes.put(mensaje);
        }
    }
    
    /**
     * Proceso completa ráfaga de CPU
     */
    public void notificarFinRafaga(int pid, int tiempoEjecutado) {
        Mensaje mensaje = new Mensaje(
            "CPU",
            "PLANIFICADOR",
            "FIN_RAFAGA",
            Map.of("pid", pid, "tiempo", tiempoEjecutado)
        );
        colaMensajes.put(mensaje);
    }
    
    /**
     * Proceso necesita E/S
     */
    public void notificarNecesidadES(int pid, int dispositivo, int tiempo) {
        Mensaje mensaje = new Mensaje(
            "PROCESO",
            "IO",
            "SOLICITUD_ES",
            Map.of("pid", pid, "dispositivo", dispositivo, "tiempo", tiempo)
        );
        colaMensajes.put(mensaje);
    }
    
    /**
     * E/S completada
     */
    public void notificarFinES(int pid) {
        Mensaje mensaje = new Mensaje(
            "IO",
            "PLANIFICADOR",
            "FIN_ES",
            Map.of("pid", pid)
        );
        colaMensajes.put(mensaje);
    }
    
    /**
     * Hilo que procesa todos los mensajes del sistema
     */
    private void procesarMensajes() {
        while (sistemaActivo || !colaMensajes.isEmpty()) {
            try {
                Mensaje mensaje = colaMensajes.poll(100, TimeUnit.MILLISECONDS);
                if (mensaje != null) {
                    procesarMensaje(mensaje);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void procesarMensaje(Mensaje mensaje) {
        System.out.printf("[Sincronizador] %s -> %s: %s%n",
                         mensaje.remitente, mensaje.destinatario, mensaje.tipo);
        
        switch (mensaje.tipo) {
            case "SOLICITAR_EJECUCION":
                manejarSolicitudEjecucion(mensaje);
                break;
            case "EJECUCION_APROBADA":
                manejarEjecucionAprobada(mensaje);
                break;
            case "EJECUCION_RECHAZADA":
                manejarEjecucionRechazada(mensaje);
                break;
            case "FIN_RAFAGA":
                manejarFinRafaga(mensaje);
                break;
            case "SOLICITUD_ES":
                manejarSolicitudES(mensaje);
                break;
            case "FIN_ES":
                manejarFinES(mensaje);
                break;
        }
    }
    
    private void manejarSolicitudEjecucion(Mensaje mensaje) {
        try {
            semaforoMemoria.acquire();
            PCB proceso = (PCB) mensaje.datos;
            
            // Verificar si páginas están en memoria
            boolean paginasCargadas = memoria.verificarPaginasCargadas(
                proceso.getPid(), proceso.getPaginasRequeridas()
            );
            
            if (paginasCargadas) {
                responderSolicitudMemoria(proceso.getPid(), true, "Páginas en memoria");
            } else {
                // Cargar páginas faltantes
                System.out.println("[Sincronizador] Cargando páginas para P" + proceso.getPid());
                memoria.cargarPaginas(proceso.getPid(), proceso.getPaginasRequeridas());
                responderSolicitudMemoria(proceso.getPid(), true, "Páginas cargadas");
            }
            
        } catch (InterruptedException e) {
            responderSolicitudMemoria(((PCB)mensaje.datos).getPid(), false, "Error de sincronización");
        } finally {
            semaforoMemoria.release();
        }
    }
    
    private void manejarEjecucionAprobada(Mensaje mensaje) {
        // Notificar al planificador (implementado por Estudiante 2)
        System.out.println("[Sincronizador] Proceso puede ejecutar: " + mensaje.datos);
    }
    
    private void manejarEjecucionRechazada(Mensaje mensaje) {
        // Notificar al planificador que proceso debe esperar
        System.out.println("[Sincronizador] Proceso debe esperar: " + mensaje.datos);
    }
    
    private void manejarFinRafaga(Mensaje mensaje) {
        @SuppressWarnings("unchecked")
        Map<String, Object> datos = (Map<String, Object>) mensaje.datos;
        int pid = (int) datos.get("pid");
        int tiempo = (int) datos.get("tiempo");
        
        // Actualizar tiempo de CPU en proceso
        System.out.printf("[Sincronizador] P%d completó %d unidades de CPU%n", pid, tiempo);
        
        // Notificar al planificador para siguiente decisión
    }
    
    private void manejarSolicitudES(Mensaje mensaje) {
        @SuppressWarnings("unchecked")
        Map<String, Object> datos = (Map<String, Object>) mensaje.datos;
        int pid = (int) datos.get("pid");
        int dispositivo = (int) datos.get("dispositivo");
        int tiempo = (int) datos.get("tiempo");
        
        System.out.printf("[Sincronizador] P%d solicita E/S en dispositivo %d por %d unidades%n",
                         pid, dispositivo, tiempo);
        
        // Simular E/S (en hilo separado)
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 100); // 100ms por unidad
                notificarFinES(pid);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private void manejarFinES(Mensaje mensaje) {
        @SuppressWarnings("unchecked")
        Map<String, Object> datos = (Map<String, Object>) mensaje.datos;
        int pid = (int) datos.get("pid");
        
        System.out.println("[Sincronizador] P" + pid + " completó E/S, listo para continuar");
        
        // Notificar al planificador que proceso está listo
    }
    
    public void detenerSistema() {
        sistemaActivo = false;
    }
    
    /**
     * Clase interna para mensajes del sistema
     */
    private static class Mensaje {
        String remitente;
        String destinatario;
        String tipo;
        Object datos;
        
        Mensaje(String remitente, String destinatario, String tipo, Object datos) {
            this.remitente = remitente;
            this.destinatario = destinatario;
            this.tipo = tipo;
            this.datos = datos;
        }
    }
}