package Procesos;

public class Rafaga {
<<<<<<< HEAD
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
=======
    public enum Tipo { CPU, ES } // E/S = Entrada/Salida
    
    private Tipo tipo;
    private int duracion;

    public Rafaga(Tipo tipo, int duracion) {
        this.tipo = tipo;
        this.duracion = duracion;
    }

    public Tipo getTipo() { return tipo; }
    public int getDuracion() { return duracion; }
    public void decrementarDuracion(int cantidad) { this.duracion -= cantidad; }
}
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
