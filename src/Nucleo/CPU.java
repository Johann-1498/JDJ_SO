package Nucleo;

public class CPU {
    public void ejecutarRafaga(int tiempo) throws InterruptedException {
        // Esta función ahora está en HiloProceso para mejor control del tiempo
        // Se mantiene por compatibilidad
        for (int i = 0; i < tiempo; i++) {
            Thread.sleep(100);
            System.out.print(".");
        }
        System.out.println(" Hecho.");
    }
}