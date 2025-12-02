package IO;

import Procesos.PCB;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ProcesosParser {
    
    /**
     * Lee un archivo de procesos y retorna lista de PCB
     * Formato: P1 0 CPU(4),E/S(3),CPU(5) 1 4
     */
    public static List<PCB> parsearArchivo(String rutaArchivo) throws IOException {
        List<PCB> procesos = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
        String linea;
        int pidCounter = 1;
        
        System.out.println("[Parser] Leyendo archivo: " + rutaArchivo);
        
        while ((linea = reader.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty() || linea.startsWith("#")) continue;
            
            try {
                PCB pcb = parsearLinea(linea, pidCounter);
                if (pcb != null) {
                    procesos.add(pcb);
                    pidCounter++;
                }
            } catch (Exception e) {
                System.err.println("[Parser] Error en línea: " + linea + " - " + e.getMessage());
            }
        }
        
        reader.close();
        System.out.println("[Parser] " + procesos.size() + " procesos cargados exitosamente");
        return procesos;
    }
    
    private static PCB parsearLinea(String linea, int pid) {
        // Regex para formato: P1 0 CPU(4),E/S(3),CPU(5) 1 4
        // O simplificado: PID tiempoLlegada ráfagas prioridad páginas
        String[] partes = linea.split("\\s+");
        
        if (partes.length < 4) {
            throw new IllegalArgumentException("Formato inválido. Se esperan al menos 4 campos");
        }
        
        // Extraer tiempo de llegada
        int tiempoLlegada = Integer.parseInt(partes[1]);
        
        // Extraer ráfagas (CPU y E/S)
        List<Rafaga> rafagas = parsearRafagas(partes[2]);
        
        // Extraer prioridad (si existe)
        int prioridad = partes.length > 3 ? Integer.parseInt(partes[3]) : 1;
        
        // Extraer páginas requeridas
        int paginasRequeridas = partes.length > 4 ? Integer.parseInt(partes[4]) : 3;
        List<Integer> paginas = generarPaginas(pid, paginasRequeridas);
        
        System.out.printf("[Parser] Proceso P%d: Llegada=%d, Prioridad=%d, Páginas=%d, Ráfagas=%d%n",
                         pid, tiempoLlegada, prioridad, paginasRequeridas, rafagas.size());
        
        return new PCB(pid, tiempoLlegada, prioridad, rafagas, paginas);
    }
    
    private static List<Rafaga> parsearRafagas(String strRafagas) {
        List<Rafaga> rafagas = new ArrayList<>();
        
        // Formato: CPU(4),E/S(3),CPU(5)
        String[] partes = strRafagas.split(",");
        
        Pattern pattern = Pattern.compile("(CPU|E/S)\\((\\d+)\\)");
        
        for (String parte : partes) {
            Matcher matcher = pattern.matcher(parte.trim());
            if (matcher.matches()) {
                String tipo = matcher.group(1);
                int duracion = Integer.parseInt(matcher.group(2));
                rafagas.add(new Rafaga(tipo, duracion));
            }
        }
        
        return rafagas;
    }
    
    private static List<Integer> generarPaginas(int pid, int cantidad) {
        List<Integer> paginas = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            paginas.add(pid * 100 + i); // Páginas únicas por proceso
        }
        return paginas;
    }
    
    /**
     * Lee configuración del sistema desde archivo
     */
    public static ConfiguracionSistema leerConfiguracion(String rutaConfig) throws IOException {
        ConfiguracionSistema config = new ConfiguracionSistema();
        Properties props = new Properties();
        
        try (InputStream input = new FileInputStream(rutaConfig)) {
            props.load(input);
            
            config.MARCOS_MEMORIA = Integer.parseInt(props.getProperty("marcos_memoria", "4"));
            config.ALGORITMO_PLANIF = props.getProperty("algoritmo_planificacion", "FCFS");
            config.ALGORITMO_REEMPLAZO = props.getProperty("algoritmo_reemplazo", "FIFO");
            config.QUANTUM = Integer.parseInt(props.getProperty("quantum", "3"));
            config.TAMANIO_MARCO = Integer.parseInt(props.getProperty("tamanio_marco", "1024"));
            
            System.out.println("[Parser] Configuración cargada: " + config);
        }
        
        return config;
    }
}