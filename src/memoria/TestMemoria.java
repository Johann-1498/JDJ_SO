package Memoria;

import java.util.Arrays;
import java.util.List;

public class TestMemoria {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE MEMORIA FÍSICA ===\n");
        
        // Crear memoria con 4 marcos
        MemoriaFisica memoria = new MemoriaFisica(4, 1024, "FIFO");
        memoria.imprimirEstado();
        
        // Proceso 1 necesita 2 páginas
        System.out.println("\n--- Cargando P1 ---");
        List<Integer> paginasP1 = Arrays.asList(1, 2);
        memoria.cargarPaginas(1, paginasP1);
        
        // Proceso 2 necesita 3 páginas
        System.out.println("\n--- Cargando P2 ---");
        List<Integer> paginasP2 = Arrays.asList(1, 2, 3);
        memoria.cargarPaginas(2, paginasP2);
        
        // Proceso 3 necesita 2 páginas (debería causar reemplazo)
        System.out.println("\n--- Cargando P3 ---");
        List<Integer> paginasP3 = Arrays.asList(1, 2);
        memoria.cargarPaginas(3, paginasP3);
        
        // Liberar proceso 2
        System.out.println("\n--- Liberando P2 ---");
        memoria.liberarProceso(2);
        
        // Mostrar estadísticas finales
        memoria.imprimirEstadisticas();
    }
}