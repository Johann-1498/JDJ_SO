package Memoria;

import java.util.*;

public class AlgoritmosReemplazo {
    
    public interface EstrategiaReemplazo {
        // Cambiamos Object... infoExtra a Object[] infoExtra para evitar warnings
        int seleccionarVictima(int[] marcos, int pid, int pagina, Object[] infoExtra);
        void notificarAcceso(int marco);
        void notificarCarga(int marco, int pid);
    }
    
    public static class FIFO implements EstrategiaReemplazo {
        private Queue<Integer> cola;
        
        public FIFO(int numMarcos) {
            this.cola = new LinkedList<>();
        }
        
        @Override
        public int seleccionarVictima(int[] marcos, int pid, int pagina, Object[] infoExtra) {
            if (cola.isEmpty()) {
                for (int i = 0; i < marcos.length; i++) {
                    if (marcos[i] != -1) return i;
                }
                return 0;
            }
            return cola.poll();
        }
        
        @Override
        public void notificarAcceso(int marco) {}
        
        @Override
        public void notificarCarga(int marco, int pid) {
            cola.add(marco);
        }
        
        public Queue<Integer> getCola() {
            return cola;
        }
    }
    
    public static class LRU implements EstrategiaReemplazo {
        private LinkedHashMap<Integer, Long> accesoMap;
        private long contadorTiempo;
        
        public LRU(int numMarcos) {
            this.accesoMap = new LinkedHashMap<>(numMarcos, 0.75f, true);
            this.contadorTiempo = 0;
        }
        
        @Override
        public int seleccionarVictima(int[] marcos, int pid, int pagina, Object[] infoExtra) {
            if (!accesoMap.isEmpty()) {
                int victima = accesoMap.keySet().iterator().next();
                accesoMap.remove(victima);
                return victima;
            }
            
            for (int i = 0; i < marcos.length; i++) {
                if (marcos[i] != -1) return i;
            }
            return 0;
        }
        
        @Override
        public void notificarAcceso(int marco) {
            contadorTiempo++;
            accesoMap.put(marco, contadorTiempo);
        }
        
        @Override
        public void notificarCarga(int marco, int pid) {
            notificarAcceso(marco);
        }
        
        public void imprimirEstadoLRU() {
            System.out.print("[LRU]: Orden (menos reciente → más reciente): ");
            for (Integer marco : accesoMap.keySet()) {
                System.out.print("M" + marco + " ");
            }
            System.out.println();
        }
    }
    
    public static class OPTIMO implements EstrategiaReemplazo {
        private List<Integer> secuenciaReferencias;
        private int indiceActual;
        
        public OPTIMO(int numMarcos) {
            this.secuenciaReferencias = new ArrayList<>();
            this.indiceActual = 0;
        }
        
        public void setSecuenciaReferencias(List<Integer> secuencia) {
            this.secuenciaReferencias = secuencia;
            this.indiceActual = 0;
        }
        
        @Override
        public int seleccionarVictima(int[] marcos, int pid, int pagina, Object[] infoExtra) {
            if (infoExtra != null && infoExtra.length > 0 && infoExtra[0] instanceof List) {
                @SuppressWarnings("unchecked")
                List<Integer> refFuturas = (List<Integer>) infoExtra[0];
                return seleccionarConReferencias(marcos, refFuturas);
            }
            
            for (int i = 0; i < marcos.length; i++) {
                if (marcos[i] != -1) return i;
            }
            return 0;
        }
        
        private int seleccionarConReferencias(int[] marcos, List<Integer> refFuturas) {
            int victima = -1;
            int maxDistancia = -1;
            
            for (int i = 0; i < marcos.length; i++) {
                if (marcos[i] != -1) {
                    int distancia = encontrarDistancia(i, refFuturas);
                    if (distancia == -1) {
                        return i;
                    }
                    if (distancia > maxDistancia) {
                        maxDistancia = distancia;
                        victima = i;
                    }
                }
            }
            
            return (victima != -1) ? victima : 0;
        }
        
        private int encontrarDistancia(int marco, List<Integer> refFuturas) {
            for (int j = 0; j < Math.min(10, refFuturas.size()); j++) {
                if (refFuturas.get(j) == marco) {
                    return j;
                }
            }
            return -1;
        }
        
        @Override
        public void notificarAcceso(int marco) {}
        
        @Override
        public void notificarCarga(int marco, int pid) {}
    }
    
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
                System.out.println("Algoritmo '" + algoritmo + "' no reconocido. Usando FIFO.");
                return new FIFO(numMarcos);
        }
    }
}