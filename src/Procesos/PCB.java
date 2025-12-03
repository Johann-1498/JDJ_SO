package Procesos;

import java.util.List;
import java.util.Queue;

public class PCB {
    private int pid;
    private int tiempoLlegada;
    // Cola de ráfagas (CPU o E/S)
    private Queue<Rafaga> rafagas; 
    private List<Integer> paginasRequeridas;
    private String estado;
    
    // Métricas
    private int tiempoFinalizacion;
    private int tiempoCPUUtilizado;

    public PCB(int pid, int tiempoLlegada, Queue<Rafaga> rafagas, List<Integer> paginas) {
        this.pid = pid;
        this.tiempoLlegada = tiempoLlegada;
        this.rafagas = rafagas;
        this.paginasRequeridas = paginas;
        this.estado = "NUEVO";
        this.tiempoCPUUtilizado = 0;
    }

    // --- MÉTODOS DE GESTIÓN DE RÁFAGAS (Aquí estaba tu error) ---
    
    public boolean esRafagaCPU() {
        return rafagas.peek() != null && rafagas.peek().getTipo() == Rafaga.Tipo.CPU;
    }
    
    public int getTiempoRafagaActual() {
        return rafagas.peek() != null ? rafagas.peek().getDuracion() : 0;
    }
    
    public Rafaga getRafagaActual() {
        return rafagas.peek();
    }
    
    // ¡ESTE ES EL MÉTODO QUE TE FALTABA!
    public void completarRafagaActual() {
        if (!rafagas.isEmpty()) {
            rafagas.poll(); // Saca la ráfaga de la cabeza de la cola
        }
    }
    
    public void actualizarTiempoRestante(int tiempoEjecutado) {
        Rafaga actual = rafagas.peek();
        if (actual != null) {
            actual.decrementarDuracion(tiempoEjecutado);
            if (actual.getDuracion() <= 0) {
                rafagas.poll(); // Ya terminó, la sacamos
            }
        }
    }
    
    public boolean haTerminado() {
        return rafagas.isEmpty();
    }

    // --- GETTERS Y SETTERS ---
    public int getPid() { return pid; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public List<Integer> getPaginasRequeridas() { return paginasRequeridas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    // --- MÉTRICAS ---
    public void registrarFin(int tiempoActual) { this.tiempoFinalizacion = tiempoActual; }
    public void agregarTiempoCPU(int t) { this.tiempoCPUUtilizado += t; }
    
    public int getTiempoRetorno() { return tiempoFinalizacion - tiempoLlegada; }
    public int getTiempoEspera() { 
        int retorno = getTiempoRetorno();
        return (retorno > tiempoCPUUtilizado) ? (retorno - tiempoCPUUtilizado) : 0; 
    }
}