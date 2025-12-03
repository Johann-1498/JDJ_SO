package Memoria;

import java.util.Arrays;
import java.util.List;

public class TestMemoria {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE MEMORIA FÍSICA ===\n");
        
        // No necesita import porque está en el mismo paquete
        MemoriaFisica memoria = new MemoriaFisica(4, 1024, "FIFO");
        memoria.imprimirEstado();
        
        System.out.println("\n--- Cargando P1 ---");
        List<Integer> paginasP1 = Arrays.asList(1, 2);
        memoria.cargarPaginas(1, paginasP1);
        
        System.out.println("\n--- Cargando P2 ---");
        List<Integer> paginasP2 = Arrays.asList(1, 2, 3);
        memoria.cargarPaginas(2, paginasP2);
        
        System.out.println("\n--- Cargando P3 ---");
        List<Integer> paginasP3 = Arrays.asList(1, 2);
        memoria.cargarPaginas(3, paginasP3);
        
        System.out.println("\n--- Liberando P2 ---");
        memoria.liberarProceso(2);
        
        memoria.imprimirEstadisticas();
    }
}