package Procesos;

import java.util.List;

public class PCB {
    private int pid;
    private int tiempoLlegada;
    private List<String> rafagas;
    private int tiempoRestante;
    private List<Integer> paginasRequeridas;
    private String estado;
    private int prioridad;
    private int indiceRafagaActual;
    private int tiempoBloqueoRestante;
    private int tiempoInicioEjecucion;
    private int tiempoFinEjecucion;
    
    public PCB(int pid, int tiempoLlegada, List<String> rafagas, int prioridad, List<Integer> paginas) {
        this.pid = pid;
        this.tiempoLlegada = tiempoLlegada;
        this.rafagas = rafagas;
        this.prioridad = prioridad;
        this.paginasRequeridas = paginas;
        this.estado = "NUEVO";
        this.indiceRafagaActual = 0;
        this.tiempoBloqueoRestante = 0;
        this.tiempoRestante = calcularTiempoTotal();
        this.tiempoInicioEjecucion = -1;
        this.tiempoFinEjecucion = -1;
    }
    
    private int calcularTiempoTotal() {
        int total = 0;
        for (String rafaga : rafagas) {
            String[] partes = rafaga.split("[()]");
            if (partes.length >= 2) {
                total += Integer.parseInt(partes[1]);
            }
        }
        return total;
    }
    
    public String getRafagaActual() {
        if (indiceRafagaActual < rafagas.size()) {
            return rafagas.get(indiceRafagaActual);
        }
        return "TERMINADO";
    }
    
    public int getTiempoRafagaActual() {
        String rafaga = getRafagaActual();
        if (rafaga.contains("(")) {
            String[] partes = rafaga.split("[()]");
            return Integer.parseInt(partes[1]);
        }
        return 0;
    }
    
    public boolean esRafagaCPU() {
        String rafaga = getRafagaActual();
        return rafaga.startsWith("CPU");
    }
    
    public boolean esRafagaES() {
        String rafaga = getRafagaActual();
        return rafaga.startsWith("E/S");
    }
    
    public void avanzarRafaga() {
        indiceRafagaActual++;
        if (indiceRafagaActual < rafagas.size()) {
            String siguiente = rafagas.get(indiceRafagaActual);
            if (siguiente.startsWith("E/S")) {
                estado = "BLOQUEADO";
                String[] partes = siguiente.split("[()]");
                tiempoBloqueoRestante = Integer.parseInt(partes[1]);
            } else if (siguiente.startsWith("CPU")) {
                estado = "LISTO";
            }
        } else {
            estado = "TERMINADO";
        }
    }
    
    public void actualizarTiempoBloqueo(int tiempo) {
        if (tiempoBloqueoRestante > 0) {
            tiempoBloqueoRestante -= tiempo;
            if (tiempoBloqueoRestante <= 0) {
                estado = "LISTO";
                avanzarRafaga(); // Avanzar a la siguiente ráfaga
            }
        }
    }
    
    // Getters y Setters
    public int getPid() { return pid; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public int getTiempoRafagaTotal() { return calcularTiempoTotal(); }
    public List<Integer> getPaginasRequeridas() { return paginasRequeridas; }
    public String getEstado() { return estado; }
    public int getTiempoRestante() { return tiempoRestante; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoBloqueoRestante() { return tiempoBloqueoRestante; }
    public List<String> getRafagas() { return rafagas; }
    public int getTiempoInicioEjecucion() { return tiempoInicioEjecucion; }
    public int getTiempoFinEjecucion() { return tiempoFinEjecucion; }
    public void setTiempoInicioEjecucion(int tiempo) { this.tiempoInicioEjecucion = tiempo; }
    public void setTiempoFinEjecucion(int tiempo) { this.tiempoFinEjecucion = tiempo; }
    
    public void actualizarTiempoRestante(int tiempoEjecutado) {
        this.tiempoRestante -= tiempoEjecutado;
        if (esRafagaCPU()) {
            int tiempoRafaga = getTiempoRafagaActual();
            if (tiempoEjecutado >= tiempoRafaga) {
                avanzarRafaga();
            }
        }
    }
    
    public void setEstado(String estado) { this.estado = estado; }
}