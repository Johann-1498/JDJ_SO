package memoria;

import java.util.*;

public class AlgoritmosReemplazo {
    
    // ============================================
    // INTERFAZ PARA ESTRATEGIAS DE REEMPLAZO
    // ============================================
    public interface EstrategiaReemplazo {
        /**
         * Selecciona una víctima para reemplazo
         * @param marcos Array de marcos de memoria
         * @param pid Proceso que solicita la página
         * @param pagina Página a cargar
         * @param infoExtra Información adicional para el algoritmo
         * @return Índice del marco a reemplazar
         */
        int seleccionarVictima(int[] marcos, int pid, int pagina, Object... infoExtra);
        
        /**
         * Notifica el acceso a un marco (para algoritmos como LRU)
         * @param marco Índice del marco accedido
         */
        void notificarAcceso(int marco);
        
        /**
         * Notifica la carga de una nueva página
         * @param marco Índice del marco cargado
         * @param pid Proceso dueño
         */
        void notificarCarga(int marco, int pid);
    }
    
    // ============================================
    // ALGORITMO FIFO (First In, First Out)
    // ============================================
    public static class FIFO implements EstrategiaReemplazo {
        private Queue<Integer> cola;
        
        public FIFO(int numMarcos) {
            this.cola = new LinkedList<>();
        }
        
        @Override
        public int seleccionarVictima(int[] marcos, int pid, int pagina, Object... infoExtra) {
            // FIFO siempre saca el primero que entró
            return cola.poll();
        }
        
        @Override
        public void notificarAcceso(int marco) {
            // FIFO no considera accesos recientes
        }
        
        @Override
        public void notificarCarga(int marco, int pid) {
            // Cuando se carga una nueva página, se añade a la cola
            cola.add(marco);
        }
        
        public Queue<Integer> getCola() {
            return cola;
        }
    }
    
    // ============================================
    // ALGORITMO LRU (Least Recently Used)
    // ============================================
    public static class LRU implements EstrategiaReemplazo {
        private Map<Integer, Long> tiempoUltimoAcceso; // marco -> timestamp
        private long contadorTiempo;
        
        public LRU(int numMarcos) {
            this.tiempoUltimoAcceso = new HashMap<>();
            this.contadorTiempo = 0;
            
            // Inicializar todos los marcos
            for (int i = 0; i < numMarcos; i++) {
                tiempoUltimoAcceso.put(i, -1L);
            }
        }
        
        @Override
        public int seleccionarVictima(int[] marcos, int pid, int pagina, Object... infoExtra) {
            // LRU: selecciona el marco con el acceso más antiguo
            int victima = -1;
            long tiempoMasAntiguo = Long.MAX_VALUE;
            
            for (Map.Entry<Integer, Long> entry : tiempoUltimoAcceso.entrySet()) {
                int marco = entry.getKey();
                long tiempo = entry.getValue();
                
                // Solo considerar marcos que están ocupados
                if (marcos[marco] != -1 && tiempo < tiempoMasAntiguo) {
                    tiempoMasAntiguo = tiempo;
                    victima = marco;
                }
            }
            
            // Limpiar registro del marco victima
            if (victima != -1) {
                tiempoUltimoAcceso.remove(victima);
                tiempoUltimoAcceso.put(victima, -1L);
            }
            
            return victima;
        }
        
        @Override
        public void notificarAcceso(int marco) {
            // Actualizar tiempo de acceso
            contadorTiempo++;
            tiempoUltimoAcceso.put(marco, contadorTiempo);
        }
        
        @Override
        public void notificarCarga(int marco, int pid) {
            // Registrar carga como un acceso
            notificarAcceso(marco);
        }
        
        public void imprimirEstadoLRU() {
            System.out.print("[LRU Estado]: ");
            List<Map.Entry<Integer, Long>> lista = new ArrayList<>(tiempoUltimoAcceso.entrySet());
            lista.sort(Map.Entry.comparingByValue());
            
            for (Map.Entry<Integer, Long> entry : lista) {
                long tiempo = entry.getValue();
                System.out.print("M" + entry.getKey() + ":" + 
                    (tiempo == -1 ? "N" : tiempo) + " ");
            }
            System.out.println();
        }
    }
    
    // ============================================
    // ALGORITMO ÓPTIMO (Optimal)
    // ============================================
    public static class OPTIMO implements EstrategiaReemplazo {
        private List<List<Integer>> referenciasFuturas; // Por proceso
        
        public OPTIMO(int numMarcos) {
            this.referenciasFuturas = new ArrayList<>();
        }
        
        public void setReferenciasFuturas(List<List<Integer>> referencias) {
            this.referenciasFuturas = referencias;
        }
        
        @Override
        public int seleccionarVictima(int[] marcos, int pid, int pagina, Object... infoExtra) {
            // infoExtra[0] debe ser el índice de referencia actual
            int indiceReferencia = (infoExtra.length > 0) ? (int) infoExtra[0] : 0;
            
            // OPTIMO: selecciona la página que no se usará por más tiempo
            int victima = -1;
            int tiempoMasLejano = -1;
            
            for (int i = 0; i < marcos.length; i++) {
                if (marcos[i] != -1) { // Marco ocupado
                    int tiempoHastaProximoUso = tiempoHastaProximaReferencia(i, marcos[i], indiceReferencia);
                    
                    if (tiempoHastaProximoUso == -1) {
                        // Esta página nunca se usará de nuevo - víctima perfecta
                        return i;
                    }
                    
                    if (tiempoHastaProximoUso > tiempoMasLejano) {
                        tiempoMasLejano = tiempoHastaProximoUso;
                        victima = i;
                    }
                }
            }
            
            return victima;
        }
        
        private int tiempoHastaProximaReferencia(int marco, int pid, int indiceActual) {
            // Buscar la próxima referencia a una página del proceso en este marco
            // En una implementación real, necesitarías mapeo marco->página
            
            // Simulación simplificada: asumimos que cada proceso tiene su lista
            if (pid < referenciasFuturas.size()) {
                List<Integer> referencias = referenciasFuturas.get(pid);
                for (int i = indiceActual + 1; i < referencias.size(); i++) {
                    // Aquí necesitarías lógica específica para determinar
                    // si la referencia es a la página en este marco
                    // Por simplicidad, devolvemos un valor aleatorio
                    return i - indiceActual;
                }
            }
            return -1; // Nunca se referenciará de nuevo
        }
        
        @Override
        public void notificarAcceso(int marco) {
            // OPTIMO no necesita notificaciones de acceso
        }
        
        @Override
        public void notificarCarga(int marco, int pid) {
            // OPTIMO no necesita notificaciones de carga
        }
    }
    
    // ============================================
    // FÁBRICA DE ALGORITMOS
    // ============================================
    public static EstrategiaReemplazo crearEstrategia(String algoritmo, int numMarcos) {
        switch (algoritmo.toUpperCase()) {
            case "FIFO":
                return new FIFO(numMarcos);
            case "LRU":
                return new LRU(numMarcos);
            case "OPTIMO":
            case "OPTIMAL":
                return new OPTIMO(numMarcos);
            default:
                System.err.println("Algoritmo '" + algoritmo + "' no reconocido. Usando FIFO por defecto.");
                return new FIFO(numMarcos);
        }
    }
}