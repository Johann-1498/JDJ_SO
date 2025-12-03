package Procesos;

import Nucleo.CPU;

public class HiloProceso extends Thread {
    private PCB pcb;
    private CPU cpu;
    
    public HiloProceso(PCB pcb, CPU cpu) {
        this.pcb = pcb;
        this.cpu = cpu;
    }
    
    public PCB getPcb() {
        return pcb;
    }
    
    @Override
    public void run() {
        try {
            pcb.setEstado("EJECUTANDO");
            
            // Ejecutar ráfaga CPU actual
            if (pcb.esRafagaCPU()) {
                int tiempoRafaga = pcb.getTiempoRafagaActual();
                
                // Simular tiempo de CPU
                for (int i = 0; i < tiempoRafaga; i++) {
                    Thread.sleep(100); // 100ms = 1 unidad de tiempo
                    System.out.print(".");
                }
                System.out.println(" Hecho.");
                
                pcb.actualizarTiempoRestante(tiempoRafaga);
                
                // Si la siguiente ráfaga es E/S, se bloqueará automáticamente
                // El planificador manejará el estado BLOQUEADO
            }
            
        } catch (InterruptedException e) {
            System.out.println("Hilo P" + pcb.getPid() + " interrumpido");
        }
    }
}