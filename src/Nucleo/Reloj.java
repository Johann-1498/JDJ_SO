package Nucleo;

public class Reloj {
    private int tiempoActual = 0;
    
    public synchronized void tic() {
        tiempoActual++;
    }
    
    public synchronized void setTiempo(int tiempo) {
        this.tiempoActual = tiempo;
    }
    
    public synchronized void avanzarTiempo(int unidades) {
        this.tiempoActual += unidades;
    }
    
    public synchronized int getTiempo() { 
        return tiempoActual; 
    }
}