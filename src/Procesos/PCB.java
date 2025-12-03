package Procesos;

import java.util.List;

public class PCB {
    private int pid;
    private int tiempoLlegada;
    private List<String> rafagas;
    private int tiempoTotal;
    private int tiempoEjecutado;
    private List<Integer> paginasRequeridas;
    private String estado;
    private int prioridad;
    private int indiceRafagaActual;
    private int tiempoBloqueoRestante;
    private int tiempoEjecucionRestanteEnRafaga;
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
        this.tiempoTotal = calcularTiempoTotal();
        this.tiempoEjecutado = 0;
        this.tiempoInicioEjecucion = -1;
        this.tiempoFinEjecucion = -1;
        
        // Inicializar tiempo restante en la primera ráfaga
        if (rafagas.size() > 0) {
            String primeraRafaga = rafagas.get(0);
            if (primeraRafaga.startsWith("CPU")) {
                this.tiempoEjecucionRestanteEnRafaga = obtenerTiempoRafaga(primeraRafaga);
                this.estado = "LISTO";
            }
        }
    }
    
    private int calcularTiempoTotal() {
        int total = 0;
        for (String rafaga : rafagas) {
            total += obtenerTiempoRafaga(rafaga);
        }
        return total;
    }
    
    private int obtenerTiempoRafaga(String rafaga) {
        if (rafaga.contains("(")) {
            String[] partes = rafaga.split("[()]");
            if (partes.length >= 2) {
                try {
                    return Integer.parseInt(partes[1]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public String getRafagaActual() {
        if (indiceRafagaActual < rafagas.size()) {
            return rafagas.get(indiceRafagaActual);
        }
        return "TERMINADO";
    }
    
    public int getTiempoRafagaActual() {
        return obtenerTiempoRafaga(getRafagaActual());
    }
    
    public boolean esRafagaCPU() {
        String rafaga = getRafagaActual();
        return rafaga.startsWith("CPU");
    }
    
    public boolean esRafagaES() {
        String rafaga = getRafagaActual();
        return rafaga.startsWith("E/S");
    }
    
    public void avanzarASiguienteRafaga() {
        indiceRafagaActual++;
        
        if (indiceRafagaActual >= rafagas.size()) {
            estado = "TERMINADO";
            tiempoBloqueoRestante = 0;
            tiempoEjecucionRestanteEnRafaga = 0;
            tiempoFinEjecucion = tiempoInicioEjecucion + tiempoTotal;
            return;
        }
        
        String siguienteRafaga = rafagas.get(indiceRafagaActual);
        if (siguienteRafaga.startsWith("CPU")) {
            estado = "LISTO";
            tiempoEjecucionRestanteEnRafaga = obtenerTiempoRafaga(siguienteRafaga);
        } else if (siguienteRafaga.startsWith("E/S")) {
            estado = "BLOQUEADO";
            tiempoBloqueoRestante = obtenerTiempoRafaga(siguienteRafaga);
        }
    }
    
    public void ejecutarCPU(int tiempo) {
        if (tiempo <= 0 || estado.equals("TERMINADO") || !esRafagaCPU()) {
            return;
        }
        
        tiempoEjecutado += tiempo;
        tiempoEjecucionRestanteEnRafaga -= tiempo;
        
        if (tiempoEjecucionRestanteEnRafaga <= 0) {
            avanzarASiguienteRafaga();
        }
    }
    
    public void avanzarBloqueo(int tiempo) {
        if (tiempo <= 0 || !estado.equals("BLOQUEADO")) {
            return;
        }
        
        tiempoBloqueoRestante -= tiempo;
        
        if (tiempoBloqueoRestante <= 0) {
            avanzarASiguienteRafaga();
        }
    }
    
    // Getters y Setters
    public int getPid() { return pid; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public int getTiempoRafagaTotal() { return tiempoTotal; }
    public List<Integer> getPaginasRequeridas() { return paginasRequeridas; }
    public String getEstado() { return estado; }
    public int getTiempoRestante() { return tiempoTotal - tiempoEjecutado; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoBloqueoRestante() { return tiempoBloqueoRestante; }
    public List<String> getRafagas() { return rafagas; }
    public int getTiempoInicioEjecucion() { return tiempoInicioEjecucion; }
    public int getTiempoFinEjecucion() { return tiempoFinEjecucion; }
    
    public void setTiempoInicioEjecucion(int tiempo) { 
        if (this.tiempoInicioEjecucion == -1) {
            this.tiempoInicioEjecucion = tiempo; 
        }
    }
    
    public void setTiempoFinEjecucion(int tiempo) { 
        this.tiempoFinEjecucion = tiempo; 
    }
    
    public void setEstado(String estado) { 
        this.estado = estado; 
    }
    
    public int getTiempoEjecucionRestanteEnRafaga() {
        return tiempoEjecucionRestanteEnRafaga;
    }
}