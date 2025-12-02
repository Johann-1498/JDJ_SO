package Procesos;

import java.util.List;

public class PCB {
    private int pid;
    private int tiempoLlegada;
    private int prioridad;
    private List<Rafaga> rafagas;
    private List<Integer> paginasRequeridas;
    private String estado; // "NUEVO", "LISTO", "EJECUTANDO", "BLOQUEADO_MEM", "BLOQUEADO_ES", "TERMINADO"
    
    // Métricas
    private int tiempoInicio;
    private int tiempoFin;
    private int tiempoEspera;
    private int tiempoRetorno;
    
    // Para planificación
    private int rafagaActual;
    private int tiempoEnCPU;
    
    public PCB(int pid, int tiempoLlegada, int prioridad, 
               List<Rafaga> rafagas, List<Integer> paginasRequeridas) {
        this.pid = pid;
        this.tiempoLlegada = tiempoLlegada;
        this.prioridad = prioridad;
        this.rafagas = rafagas;
        this.paginasRequeridas = paginasRequeridas;
        this.estado = "NUEVO";
        this.rafagaActual = 0;
        this.tiempoEnCPU = 0;
    }
    
    // Getters y Setters
    public int getPid() { return pid; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public int getPrioridad() { return prioridad; }
    public List<Rafaga> getRafagas() { return rafagas; }
    public List<Integer> getPaginasRequeridas() { return paginasRequeridas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    // Métricas
    public int getTiempoInicio() { return tiempoInicio; }
    public void setTiempoInicio(int tiempo) { this.tiempoInicio = tiempo; }
    public int getTiempoFin() { return tiempoFin; }
    public void setTiempoFin(int tiempo) { this.tiempoFin = tiempo; }
    
    public void calcularMetricas() {
        if (tiempoFin > 0 && tiempoInicio > 0) {
            tiempoRetorno = tiempoFin - tiempoLlegada;
            tiempoEspera = tiempoRetorno - tiempoEnCPU;
        }
    }
    
    public int getTiempoEspera() { return tiempoEspera; }
    public int getTiempoRetorno() { return tiempoRetorno; }
    public int getTiempoEnCPU() { return tiempoEnCPU; }
    public void agregarTiempoCPU(int tiempo) { tiempoEnCPU += tiempo; }
    
    // Manejo de ráfagas
    public Rafaga getRafagaActual() {
        if (rafagaActual < rafagas.size()) {
            return rafagas.get(rafagaActual);
        }
        return null;
    }
    
    public void avanzarRafaga() {
        rafagaActual++;
    }
    
    public boolean tieneMasRafagas() {
        return rafagaActual < rafagas.size();
    }
    
    public int getTotalRafagasCPU() {
        int total = 0;
        for (Rafaga r : rafagas) {
            if (r.esCPU()) total += r.getDuracion();
        }
        return total;
    }
}