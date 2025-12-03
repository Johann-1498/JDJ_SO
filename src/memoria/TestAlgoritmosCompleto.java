package memoria;

import java.util.Arrays;
import java.util.List;

public class TestAlgoritmosCompleto {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA COMPLETA DE ALGORITMOS DE REEMPLAZO ===\n");
        
        // Secuencia de referencias para pruebas
        List<Integer> secuenciaReferencias = Arrays.asList(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5);
        
        // Prueba 1: FIFO
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("PRUEBA 1: ALGORITMO FIFO");
        System.out.println("═══════════════════════════════════════════");
        probarAlgoritmo("FIFO");
        
        // Prueba 2: LRU
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("PRUEBA 2: ALGORITMO LRU");
        System.out.println("═══════════════════════════════════════════");
        probarAlgoritmo("LRU");
        
        // Prueba 3: ÓPTIMO
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("PRUEBA 3: ALGORITMO ÓPTIMO");
        System.out.println("═══════════════════════════════════════════");
        probarAlgoritmo("OPTIMO");
        
        // Prueba 4: Comparativa de rendimiento
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("COMPARATIVA DE RENDIMIENTO");
        System.out.println("═══════════════════════════════════════════");
        compararAlgoritmos();
    }
    
    private static void probarAlgoritmo(String algoritmo) {
        // Crear memoria con 3 marcos (típico para pruebas)
        MemoriaFisica memoria = new MemoriaFisica(3, 1024, algoritmo);
        
        // Simular secuencia de accesos de diferentes procesos
        System.out.println("\n--- Proceso 1 solicita páginas 1, 2, 3 ---");
        memoria.cargarPaginas(1, Arrays.asList(1, 2, 3));
        
        System.out.println("\n--- Proceso 2 solicita páginas 4, 1 ---");
        memoria.cargarPaginas(2, Arrays.asList(4, 1));
        
        System.out.println("\n--- Proceso 3 solicita página 5 ---");
        memoria.cargarPaginas(3, Arrays.asList(5));
        
        System.out.println("\n--- Proceso 1 solicita página 2 de nuevo ---");
        memoria.cargarPaginas(1, Arrays.asList(2));
        
        System.out.println("\n--- Proceso 2 solicita página 4 de nuevo ---");
        memoria.cargarPaginas(2, Arrays.asList(4));
        
        System.out.println("\n--- Proceso 3 solicita página 5 de nuevo ---");
        memoria.cargarPaginas(3, Arrays.asList(5));
        
        // Estadísticas
        memoria.imprimirEstadisticas();
    }
    
    private static void compararAlgoritmos() {
        String[] algoritmos = {"FIFO", "LRU", "OPTIMO"};
        int[] fallos = new int[3];
        int[] reemplazos = new int[3];
        
        // Secuencia clásica de Belady
        List<List<Integer>> secuenciaProcesos = Arrays.asList(
            Arrays.asList(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5),
            Arrays.asList(7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1),
            Arrays.asList(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
        );
        
        for (int i = 0; i < algoritmos.length; i++) {
            MemoriaFisica memoria = new MemoriaFisica(3, 1024, algoritmos[i]);
            
            // Simular secuencia como si fuera un solo proceso
            for (List<Integer> secuencia : secuenciaProcesos) {
                for (int pagina : secuencia) {
                    // Simular acceso individual
                    if (!simularAcceso(memoria, 1, pagina)) {
                        fallos[i]++;
                    }
                }
            }
            
            reemplazos[i] = memoria.getReemplazosRealizados();
        }
        
        // Mostrar resultados comparativos
        System.out.println("\nRESULTADOS COMPARATIVOS (3 marcos):");
        System.out.println("+-----------------+--------+-------------+");
        System.out.println("| Algoritmo       | Fallos | Reemplazos  |");
        System.out.println("+-----------------+--------+-------------+");
        for (int i = 0; i < algoritmos.length; i++) {
            System.out.printf("| %-15s | %6d | %11d |\n", 
                algoritmos[i], fallos[i], reemplazos[i]);
        }
        System.out.println("+-----------------+--------+-------------+");
        
        // Análisis de resultados
        System.out.println("\nANÁLISIS:");
        System.out.println("1. FIFO: Simple pero puede sufrir anomalía de Belady");
        System.out.println("2. LRU: Mejor que FIFO, aproxima al óptimo");
        System.out.println("3. ÓPTIMO: Ideal pero requiere conocimiento futuro");
        System.out.println("   (En esta simulación es una aproximación)");
    }
    
    private static boolean simularAcceso(MemoriaFisica memoria, int pid, int pagina) {
        // Método auxiliar para simular accesos individuales
        // En una implementación real, esto sería más complejo
        try {
            memoria.cargarPaginas(pid, Arrays.asList(pagina));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}