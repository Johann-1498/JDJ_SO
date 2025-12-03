package Procesos;

import Nucleo.CPU; // Se mantiene por compatibilidad, aunque Planificador gestiona el tiempo

public class HiloProceso {
    private PCB pcb;
    
    // Eliminamos el campo 'cpu' si no se usa para limpiar el Warning
    // O lo dejamos si quieres mantener la estructura, pero lo quitamos del constructor si molesta
    @SuppressWarnings("unused") 
    private CPU cpu; 
    
    public HiloProceso(PCB pcb, CPU cpu) {
        this.pcb = pcb;
        this.cpu = cpu;
    }
    
    public PCB getPcb() {
        return pcb;
    }
<<<<<<< HEAD
=======
    
    @Override
    public void run() {
        // En esta arquitectura avanzada, el PLANIFICADOR gestiona la ejecución
        // paso a paso dentro de su bucle while.
        // Este método run() se deja simple o vacío porque no usamos start() tradicional
        // sino que simulamos el flujo en el hilo principal del Planificador.
        
        // Si quisieras usar hilos reales concurrentes, aquí iría la lógica,
        // pero eso hace muy difícil implementar Round Robin y E/S simulada correctamente.
    }
>>>>>>> 7271c7e410f4fd1dd9c0e4ceeac1f02b69cab14c
}