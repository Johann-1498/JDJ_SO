package Procesos;

public class Rafaga {
    private String tipo; // "CPU" o "E/S"
    private int duracion;
    private int tiempoRestante;
    
    public Rafaga(String tipo, int duracion) {
        this.tipo = tipo;
        this.duracion = duracion;
        this.tiempoRestante = duracion;
    }
    
    // Getters y Setters
    public String getTipo() { return tipo; }
    public int getDuracion() { return duracion; }
    public int getTiempoRestante() { return tiempoRestante; }
    public void setTiempoRestante(int tiempo) { this.tiempoRestante = tiempo; }
    
    public boolean esCPU() { return "CPU".equalsIgnoreCase(tipo); }
    public boolean esES() { return "E/S".equalsIgnoreCase(tipo); }
    
    @Override
    public String toString() {
        return tipo + "(" + duracion + ")";
    }
}
