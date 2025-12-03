package Procesos;

public class Rafaga {
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