import java.util.List;

public class Metodo {
    private Balanza balanza;

    public Metodo(Balanza balanza) {
        this.balanza = balanza;
    }

    // Heap Sort usando Barril y Balanza
    public void heapSort(List<Barril> barriles) {
        int n = barriles.size();
        // Construir el heap
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(barriles, n, i);
        // Extraer elementos del heap uno por uno
        for (int i = n - 1; i > 0; i--) {
            Barril temp = barriles.get(0);
            barriles.set(0, barriles.get(i));
            barriles.set(i, temp);
            heapify(barriles, i, 0);
        }
    }

    // Mantener la propiedad de heap usando la balanza
    private void heapify(List<Barril> barriles, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (left < n && balanza.comparar(barriles.get(left), barriles.get(largest)) == barriles.get(left))
            largest = left;
        if (right < n && balanza.comparar(barriles.get(right), barriles.get(largest)) == barriles.get(right))
            largest = right;
        if (largest != i) {
            Barril swap = barriles.get(i);
            barriles.set(i, barriles.get(largest));
            barriles.set(largest, swap);
            heapify(barriles, n, largest);
        }
    }
}
