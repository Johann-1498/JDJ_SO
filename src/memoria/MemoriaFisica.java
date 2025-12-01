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

// Intenta cargar las páginas de un proceso
    public synchronized boolean cargarPaginas(int pid, java.util.List<Integer> paginas) {
        System.out.println("[Memoria] Verificando páginas para P" + pid + "...");
        
        for (int pagina : paginas) {
            if (!estaEnMemoria(pid, pagina)) {
                fallosPagina++;
                System.out.println("[Memoria] FALLO DE PÁGINA: P" + pid + " necesita página " + pagina);
                reemplazarPagina(pid, pagina);
            }
        }
        return true; // En simulación simple, asumimos que siempre logramos cargar
    }
    private void reemplazarPagina(int pid, int pagina) {
        // 1. Buscar marco libre
        int marcoLibre = -1;
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == -1) {
                marcoLibre = i;
                break;
            }
        }

        // 2. Si hay libre, usarlo
        if (marcoLibre != -1) {
            marcos[marcoLibre] = pid; // Asignamos marco al proceso
            colaFIFO.add(marcoLibre); // Añadir a cola FIFO
            System.out.println("   -> Cargado en Marco " + marcoLibre);
        } else {
            // 3. Si no, aplicar Algoritmo de Reemplazo (FIFO)
            // AQUI AGREGARÍAS IF(ALGORITMO == LRU) ...
            int marcoVictima = colaFIFO.poll();
            System.out.println("   -> REEMPLAZO (FIFO): Marco " + marcoVictima + " liberado.");
            marcos[marcoVictima] = pid;
            colaFIFO.add(marcoVictima);
        }
    }
    
    public void imprimirEstado() {
        System.out.print("[RAM Estado]: ");
        for (int m : marcos) {
            System.out.print("[" + (m == -1 ? "L" : "P"+m) + "] ");
        }
        System.out.println();
    }
}