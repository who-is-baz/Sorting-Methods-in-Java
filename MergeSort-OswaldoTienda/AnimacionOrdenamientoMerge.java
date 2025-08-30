import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/// Creado por José Oswaldo Tienda Mendoza

public class AnimacionOrdenamientoMerge extends JFrame {
    private int[] array;
    private int comparisons = 0;
    private int moves = 0;
    private final DrawPanel drawPanel;
    private final JLabel comparisonLabel;
    private final JLabel moveLabel;
    private final JButton shuffleButton;
    private final JButton sortButton;
    private final JButton toggleNumbersButton;
    private final JSlider speedSlider;
    private Image barrelImage;
    private Image backgroundImage; // Variable para el fondo

    private int delay = 500;
    private final int BAR_WIDTH = 80;
    private final int BAR_HEIGHT = 120;
    private boolean showNumbers = true;

    private List<List<int[]>> states = new ArrayList<>();

    // Variables para resaltar comparaciones
    private int compareIndex1 = -1;
    private int compareIndex2 = -1;
    private int currentLevel = -1;

    public AnimacionOrdenamientoMerge(int size) {
        array = generateRandomArray(size);

        barrelImage = new ImageIcon(getClass().getResource("/resources/Barril1.png")).getImage();
        backgroundImage = new ImageIcon(getClass().getResource("/resources/backgroundMergeSort.jpg")).getImage(); // Carga la imagen del fondo

        drawPanel = new DrawPanel();
        comparisonLabel = new JLabel("Comparaciones: 0");
        moveLabel = new JLabel("Movimientos: 0");
        shuffleButton = new JButton("Mezclar");
        sortButton = new JButton("Ordenar (Merge Sort)");
        toggleNumbersButton = new JButton("Ocultar Números");
        speedSlider = new JSlider(50, 1000, delay);

        shuffleButton.addActionListener(e -> shuffleArray());
        sortButton.addActionListener(e -> new Thread(this::mergeSortStart).start());
        toggleNumbersButton.addActionListener(e -> toggleNumbers());
        speedSlider.addChangeListener(e -> delay = speedSlider.getValue());

        JPanel controlPanel = new JPanel();
        controlPanel.add(comparisonLabel);
        controlPanel.add(moveLabel);
        controlPanel.add(shuffleButton);
        controlPanel.add(sortButton);
        controlPanel.add(toggleNumbersButton);
        controlPanel.add(new JLabel("Velocidad:"));
        controlPanel.add(speedSlider);

        JScrollPane scrollPane = new JScrollPane(drawPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setTitle("Animación Merge Sort con Barriles");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        captureState(array);
    }

    private int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = rand.nextInt(150) + 50;
        return arr;
    }

    private void shuffleArray() {
        array = generateRandomArray(array.length);
        comparisons = 0;
        moves = 0;
        compareIndex1 = -1;
        compareIndex2 = -1;
        states.clear();
        updateLabels();
        captureState(array);
        drawPanel.repaint();
    }

    private void updateLabels() {
        comparisonLabel.setText("Comparaciones: " + comparisons);
        moveLabel.setText("Movimientos: " + moves);
    }

    private void toggleNumbers() {
        showNumbers = !showNumbers;
        toggleNumbersButton.setText(showNumbers ? "Ocultar Números" : "Mostrar Números");
        drawPanel.repaint();
    }

    private void mergeSortStart() {
        states.clear();
        compareIndex1 = -1;
        compareIndex2 = -1;
        captureState(array);
        mergeSort(array, 0, array.length - 1);
    }

    private void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
            captureState(arr);
            sleep();
        }
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;
        currentLevel = states.size(); // Nivel actual

        while (i < n1 && j < n2) {
            comparisons++;
            compareIndex1 = left + i;
            compareIndex2 = mid + 1 + j;
            drawPanel.repaint();

            if (L[i] <= R[j]) arr[k++] = L[i++];
            else arr[k++] = R[j++];
            moves++;
            updateLabels();
            sleep();
        }
        while (i < n1) {
            arr[k++] = L[i++];
            moves++;
            updateLabels();
            sleep();
        }
        while (j < n2) {
            arr[k++] = R[j++];
            moves++;
            updateLabels();
            sleep();
        }
        compareIndex1 = -1;
        compareIndex2 = -1;
    }

    private void captureState(int[] arr) {
        List<int[]> snapshot = new ArrayList<>();
        snapshot.add(arr.clone());
        states.add(snapshot);

        int panelHeight = 150 + (states.size() * 150);
        drawPanel.setPreferredSize(new Dimension(1200, panelHeight));
        drawPanel.revalidate();

        drawPanel.repaint();
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Dibujar el fondo
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            int yOffsetBase = 50;
            for (int level = 0; level < states.size(); level++) {
                int[] current = states.get(level).get(0);

                int spacing = BAR_WIDTH;
                int panelWidth = getWidth();
                int totalWidth = current.length * spacing;
                int xOffset = (panelWidth - totalWidth) / 2;
                int yOffset = yOffsetBase + (level * 130);

                for (int i = 0; i < current.length; i++) {
                    int value = current[i];
                    g.drawImage(barrelImage, xOffset + i * spacing, yOffset, BAR_WIDTH, BAR_HEIGHT, this);

                    if (showNumbers) {
                        if (level == currentLevel && (i == compareIndex1 || i == compareIndex2)) {
                            g.setColor(Color.RED);
                        } else {
                            g.setColor(Color.WHITE);
                        }
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        FontMetrics fm = g.getFontMetrics();
                        int textWidth = fm.stringWidth(String.valueOf(value));
                        int textX = xOffset + i * spacing + (BAR_WIDTH - textWidth) / 2;
                        int textY = yOffset + (BAR_HEIGHT / 2) + fm.getAscent() / 2;
                        g.drawString(String.valueOf(value), textX, textY);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnimacionOrdenamientoMerge(8));
    }
}
