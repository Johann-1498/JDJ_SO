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
                cpu.ejecutarRafaga(tiempoRafaga);
                pcb.actualizarTiempoRestante(tiempoRafaga);
                
                // Verificar si sigue con E/S
                if (pcb.getEstado().equals("BLOQUEADO")) {
                    System.out.println("P" + pcb.getPid() + " entra en E/S por " + 
                                     pcb.getTiempoBloqueoRestante() + " unidades");
                    Thread.sleep(pcb.getTiempoBloqueoRestante() * 100);
                    pcb.actualizarTiempoBloqueo(pcb.getTiempoBloqueoRestante());
                }
            }
            
            if (pcb.getEstado().equals("TERMINADO")) {
                System.out.println("P" + pcb.getPid() + " ha terminado completamente");
            }
            
        } catch (InterruptedException e) {
            System.out.println("Hilo P" + pcb.getPid() + " interrumpido");
        }
    }
}