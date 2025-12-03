package Procesos;

import Nucleo.CPU;

public class HiloProceso {
    private PCB pcb;
    private CPU cpu;
    
    public HiloProceso(PCB pcb, CPU cpu) {
        this.pcb = pcb;
        this.cpu = cpu;
    }
    
    public PCB getPcb() {
        return pcb;
    }
}