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
        // En la Ronda 2 llenaremos esto con la lógica real
        System.out.println("Proceso " + pcb.getPid() + " listo para iniciar.");
    }
}