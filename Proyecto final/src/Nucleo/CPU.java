package Nucleo;

public class CPU {
    public void ejecutarRafaga(int tiempo) throws InterruptedException {
        // Simula que la CPU trabaja (100ms reales = 1 unidad de tiempo simulado)
        for (int i = 0; i < tiempo; i++) {
            Thread.sleep(100); 
            System.out.print("."); // Indicador visual de trabajo
        }
        System.out.println(" Hecho.");
    }
}