import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class CountingSortVisualizer extends JFrame {

    // Constantes
    private static final int SPHERE_SIZE = 60;
    private static final int BARREL_WIDTH = 120;
    private static final int BARREL_HEIGHT = 200;

    // Colores y valores asociados
    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA
    };

    private static final String[] COLOR_NAMES = {
            "Rojo", "Azul", "Verde", "Naranja", "Morado"
    };

    private static final int[] COLOR_VALUES = {
            1, 2, 3, 4, 5  // Cada color tiene un valor único
    };

    // Componentes de la interfaz
    private JPanel spheresPanel;
    private JPanel barrelsPanel;
    private JPanel sortedPanel;
    private JLabel stepLabel;
    private JButton generateButton;
    private JButton sortButton;
    private JButton resetButton;

    // Datos del algoritmo
    private java.util.List<Sphere> spheres;
    private Map<String, java.util.List<Sphere>> barrels;
    private boolean isAnimating = false;

    // Clase interna para representar una esfera
    private static class Sphere {
        int id;
        Color color;
        String colorName;
        int value;
        int x, y; // Posición para animación

        public Sphere(int id, Color color, String colorName, int value) {
            this.id = id;
            this.color = color;
            this.colorName = colorName;
            this.value = value;
        }
    }

    // Panel personalizado para dibujar esferas
    private class SpherePanel extends JPanel {
        private java.util.List<Sphere> spheresToDraw;
        private String title;
        private boolean showBarrels;

        public SpherePanel(String title, boolean showBarrels) {
            this.title = title;
            this.showBarrels = showBarrels;
            this.spheresToDraw = new ArrayList<>();
            setPreferredSize(new Dimension(800, showBarrels ? 300 : 150));
            setBackground(showBarrels ? new Color(240, 248, 255) : new Color(248, 255, 248));
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 2),
                    title,
                    0,
                    0,
                    new Font("Arial", Font.BOLD, 16)
            ));
        }

        public void setSpheres(java.util.List<Sphere> spheres) {
            this.spheresToDraw = new ArrayList<>(spheres);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (showBarrels) {
                drawBarrels(g2d);
            } else {
                drawSpheresInGrid(g2d);
            }
        }

        private void drawSpheresInGrid(Graphics2D g2d) {
            int cols = (getWidth() - 40) / (SPHERE_SIZE + 10);
            if (cols == 0) cols = 1;

            for (int i = 0; i < spheresToDraw.size(); i++) {
                int row = i / cols;
                int col = i % cols;
                int x = 20 + col * (SPHERE_SIZE + 10);
                int y = 30 + row * (SPHERE_SIZE + 10);

                drawSphere(g2d, spheresToDraw.get(i), x, y);
            }
        }

        private void drawBarrels(Graphics2D g2d) {
            int barrelSpacing = (getWidth() - (COLOR_NAMES.length * BARREL_WIDTH)) / (COLOR_NAMES.length + 1);

            for (int i = 0; i < COLOR_NAMES.length; i++) {
                int x = barrelSpacing + i * (BARREL_WIDTH + barrelSpacing);
                int y = 50;

                // Dibujar barril
                drawBarrel(g2d, x, y, COLORS[i], COLOR_NAMES[i]);

                // Dibujar esferas en el barril
                java.util.List<Sphere> barrelSpheres = barrels.get(COLOR_NAMES[i]);
                if (barrelSpheres != null) {
                    for (int j = 0; j < barrelSpheres.size(); j++) {
                        int sphereX = x + (BARREL_WIDTH - 40) / 2;
                        int sphereY = y + BARREL_HEIGHT - 30 - (j * 35);
                        drawSphere(g2d, barrelSpheres.get(j), sphereX, sphereY, 40);
                    }
                }
            }
        }

        private void drawBarrel(Graphics2D g2d, int x, int y, Color color, String colorName) {
            // Cuerpo del barril
            g2d.setColor(new Color(139, 69, 19)); // Brown
            g2d.fillRoundRect(x, y, BARREL_WIDTH, BARREL_HEIGHT, 20, 20);

            // Borde del barril
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x, y, BARREL_WIDTH, BARREL_HEIGHT, 20, 20);

            // Etiqueta del barril
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x + 10, y - 25, BARREL_WIDTH - 20, 20, 10, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(x + 10, y - 25, BARREL_WIDTH - 20, 20, 10, 10);

            // Texto de la etiqueta
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            String label = colorName;
            int textWidth = fm.stringWidth(label);
            g2d.drawString(label, x + (BARREL_WIDTH - textWidth) / 2, y - 10);

            // Contador de esferas
            java.util.List<Sphere> barrelSpheres = barrels.get(colorName);
            int count = barrelSpheres != null ? barrelSpheres.size() : 0;
            String countText = "(" + count + ")";
            int countWidth = fm.stringWidth(countText);
            g2d.setColor(color);
            g2d.drawString(countText, x + (BARREL_WIDTH - countWidth) / 2, y + 20);
        }

        private void drawSphere(Graphics2D g2d, Sphere sphere, int x, int y) {
            drawSphere(g2d, sphere, x, y, SPHERE_SIZE);
        }

        private void drawSphere(Graphics2D g2d, Sphere sphere, int x, int y, int size) {
            // Sombra
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(x + 3, y + 3, size, size);

            // Esfera principal
            g2d.setColor(sphere.color);
            g2d.fillOval(x, y, size, size);

            // Borde
            g2d.setColor(sphere.color.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, size, size);

            // Brillo
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x + size/4, y + size/4, size/4, size/4);

            // Valor en el centro
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, size/4));
            FontMetrics fm = g2d.getFontMetrics();
            String valueStr = String.valueOf(sphere.value);
            int textWidth = fm.stringWidth(valueStr);
            int textHeight = fm.getHeight();
            g2d.drawString(valueStr,
                    x + (size - textWidth) / 2,
                    y + (size + textHeight/2) / 2);
        }
    }

    public CountingSortVisualizer() {
        initializeComponents();
        setupLayout();
        setupEventListeners();

        spheres = new ArrayList<>();
        barrels = new HashMap<>();

        // Inicializar barriles vacíos
        for (String colorName : COLOR_NAMES) {
            barrels.put(colorName, new ArrayList<>());
        }
    }

    private void initializeComponents() {
        setTitle("Visualizador de Counting Sort - Esferas y Barriles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Etiqueta de pasos
        stepLabel = new JLabel("Haz clic en 'Generar Esferas' para comenzar", JLabel.CENTER);
        stepLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stepLabel.setForeground(Color.BLUE);

        // Botones
        generateButton = new JButton("🎲 Generar Esferas");
        sortButton = new JButton("📊 Iniciar Counting Sort");
        resetButton = new JButton("🔄 Reiniciar");

        sortButton.setEnabled(false);

        // Paneles
        spheresPanel = new SpherePanel("🎯 Esferas Desordenadas", false);
        barrelsPanel = new SpherePanel("🛢️ Barriles de Conteo (por Color)", true);
        sortedPanel = new SpherePanel("✅ Resultado Ordenado", false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel superior con título y botones
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("🔵 Visualizador de Counting Sort 🔵", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(102, 126, 234));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(resetButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(stepLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel central con las visualizaciones
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(spheresPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(barrelsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(sortedPanel);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void setupEventListeners() {
        generateButton.addActionListener(e -> generateSpheres());
        sortButton.addActionListener(e -> startCountingSort());
        resetButton.addActionListener(e -> reset());
    }

    private void generateSpheres() {
        spheres.clear();
        Random random = new Random();

        // Generar 15 esferas aleatorias
        for (int i = 0; i < 15; i++) {
            int colorIndex = random.nextInt(COLORS.length);

            Sphere sphere = new Sphere(
                    i,
                    COLORS[colorIndex],
                    COLOR_NAMES[colorIndex],
                    COLOR_VALUES[colorIndex]  // Ahora cada color tiene un valor fijo
            );

            spheres.add(sphere);
        }

        // Mezclar las esferas
        Collections.shuffle(spheres);

        // Actualizar visualización
        ((SpherePanel) spheresPanel).setSpheres(spheres);

        stepLabel.setText("Esferas generadas. Haz clic en 'Iniciar Counting Sort'");
        sortButton.setEnabled(true);
    }

    private void startCountingSort() {
        if (isAnimating) return;

        isAnimating = true;
        generateButton.setEnabled(false);
        sortButton.setEnabled(false);

        // Ejecutar el algoritmo paso a paso
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                performCountingSort();
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    stepLabel.setText(message);
                }
            }

            @Override
            protected void done() {
                isAnimating = false;
                generateButton.setEnabled(true);
                resetButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void performCountingSort() throws InterruptedException {
        // Paso 1: Limpiar barriles
        for (String colorName : COLOR_NAMES) {
            barrels.get(colorName).clear();
        }

        SwingUtilities.invokeLater(() -> {
            stepLabel.setText("Paso 1: Contando esferas por color...");
            ((SpherePanel) barrelsPanel).repaint();
        });

        Thread.sleep(1500);

        // Paso 2: Contar y colocar esferas en barriles
        for (int i = 0; i < spheres.size(); i++) {
            Sphere sphere = spheres.get(i);
            barrels.get(sphere.colorName).add(sphere);

            final int currentIndex = i;
            SwingUtilities.invokeLater(() -> {
                stepLabel.setText("Colocando esfera " + (currentIndex + 1) + " (valor " +
                        sphere.value + ") en barril " + sphere.colorName);
                ((SpherePanel) barrelsPanel).repaint();
            });

            Thread.sleep(800);
        }

        Thread.sleep(1000);

        // Paso 3: Ordenar dentro de cada barril por valor
        SwingUtilities.invokeLater(() -> {
            stepLabel.setText("Paso 2: Ordenando esferas por valor dentro de cada barril...");
        });

        for (String colorName : COLOR_NAMES) {
            if (!barrels.get(colorName).isEmpty()) {
                barrels.get(colorName).sort(Comparator.comparingInt(s -> s.value));
                SwingUtilities.invokeLater(() -> ((SpherePanel) barrelsPanel).repaint());
                Thread.sleep(500);
            }
        }

        Thread.sleep(1000);

        // Paso 4: Crear resultado ordenado
        java.util.List<Sphere> sortedSpheres = new ArrayList<>();

        SwingUtilities.invokeLater(() -> {
            stepLabel.setText("Paso 3: Creando resultado final ordenado...");
        });

        Thread.sleep(1000);

        // Recopilar todas las esferas ordenadas por valor
        for (String colorName : COLOR_NAMES) {
            for (Sphere sphere : barrels.get(colorName)) {
                sortedSpheres.add(sphere);
                SwingUtilities.invokeLater(() -> {
                    ((SpherePanel) sortedPanel).setSpheres(new ArrayList<>(sortedSpheres));
                });
                Thread.sleep(300);
            }
        }

        SwingUtilities.invokeLater(() -> {
            stepLabel.setText("¡Counting Sort completado! Esferas ordenadas por valor.");
        });
    }

    private void reset() {
        spheres.clear();
        for (String colorName : COLOR_NAMES) {
            barrels.get(colorName).clear();
        }

        ((SpherePanel) spheresPanel).setSpheres(new ArrayList<>());
        ((SpherePanel) barrelsPanel).repaint();
        ((SpherePanel) sortedPanel).setSpheres(new ArrayList<>());

        stepLabel.setText("Sistema reiniciado. Haz clic en 'Generar Esferas' para comenzar");

        generateButton.setEnabled(true);
        sortButton.setEnabled(false);
        isAnimating = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CountingSortVisualizer().setVisible(true);
        });
    }
}