import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Crear barriles con pesos de ejemplo
        List<Barril> barriles = new ArrayList<>();
        barriles.add(new Barril(21));
        barriles.add(new Barril(5));
        barriles.add(new Barril(65));
        barriles.add(new Barril(56));
        barriles.add(new Barril(75));

        // Crear balanza y método
        Balanza balanza = new Balanza();
        Metodo metodo = new Metodo(balanza);

        System.out.println("Pesos antes de ordenar:");
        for (Barril b : barriles) {
            System.out.print(b.getPeso() + " ");
        }
        System.out.println();

        // Ordenar los barriles
        metodo.heapSort(barriles);

        System.out.println("Pesos después de ordenar:");
        for (Barril b : barriles) {
            System.out.print(b.getPeso() + " ");
        }
        System.out.println();
        System.out.println("Comparaciones realizadas: " + balanza.getComparaciones());
    }
}
