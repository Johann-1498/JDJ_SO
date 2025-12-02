package Nucleo;

public class Reloj {
    private int tiempoActual = 0;
    
    public void tic() {
        tiempoActual++;
    }
    
    public int getTiempo() { return tiempoActual; }
}