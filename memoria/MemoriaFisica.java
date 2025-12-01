package Memoria;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MemoriaFisica {
    private int[] marcos; // -1 = libre, >0 = PID del proceso dueño
    private int tamanioMarco;
    private Queue<Integer> colaFIFO; // Para el algoritmo FIFO
    private int fallosPagina;

    public MemoriaFisica(int cantidadMarcos, int tamanioMarco) {
        this.marcos = new int[cantidadMarcos];
        Arrays.fill(this.marcos, -1); // Inicializar todo como libre
        this.tamanioMarco = tamanioMarco;
        this.colaFIFO = new LinkedList<>();
        this.fallosPagina = 0;
    }

    

    private boolean estaEnMemoria(int pid, int pagina) {
        // En una simulación real compleja, tendrías una tabla de páginas.
        // Simplificación: Revisamos si hay algún marco asignado (lógica básica)
        // Para este ejemplo simple, asumimos que si hay espacio, cargamos.
        return false; // Forzamos carga para ver el funcionamiento
    }
  
    public void imprimirEstado() {
        System.out.print("[RAM Estado]: ");
        for (int m : marcos) {
            System.out.print("[" + (m == -1 ? "L" : "P"+m) + "] ");
        }
        System.out.println();
    }
}