public class Balanza {
    // Cuenta las comparaciones realizadas
    private int comparaciones = 0;

    // Compara dos barriles y retorna el mayor
    public Barril comparar(Barril b1, Barril b2) {
        comparaciones++;
        if (b1.getPeso() >= b2.getPeso()) {
            return b1;
        } else {
            return b2;
        }
    }

    public int getComparaciones() {
        return comparaciones;
    }

    public void resetComparaciones() {
        comparaciones = 0;
    }
}
