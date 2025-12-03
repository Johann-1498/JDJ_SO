package Memoria;

// Importación explícita de MemoriaFisica
public class MMU {
    private MemoriaFisica memoria;
    private int pidActual;
    
    public MMU(MemoriaFisica memoria) {
        this.memoria = memoria;
    }
    
    public void setPidActual(int pid) {
        this.pidActual = pid;
    }
    
    public boolean validarAcceso(int direccionLogica) {
        System.out.println("[MMU] Proceso P" + pidActual + 
                          " accediendo a dirección lógica: " + direccionLogica);
        
        // Simular traducción a dirección física
        int marco = direccionLogica / 1024;
        int desplazamiento = direccionLogica % 1024;
        
        System.out.println("[MMU] Traducción: Página " + marco + 
                          ", Desplazamiento: " + desplazamiento);
        
        return true;
    }
    
    public void reportarFalloPagina(int pagina) {
        System.out.println("[MMU] FALLO DE PÁGINA detectado para P" + 
                          pidActual + ", página: " + pagina);
    }
}