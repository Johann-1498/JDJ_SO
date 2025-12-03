package memoria;

public class MMU {
    // Clase auxiliar para traducir direcciones lógicas a físicas
    // y validar accesos a memoria
    
    private MemoriaFisica memoria;
    private int pidActual;
    
    public MMU(MemoriaFisica memoria) {
        this.memoria = memoria;
    }
    
    public void setPidActual(int pid) {
        this.pidActual = pid;
    }
    
    /**
     * Valida si un proceso puede acceder a una dirección de memoria
     */
    public boolean validarAcceso(int direccionLogica) {
        // En una implementación real:
        // 1. Extraer número de página de la dirección lógica
        // 2. Verificar si la página está en la tabla de páginas del proceso
        // 3. Verificar permisos de lectura/escritura
        
        // Para esta simulación simplificada:
        // - Asumimos que si el proceso tiene páginas cargadas, puede acceder
        // - La validación principal la hace el sistema de memoria virtual
        
        System.out.println("[MMU] Proceso P" + pidActual + 
                          " accediendo a dirección lógica: " + direccionLogica);
        
        // Simular traducción a dirección física
        int marco = direccionLogica / 1024; // Suponiendo páginas de 1KB
        int desplazamiento = direccionLogica % 1024;
        
        System.out.println("[MMU] Traducción: Página " + marco + 
                          ", Desplazamiento: " + desplazamiento);
        
        return true; // En simulación, siempre permitido
    }
    
    /**
     * Reporta un fallo de página al sistema
     */
    public void reportarFalloPagina(int pagina) {
        System.out.println("[MMU] FALLO DE PÁGINA detectado para P" + 
                          pidActual + ", página: " + pagina);
        // En un sistema real, esto activaría el manejador de fallos de página
    }
}