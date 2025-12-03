package Memoria;

import java.util.*;

public class TablaPaginas {
    private Map<Integer, List<EntradaPagina>> tablas;
    
    public class EntradaPagina {
        public int pagina;
        public int marco;
        public boolean presente;
        public boolean modificada;
        public boolean referenciada;
        
        public EntradaPagina(int pagina, int marco) {
            this.pagina = pagina;
            this.marco = marco;
            this.presente = true;
            this.modificada = false;
            this.referenciada = true;
        }
    }
    
    public TablaPaginas() {
        this.tablas = new HashMap<>();
    }
    
    public void crearTabla(int pid, List<Integer> paginas) {
        List<EntradaPagina> tabla = new ArrayList<>();
        for (int pagina : paginas) {
            tabla.add(new EntradaPagina(pagina, -1)); // -1 = no cargada
        }
        tablas.put(pid, tabla);
    }
    
    public void actualizarMarco(int pid, int pagina, int marco) {
        List<EntradaPagina> tabla = tablas.get(pid);
        if (tabla != null) {
            for (EntradaPagina entrada : tabla) {
                if (entrada.pagina == pagina) {
                    entrada.marco = marco;
                    entrada.presente = (marco != -1);
                    break;
                }
            }
        }
    }
    
    public void imprimirTabla(int pid) {
        List<EntradaPagina> tabla = tablas.get(pid);
        if (tabla == null) {
            System.out.println("No existe tabla de páginas para P" + pid);
            return;
        }
        
        System.out.println("\n=== TABLA DE PÁGINAS - P" + pid + " ===");
        System.out.println("+---------+---------+----------+------------+");
        System.out.println("| Página  | Marco   | Presente | Referenciada|");
        System.out.println("+---------+---------+----------+------------+");
        
        for (EntradaPagina entrada : tabla) {
            System.out.printf("| %7d | %7s | %8s | %10s |\n",
                entrada.pagina,
                entrada.marco == -1 ? "Disk" : entrada.marco,
                entrada.presente ? "Sí" : "No",
                entrada.referenciada ? "Sí" : "No");
        }
        System.out.println("+---------+---------+----------+------------+");
    }
    
    public void imprimirTodasTablas() {
        for (int pid : tablas.keySet()) {
            imprimirTabla(pid);
        }
    }
}