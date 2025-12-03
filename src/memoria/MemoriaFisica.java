package memoria;

import java.util.Arrays;
import java.util.List;

public class MemoriaFisica {
    private int[] marcos; // -1 = libre, >0 = PID del proceso dueño
    private int tamanioMarco;
    private AlgoritmosReemplazo.EstrategiaReemplazo estrategia;
    private int fallosPagina;
    private int reemplazosRealizados;
    private String algoritmoActual;
    
    // Para mapeo marco->página (necesario para algunos algoritmos)
    private int[] paginaEnMarco; // Qué página está en cada marco
    
    public MemoriaFisica(int cantidadMarcos, int tamanioMarco) {
        this(cantidadMarcos, tamanioMarco, "FIFO");
    }
    
    public MemoriaFisica(int cantidadMarcos, int tamanioMarco, String algoritmo) {
        this.marcos = new int[cantidadMarcos];
        this.paginaEnMarco = new int[cantidadMarcos];
        Arrays.fill(this.marcos, -1);
        Arrays.fill(this.paginaEnMarco, -1);
        
        this.tamanioMarco = tamanioMarco;
        this.fallosPagina = 0;
        this.reemplazosRealizados = 0;
        this.algoritmoActual = algoritmo.toUpperCase();
        
        // Crear estrategia de reemplazo
        this.estrategia = AlgoritmosReemplazo.crearEstrategia(algoritmo, cantidadMarcos);
        
        System.out.println("[Memoria] Inicializada con " + cantidadMarcos + 
                          " marcos, algoritmo: " + this.algoritmoActual);
    }
    
    /**
     * Verifica si una página específica de un proceso ya está en memoria
     */
    private boolean estaEnMemoria(int pid, int pagina) {
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == pid && paginaEnMarco[i] == pagina) {
                // Notificar acceso para algoritmos como LRU
                estrategia.notificarAcceso(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Intenta cargar las páginas de un proceso en memoria
     */
    public synchronized boolean cargarPaginas(int pid, List<Integer> paginas) {
        System.out.println("\n[Memoria] Cargando " + paginas.size() + " páginas para P" + pid + 
                          " (Algoritmo: " + algoritmoActual + ")");
        
        for (int pagina : paginas) {
            if (!estaEnMemoria(pid, pagina)) {
                fallosPagina++;
                System.out.println("[Memoria] FALLO DE PÁGINA: P" + pid + " necesita página " + pagina);
                
                // Buscar marco libre
                int marcoLibre = buscarMarcoLibre();
                
                if (marcoLibre != -1) {
                    // Hay marco libre
                    cargarEnMarco(pid, pagina, marcoLibre);
                } else {
                    // No hay marcos libres, aplicar reemplazo
                    aplicarReemplazo(pid, pagina);
                }
            }
        }
        
        imprimirEstado();
        return true;
    }
    
    /**
     * Busca un marco de memoria libre
     */
    private int buscarMarcoLibre() {
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == -1) {
                System.out.println("   -> Marco " + i + " está libre");
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Carga una página en un marco específico
     */
    private void cargarEnMarco(int pid, int pagina, int marco) {
        marcos[marco] = pid;
        paginaEnMarco[marco] = pagina;
        estrategia.notificarCarga(marco, pid);
        System.out.println("   -> P" + pid + " página " + pagina + " cargada en Marco " + marco);
    }
    
    /**
     * Aplica algoritmo de reemplazo
     */
    private void aplicarReemplazo(int pid, int pagina) {
        System.out.println("   -> Memoria llena. Aplicando " + algoritmoActual + "...");
        
        // Seleccionar víctima según el algoritmo
        int marcoVictima = estrategia.seleccionarVictima(marcos, pid, pagina, 0);
        
        if (marcoVictima == -1) {
            // Fallback: usar el primer marco ocupado
            for (int i = 0; i < marcos.length; i++) {
                if (marcos[i] != -1) {
                    marcoVictima = i;
                    break;
                }
            }
        }
        
        int procesoVictima = marcos[marcoVictima];
        int paginaVictima = paginaEnMarco[marcoVictima];
        
        System.out.println("   -> REEMPLAZO: Marco " + marcoVictima + 
                         " liberado (P" + procesoVictima + " página " + paginaVictima + ")");
        
        // Reemplazar
        reemplazosRealizados++;
        cargarEnMarco(pid, pagina, marcoVictima);
    }
    
    /**
     * Libera todos los marcos asignados a un proceso
     */
    public synchronized void liberarProceso(int pid) {
        System.out.println("\n[Memoria] Liberando páginas de P" + pid + "...");
        int liberados = 0;
        
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == pid) {
                marcos[i] = -1;
                paginaEnMarco[i] = -1;
                liberados++;
            }
        }
        
        System.out.println("   -> " + liberados + " marcos liberados de P" + pid);
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
                contenido = "P" + marcos[i] + "(" + paginaEnMarco[i] + ")";
            }
            System.out.print("[" + i + ":" + contenido + "] ");
        }
        System.out.println("(Fallos: " + fallosPagina + ", Reemplazos: " + reemplazosRealizados + ")");
        
        // Si es LRU, mostrar estado adicional
        if (estrategia instanceof AlgoritmosReemplazo.LRU) {
            ((AlgoritmosReemplazo.LRU)estrategia).imprimirEstadoLRU();
        }
    }
    
    /**
     * Obtiene estadísticas de memoria
     */
    public void imprimirEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS DE MEMORIA ===");
        System.out.println("Total de marcos: " + marcos.length);
        System.out.println("Tamaño por marco: " + tamanioMarco + " KB");
        System.out.println("Fallos de página: " + fallosPagina);
        System.out.println("Reemplazos realizados: " + reemplazosRealizados);
        System.out.println("Algoritmo de reemplazo: " + algoritmoActual);
        
        int marcosOcupados = 0;
        for (int marco : marcos) {
            if (marco != -1) marcosOcupados++;
        }
        
        double usoPorcentaje = (marcosOcupados * 100.0) / marcos.length;
        System.out.println("Marcos ocupados: " + marcosOcupados + "/" + marcos.length);
        System.out.printf("Porcentaje de uso: %.1f%%\n", usoPorcentaje);
        System.out.printf("Tasa de fallos: %.2f%%\n", 
            (fallosPagina > 0 ? (reemplazosRealizados * 100.0 / fallosPagina) : 0));
    }
    
    /**
     * Cambia el algoritmo de reemplazo en tiempo de ejecución
     */
    public void cambiarAlgoritmo(String nuevoAlgoritmo) {
        System.out.println("[Memoria] Cambiando algoritmo de " + algoritmoActual + 
                         " a " + nuevoAlgoritmo.toUpperCase());
        this.algoritmoActual = nuevoAlgoritmo.toUpperCase();
        this.estrategia = AlgoritmosReemplazo.crearEstrategia(nuevoAlgoritmo, marcos.length);
    }
    
    /**
     * Verifica si hay suficiente memoria disponible
     */
    public synchronized boolean hayMemoriaDisponible(int paginasRequeridas) {
        int marcosLibres = 0;
        for (int marco : marcos) {
            if (marco == -1) marcosLibres++;
        }
        return marcosLibres >= paginasRequeridas;
    }
    
    // Getters
    public int getFallosPagina() { return fallosPagina; }
    public int getReemplazosRealizados() { return reemplazosRealizados; }
    public String getAlgoritmoActual() { return algoritmoActual; }
}