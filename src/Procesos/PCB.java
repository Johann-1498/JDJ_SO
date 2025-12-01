package Procesos;

import java.util.List;

public class PCB {
    private int pid;
    private int tiempoLlegada;
    private int tiempoRafaga; 
    private int tiempoRestante; // Útil para Round Robin luego
    private List<Integer> paginasRequeridas;
    private String estado; // "NUEVO", "LISTO", "EJECUTANDO", "BLOQUEADO", "TERMINADO"

    public PCB(int pid, int tiempoLlegada, int tiempoRafaga, List<Integer> paginas) {
        this.pid = pid;
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoRafaga = tiempoRafaga;
        this.tiempoRestante = tiempoRafaga;
        this.paginasRequeridas = paginas;
        this.estado = "NUEVO";
    }

    // Getters y Setters necesarios
    public int getPid() { return pid; }
    public int getTiempoRafaga() { return tiempoRafaga; }
    public List<Integer> getPaginasRequeridas() { return paginasRequeridas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}