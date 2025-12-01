import Nucleo.CPU;
import Nucleo.Reloj;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SIMULADOR DE SO ===");
        
        // Inicializamos componentes base (Hardware)
        Reloj reloj = new Reloj();
        CPU cpu = new CPU();
        
        System.out.println("Hardware detectado: CPU y Reloj listos.");
        System.out.println("Esperando módulos de Memoria y Planificación...");
    }
}