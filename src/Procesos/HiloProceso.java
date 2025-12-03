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
            System.out.println(">>> Hilo P" + pcb.getPid() + " inicia ejecución.");
            
            // Simular trabajo usando la CPU (Clase del Estudiante 1)
            cpu.ejecutarRafaga(pcb.getTiempoRafaga());
            
            pcb.setEstado("TERMINADO");
            System.out.println("<<< Hilo P" + pcb.getPid() + " finalizó.");
        } catch (InterruptedException e) {
            System.out.println("Error en Hilo P" + pcb.getPid());
        }
    }
}