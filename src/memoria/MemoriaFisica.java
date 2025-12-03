package Memoria;

import java.util.Arrays;
import java.util.List;

public class MemoriaFisica {
    private int[] marcos;
    private int tamanioMarco;
    private AlgoritmosReemplazo.EstrategiaReemplazo estrategia;
    private int fallosPagina;
    private int reemplazosRealizados;
    private String algoritmoActual;
    private int[] paginaEnMarco;
    private int[] pidEnMarco;
    
    public MemoriaFisica(int cantidadMarcos, int tamanioMarco) {
        this(cantidadMarcos, tamanioMarco, "FIFO");
    }
    
    public MemoriaFisica(int cantidadMarcos, int tamanioMarco, String algoritmo) {
        this.marcos = new int[cantidadMarcos];
        this.paginaEnMarco = new int[cantidadMarcos];
        this.pidEnMarco = new int[cantidadMarcos];
        Arrays.fill(this.marcos, -1);
        Arrays.fill(this.paginaEnMarco, -1);
        Arrays.fill(this.pidEnMarco, -1);
        
        this.tamanioMarco = tamanioMarco;
        this.fallosPagina = 0;
        this.reemplazosRealizados = 0;
        this.algoritmoActual = algoritmo.toUpperCase();
        
        this.estrategia = AlgoritmosReemplazo.crearEstrategia(algoritmo, cantidadMarcos);
        
        System.out.println("[Memoria] Inicializada con " + cantidadMarcos + 
                          " marcos (" + (cantidadMarcos * tamanioMarco) + "KB total), algoritmo: " + this.algoritmoActual);
    }
    
    private boolean estaEnMemoria(int pid, int pagina) {
        for (int i = 0; i < marcos.length; i++) {
            if (pidEnMarco[i] == pid && paginaEnMarco[i] == pagina) {
                estrategia.notificarAcceso(i);
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean cargarPaginas(int pid, List<Integer> paginas) {
        System.out.println("\n[Memoria] P" + pid + " solicita " + paginas.size() + " páginas");
        
        boolean todasCargadas = true;
        
        for (int pagina : paginas) {
            if (!estaEnMemoria(pid, pagina)) {
                fallosPagina++;
                System.out.println("  ✗ Fallo página: P" + pid + " página " + pagina);
                
                int marcoLibre = buscarMarcoLibre();
                
                if (marcoLibre != -1) {
                    cargarEnMarco(pid, pagina, marcoLibre);
                } else {
                    aplicarReemplazo(pid, pagina);
                }
            } else {
                System.out.println("  ✓ Página " + pagina + " ya en memoria");
            }
        }
        
        imprimirEstado();
        return todasCargadas;
    }
    
    private int buscarMarcoLibre() {
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] == -1) {
                return i;
            }
        }
        return -1;
    }
    
    private void cargarEnMarco(int pid, int pagina, int marco) {
        marcos[marco] = marco;
        paginaEnMarco[marco] = pagina;
        pidEnMarco[marco] = pid;
        estrategia.notificarCarga(marco, pid);
        System.out.println("    → Cargada en Marco " + marco);
    }
    
    private void aplicarReemplazo(int pid, int pagina) {
        System.out.println("    → Memoria llena. Aplicando " + algoritmoActual + "...");
        
        // CORRECIÓN: pasar un array vacío en lugar de null
        Object[] infoExtra = new Object[0];
        int marcoVictima = estrategia.seleccionarVictima(marcos, pid, pagina, infoExtra);
        
        if (marcoVictima == -1 || marcoVictima >= marcos.length) {
            marcoVictima = 0;
        }
        
        int procesoVictima = pidEnMarco[marcoVictima];
        int paginaVictima = paginaEnMarco[marcoVictima];
        
        System.out.println("    → Victima: Marco " + marcoVictima + 
                         " (P" + procesoVictima + " página " + paginaVictima + ")");
        
        reemplazosRealizados++;
        cargarEnMarco(pid, pagina, marcoVictima);
    }
    
    public synchronized void liberarProceso(int pid) {
        System.out.println("\n[Memoria] Liberando P" + pid + "...");
        int liberados = 0;
        
        for (int i = 0; i < marcos.length; i++) {
            if (pidEnMarco[i] == pid) {
                marcos[i] = -1;
                paginaEnMarco[i] = -1;
                pidEnMarco[i] = -1;
                liberados++;
            }
        }
        
        System.out.println("  → " + liberados + " marcos liberados");
    }
    
    public void imprimirEstado() {
        System.out.print("[RAM]: ");
        for (int i = 0; i < marcos.length; i++) {
            String contenido;
            if (marcos[i] == -1) {
                contenido = "L";
            } else {
                contenido = "P" + pidEnMarco[i] + ":" + paginaEnMarco[i];
            }
            System.out.print("M" + i + "=" + contenido + " ");
        }
        System.out.println("(Fallos: " + fallosPagina + ", Reemplazos: " + reemplazosRealizados + ")");
    }
    
    public void imprimirEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS DE MEMORIA ===");
        System.out.println("Total de marcos: " + marcos.length);
        System.out.println("Tamaño por marco: " + tamanioMarco + " KB");
        System.out.println("Capacidad total: " + (marcos.length * tamanioMarco) + " KB");
        System.out.println("Fallos de página: " + fallosPagina);
        System.out.println("Reemplazos realizados: " + reemplazosRealizados);
        System.out.println("Algoritmo: " + algoritmoActual);
        
        int marcosOcupados = 0;
        for (int marco : marcos) {
            if (marco != -1) marcosOcupados++;
        }
        
        double usoPorcentaje = (marcosOcupados * 100.0) / marcos.length;
        System.out.println("Marcos ocupados: " + marcosOcupados + "/" + marcos.length);
        System.out.printf("Uso de memoria: %.1f%%\n", usoPorcentaje);
    }
    
    public void cambiarAlgoritmo(String nuevoAlgoritmo) {
        System.out.println("\n[Memoria] Cambiando algoritmo a " + nuevoAlgoritmo.toUpperCase());
        this.algoritmoActual = nuevoAlgoritmo.toUpperCase();
        this.estrategia = AlgoritmosReemplazo.crearEstrategia(nuevoAlgoritmo, marcos.length);
    }
    
    public synchronized boolean hayMemoriaDisponible(int paginasRequeridas) {
        int marcosLibres = 0;
        for (int marco : marcos) {
            if (marco == -1) marcosLibres++;
        }
        return marcosLibres >= paginasRequeridas;
    }
    
    public int getFallosPagina() { return fallosPagina; }
    public int getReemplazosRealizados() { return reemplazosRealizados; }
    public String getAlgoritmoActual() { return algoritmoActual; }
}