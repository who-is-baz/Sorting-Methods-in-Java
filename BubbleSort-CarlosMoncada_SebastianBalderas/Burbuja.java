public class Burbuja {
    
    // Clase para representar los objetos de curación de Fortnite
    static class ItemCuracion {
        String nombre;
        int vida;
        String imagen;
        
        public ItemCuracion(String nombre, int vida, String imagen) {
            this.nombre = nombre;
            this.vida = vida;
            this.imagen = imagen;
        }
        
        @Override
        public String toString() {
            return nombre + " (+" + vida + " HP) - " + imagen;
        }
    }
    
    // Método de ordenamiento burbuja por cantidad de vida
    public static void ordenamientoBurbuja(ItemCuracion[] items) {
        int n = items.length;
        boolean intercambio;
        
        System.out.println("=== INICIANDO ORDENAMIENTO BURBUJA ===");
        System.out.println("Ordenando items de curación de menor a mayor HP...\n");
        
        for (int i = 0; i < n - 1; i++) {
            intercambio = false;
            System.out.println("--- Pasada " + (i + 1) + " ---");
            
            for (int j = 0; j < n - 1 - i; j++) {
                System.out.println("Comparando: " + items[j].nombre + "(" + items[j].vida + ") vs " + 
                                 items[j + 1].nombre + "(" + items[j + 1].vida + ")");
                
                if (items[j].vida > items[j + 1].vida) {
                    // Intercambiar elementos
                    ItemCuracion temp = items[j];
                    items[j] = items[j + 1];
                    items[j + 1] = temp;
                    intercambio = true;
                    
                    System.out.println("¡Intercambio realizado!");
                } else {
                    System.out.println("No hay intercambio");
                }
            }
            
            if (!intercambio) {
                System.out.println("Array ya ordenado, terminando...");
                break;
            }
            System.out.println();
        }
    }
    
    // Método para mostrar el inventario
    public static void mostrarInventario(ItemCuracion[] items, String titulo) {
        System.out.println("=== " + titulo + " ===");
        for (int i = 0; i < items.length; i++) {
            System.out.println((i + 1) + ". " + items[i]);
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        // Items de curación de Fortnite
        ItemCuracion[] inventario = {
            new ItemCuracion("Bandage", 15, "bandage.png"),
            new ItemCuracion("Med Kit", 100, "medkit.png"),
            new ItemCuracion("Mini Shield", 25, "mini_shield.png"),
            new ItemCuracion("Shield Potion", 50, "shield_potion.png"),
            new ItemCuracion("Slurp Juice", 75, "slurp_juice.png"),
            new ItemCuracion("Chug Jug", 200, "chug_jug.png")
        };
        
        System.out.println("🎮 FORTNITE - ORGANIZADOR DE ITEMS DE CURACIÓN 🎮\n");
        
        mostrarInventario(inventario, "INVENTARIO INICIAL");
        
        ordenamientoBurbuja(inventario);
        
        mostrarInventario(inventario, "INVENTARIO ORDENADO");
        
        System.out.println("¡Inventario organizado exitosamente! 🏆");
    }
}