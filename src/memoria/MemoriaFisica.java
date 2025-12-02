package Memoria;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

public class MemoriaFisica {
    private int[] marcos; // -1 = libre, >0 = PID del proceso dueño
    private int tamanioMarco;
    private Queue<Integer> colaFIFO; // Para el algoritmo FIFO
    private int fallosPagina;
    private String algoritmoReemplazo; // "FIFO", "LRU", "OPTIMO"

    public MemoriaFisica(int cantidadMarcos, int tamanioMarco) {
        this.marcos = new int[cantidadMarcos];
        Arrays.fill(this.marcos, -1); // Inicializar todo como libre
        this.tamanioMarco = tamanioMarco;
        this.colaFIFO = new LinkedList<>();
        this.fallosPagina = 0;
        this.algoritmoReemplazo = "FIFO"; // Por defecto
    }
    
    // Constructor con algoritmo configurable
    public MemoriaFisica(int cantidadMarcos, int tamanioMarco, String algoritmo) {
        this(cantidadMarcos, tamanioMarco);
        this.algoritmoReemplazo = algoritmo;
    }

    /**
     * Verifica si una página específica de un proceso ya está en memoria
     * En esta simulación simplificada, verificamos si el proceso tiene algún marco asignado
     */
    private boolean estaEnMemoria(int pid, int pagina) {
        // Simplificación: Solo verificamos si el proceso ya tiene marcos asignados
        // En una implementación real, tendríamos una tabla de páginas por proceso
        for (int marco : marcos) {
            if (marco == pid) {
                // El proceso ya tiene al menos una página en memoria
                return true;
            }
        }
        return false; // Para esta simulación, forzamos verificación de espacio
    }

    /**
     * Intenta cargar las páginas de un proceso en memoria
     * @param pid ID del proceso
     * @param paginas Lista de páginas requeridas
     * @return true si se pudieron cargar todas las páginas (o simular)
     */
    public synchronized boolean cargarPaginas(int pid, List<Integer> paginas) {
        System.out.println("[Memoria] Verificando " + paginas.size() + " páginas para P" + pid + "...");
        
        boolean exito = true;
        
        for (int pagina : paginas) {
            if (!estaEnMemoria(pid, pagina)) {
                fallosPagina++;
                System.out.println("[Memoria] FALLO DE PÁGINA: P" + pid + " necesita página " + pagina);
                
                // Buscar marco libre
                int marcoLibre = buscarMarcoLibre();
                
                if (marcoLibre != -1) {
                    // Hay marco libre
                    asignarMarco(pid, marcoLibre, pagina);
                } else {
                    // No hay marcos libres, aplicar reemplazo
                    exito = reemplazarPagina(pid, pagina);
                }
            } else {
                System.out.println("[Memoria] Página " + pagina + " de P" + pid + " ya está en memoria");
            }
        }
        
        imprimirEstado();
        return exito;
    }

    /**
     * Busca un marco de memoria libre (valor -1)
     * @return índice del marco libre, o -1 si no hay ninguno
     */
    private int buscarMarcoLibre() {
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == -1) {
                System.out.println("   -> Marco " + i + " está libre");
                return i;
            }
        }
        System.out.println("   -> No hay marcos libres, necesito reemplazo");
        return -1;
    }

    /**
     * Asigna un marco específico a un proceso
     */
    private void asignarMarco(int pid, int marco, int pagina) {
        marcos[marco] = pid;
        colaFIFO.add(marco); // Añadir a cola FIFO para algoritmo de reemplazo
        System.out.println("   -> P" + pid + " página " + pagina + " cargada en Marco " + marco);
    }

    /**
     * Aplica algoritmo de reemplazo cuando no hay marcos libres
     */
    private boolean reemplazarPagina(int pid, int pagina) {
        System.out.println("   -> Aplicando algoritmo de reemplazo: " + algoritmoReemplazo);
        
        int marcoVictima = -1;
        int procesoVictima = -1;
        
        if (algoritmoReemplazo.equals("FIFO")) {
            // Algoritmo FIFO: el primero que entró es el primero que sale
            marcoVictima = colaFIFO.poll(); // Obtiene y remueve el primero
            procesoVictima = marcos[marcoVictima];
            
            System.out.println("   -> REEMPLAZO (FIFO): Marco " + marcoVictima + 
                             " liberado (era de P" + procesoVictima + ")");
            
            // Reasignar el marco al nuevo proceso
            marcos[marcoVictima] = pid;
            colaFIFO.add(marcoVictima); // Vuelve a entrar a la cola (pero ahora con nuevo PID)
            
            System.out.println("   -> P" + pid + " página " + pagina + 
                             " ahora ocupa Marco " + marcoVictima);
            
            return true;
        } else if (algoritmoReemplazo.equals("LRU")) {
            // Para LRU necesitarías estructuras adicionales
            System.out.println("   -> LRU no implementado aún, usando FIFO como fallback");
            return reemplazarPagina(pid, pagina); // Llama recursivamente con FIFO
        } else if (algoritmoReemplazo.equals("OPTIMO")) {
            System.out.println("   -> OPTIMO no implementado aún, usando FIFO como fallback");
            return reemplazarPagina(pid, pagina);
        }
        
        return false;
    }

    /**
     * Libera todos los marcos asignados a un proceso
     */
    public synchronized void liberarProceso(int pid) {
        System.out.println("[Memoria] Liberando páginas de P" + pid + "...");
        int liberados = 0;
        
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == pid) {
                marcos[i] = -1; // Liberar marco
                
                // También debemos remover de la cola FIFO si está ahí
                colaFIFO.remove(Integer.valueOf(i));
                
                System.out.println("   -> Marco " + i + " liberado");
                liberados++;
            }
        }
        
        if (liberados > 0) {
            System.out.println("   -> Total: " + liberados + " marcos liberados de P" + pid);
        } else {
            System.out.println("   -> P" + pid + " no tenía páginas en memoria");
        }
        
        imprimirEstado();
    }

    /**
     * Imprime el estado actual de la memoria
     */
    public void imprimirEstado() {
        System.out.print("[RAM Estado]: ");
        for (int i = 0; i < marcos.length; i++) {
            String contenido;
            if (marcos[i] == -1) {
                contenido = "L"; // Libre
            } else {
                contenido = "P" + marcos[i];
            }
            System.out.print("[" + i + ":" + contenido + "] ");
        }
        System.out.println("(Fallos: " + fallosPagina + ")");
    }

    /**
     * Obtiene estadísticas de memoria
     */
    public void imprimirEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS DE MEMORIA ===");
        System.out.println("Total de marcos: " + marcos.length);
        System.out.println("Tamaño por marco: " + tamanioMarco + " KB");
        System.out.println("Fallos de página: " + fallosPagina);
        System.out.println("Algoritmo de reemplazo: " + algoritmoReemplazo);
        
        int marcosOcupados = 0;
        for (int marco : marcos) {
            if (marco != -1) marcosOcupados++;
        }
        System.out.println("Marcos ocupados: " + marcosOcupados + "/" + marcos.length);
        System.out.println("Porcentaje de uso: " + 
                          String.format("%.1f", (marcosOcupados * 100.0 / marcos.length)) + "%");
    }

    // Getters y Setters
    public int getFallosPagina() { return fallosPagina; }
    public void setAlgoritmoReemplazo(String algoritmo) { this.algoritmoReemplazo = algoritmo; }
    public String getAlgoritmoReemplazo() { return algoritmoReemplazo; }
}