package Comun;

public class ConfiguracionSistema {
    public static int MARCOS_MEMORIA = 4;
    public static String ALGORITMO_PLANIF = "FCFS";
    public static String ALGORITMO_REEMPLAZO = "FIFO";
    public static int QUANTUM = 3;
    public static int TAMANIO_MARCO = 1024;
    
    @Override
    public String toString() {
        return String.format(
            "Config[Marcos=%d, Planif=%s, Reemplazo=%s, Quantum=%d, TamañoMarco=%d]",
            MARCOS_MEMORIA, ALGORITMO_PLANIF, ALGORITMO_REEMPLAZO, QUANTUM, TAMANIO_MARCO
        );
    }
}
