import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class InterfazGrafica extends JFrame {
    private List<Barril> barriles;
    private Metodo metodo;
    private Balanza balanza;
    private JPanel panelBarriles;
    private Image barrilImg, balanzaImg;
    private JLabel comparacionesLabel;
    private JButton mezclarBtn, ordenarBtn, animarBtn;

    // Para animación de comparación
    private int comparandoA = -1, comparandoB = -1;
    private double animacionProgreso = 0.0;

    public InterfazGrafica() {
        setTitle("Ordenamiento de Barriles con Heap Sort");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        barriles = new ArrayList<>();
        barriles.add(new Barril(21));
        barriles.add(new Barril(5));
        barriles.add(new Barril(65));
        barriles.add(new Barril(56));
        barriles.add(new Barril(75));

        balanza = new Balanza();
        metodo = new Metodo(balanza);

        comparacionesLabel = new JLabel("Comparaciones: 0");
        mezclarBtn = new JButton("Mezclar");
        ordenarBtn = new JButton("Ordenar");
        animarBtn = new JButton("Animar ordenamiento");

        JPanel panelBotones = new JPanel();
        panelBotones.add(mezclarBtn);
        panelBotones.add(ordenarBtn);
        panelBotones.add(animarBtn);
        panelBotones.add(comparacionesLabel);
        add(panelBotones, BorderLayout.NORTH);

        barrilImg = new ImageIcon("Barril2.png").getImage();
        balanzaImg = new ImageIcon("Balanza2.png").getImage();
        panelBarriles = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar la imagen de fondo escalada al tamaño del panel
                if (balanzaImg != null) {
                    g.drawImage(balanzaImg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fondo de respaldo si no se carga la imagen
                    g.setColor(new Color(180, 200, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Dibuja los barriles más grandes y centrados
                int barrilWidth = 80;
                int barrilHeight = 120;
                int espacio = (getWidth() - barriles.size() * barrilWidth) / (barriles.size() + 1);
                int yBarril = getHeight() - barrilHeight - 20; // Posición en la parte inferior
                int x = espacio;
                // Posiciones originales de los barriles
                int[] xs = new int[barriles.size()];
                int[] ys = new int[barriles.size()];
                for (int i = 0; i < barriles.size(); i++) {
                    xs[i] = x;
                    ys[i] = yBarril;
                    x += barrilWidth + espacio;
                }
                // Si hay animación de comparación, mover los barriles
                if (comparandoA != -1 && comparandoB != -1) {
                    // Coordenadas para los platillos de la balanza (ajustadas para la nueva imagen)
                    int platilloY = (int)(getHeight() * 0.10); // altura del platillo en la nueva imagen
                    int platilloIzqX = (int)(getWidth() * 0.32) - barrilWidth / 2; // platillo izquierdo
                    int platilloDerX = (int)(getWidth() * 0.68) - barrilWidth / 2; // platillo derecho
                    // Interpolación de movimiento
                    xs[comparandoA] = (int)(xs[comparandoA] * (1 - animacionProgreso) + platilloIzqX * animacionProgreso);
                    ys[comparandoA] = (int)(ys[comparandoA] * (1 - animacionProgreso) + platilloY * animacionProgreso);
                    xs[comparandoB] = (int)(xs[comparandoB] * (1 - animacionProgreso) + platilloDerX * animacionProgreso);
                    ys[comparandoB] = (int)(ys[comparandoB] * (1 - animacionProgreso) + platilloY * animacionProgreso);
                }
                // Dibujar barriles
                for (int i = 0; i < barriles.size(); i++) {
                    g.drawImage(barrilImg, xs[i], ys[i], barrilWidth, barrilHeight, this);
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    FontMetrics fm = g.getFontMetrics();
                    String peso = String.valueOf(barriles.get(i).getPeso());
                    int textX = xs[i] + (barrilWidth - fm.stringWidth(peso)) / 2;
                    int textY = ys[i] + barrilHeight / 2 + fm.getAscent() / 2;
                    g.drawString(peso, textX, textY);
                }
            }
        };
        panelBarriles.setPreferredSize(new Dimension(600, 250));
        add(panelBarriles, BorderLayout.CENTER);

        mezclarBtn.addActionListener(e -> mezclarBarriles());
        ordenarBtn.addActionListener(e -> ordenarBarriles());
        animarBtn.addActionListener(e -> animarOrdenamiento());
    }

    private void mezclarBarriles() {
        Collections.shuffle(barriles);
        balanza.resetComparaciones();
        comparacionesLabel.setText("Comparaciones: 0");
        panelBarriles.repaint();
    }

    private void ordenarBarriles() {
        metodo.heapSort(barriles);
        comparacionesLabel.setText("Comparaciones: " + balanza.getComparaciones());
        panelBarriles.repaint();
    }

    // Animación paso a paso del ordenamiento
    private void animarOrdenamiento() {
        new Thread(() -> {
            int n = barriles.size();
            // Construir el heap
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapifyAnimado(n, i);
            }
            // Extraer elementos del heap uno por uno
            for (int i = n - 1; i > 0; i--) {
                Barril temp = barriles.get(0);
                barriles.set(0, barriles.get(i));
                barriles.set(i, temp);
                panelBarriles.repaint();
                comparacionesLabel.setText("Comparaciones: " + balanza.getComparaciones());
                dormirAnimacion();
                heapifyAnimado(i, 0);
            }
            comparacionesLabel.setText("Comparaciones: " + balanza.getComparaciones());
        }).start();
    }

    // Animar el movimiento de dos barriles hacia la balanza
    private void animarComparacion(int idxA, int idxB) {
        comparandoA = idxA;
        comparandoB = idxB;
        for (int paso = 0; paso <= 10; paso++) {
            animacionProgreso = paso / 10.0;
            panelBarriles.repaint();
            try {
                Thread.sleep(35); // velocidad de animación
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Pausa en la balanza
        animacionProgreso = 1.0;
        panelBarriles.repaint();
        dormirAnimacion();
        // Regresar barriles
        for (int paso = 10; paso >= 0; paso--) {
            animacionProgreso = paso / 10.0;
            panelBarriles.repaint();
            try {
                Thread.sleep(35);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        comparandoA = -1;
        comparandoB = -1;
        animacionProgreso = 0.0;
        panelBarriles.repaint();
    }

    // Heapify animado
    private void heapifyAnimado(int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        // Animar comparación izquierda
        if (left < n) {
            animarComparacion(left, largest);
            if (balanza.comparar(barriles.get(left), barriles.get(largest)) == barriles.get(left))
                largest = left;
        }
        // Animar comparación derecha
        if (right < n) {
            animarComparacion(right, largest);
            if (balanza.comparar(barriles.get(right), barriles.get(largest)) == barriles.get(right))
                largest = right;
        }
        if (largest != i) {
            Barril swap = barriles.get(i);
            barriles.set(i, barriles.get(largest));
            barriles.set(largest, swap);
            panelBarriles.repaint();
            comparacionesLabel.setText("Comparaciones: " + balanza.getComparaciones());
            dormirAnimacion();
            heapifyAnimado(n, largest);
        }
    }

    // Pausa para la animación
    private void dormirAnimacion() {
        try {
            Thread.sleep(700); // 700 ms entre pasos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfazGrafica().setVisible(true);
        });
    }
}
