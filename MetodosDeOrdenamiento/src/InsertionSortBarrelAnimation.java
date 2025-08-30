import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class InsertionSortBarrelAnimation extends JPanel {
    // Arreglos para almacenar los valores de los barriles y las posiciones
    private int[] array;
    private int[] originalArray; // Para mantener los valores originales
    private int sortedCount = 0;
    private Image barrelImage;
    private Image backgroundImage;
    private static final int BARREL_WIDTH = 64; // Ancho del barril
    private static final int BARREL_HEIGHT = 64; // Altura del barril
    private static final int DELAY = 50; // Retraso entre los pasos de la animación
    private boolean autoMode = false; // Modo automático de la animación
    private boolean showWeights = true; // Mostrar u ocultar pesos
    private Image eyeOpenImage, eyeClosedImage; // Imágenes para el botón de mostrar/ocultar pesos
    private JButton eyeButton, modeButton, resetButton; // Botones de la interfaz
    private Timer animationTimer; // Temporizador para la animación

    // Estado de arrastre en el modo manual
    private boolean dragging = false;
    private int dragIndex = -1;
    private Point dragOffset = null;

    // Posiciones de los barriles
    private int[] barrelPositions; // 0: estante, 1: balanza, 2: transportadora
    private int[] shelfSlots; // Qué barril está en cada slot del estante (-1 si está vacío)
    private int scaleLeft = -1; // Posición del barril en el lado izquierdo de la balanza
    private int scaleRight = -1; // Posición del barril en el lado derecho de la balanza

    // Color del tema seleccionado
    private Color themeColor = Color.BLACK;

    // Control de si ya se mostró el mensaje de victoria
    private boolean victoryShown = false;

    // Variables para el valor máximo y mínimo de los barriles
    private int maxValue, minValue;

    // Pairs de imágenes de personajes y fondos
    private String[][] pairs = {
            {"BMO.png", "HDA.png"},
            {"G.jpeg", "RH.png"},
            {"F.png", "RF.png"},
            {"PC.jpeg", "DR.png"}
    };

    // Colores asociados a cada par de personajes y fondos
    private Color[] themeColors = {
            new Color(0, 150, 0),      // Verde para BMO-HDA
            new Color(0, 0, 200),      // Azul para G-RH
            Color.WHITE,               // Blanco para F-RF
            new Color(255, 20, 147)    // Rosa para PC-DR
    };

    public InsertionSortBarrelAnimation(int size) {
        // Inicializar los arreglos de barriles
        array = new int[size];
        originalArray = new int[size];
        barrelPositions = new int[size];
        shelfSlots = new int[12]; // 12 slots en el estante

        // Inicializar slots vacíos en el estante
        for (int i = 0; i < 12; i++) {
            shelfSlots[i] = -1;
        }

        Random rand = new Random();
        maxValue = Integer.MIN_VALUE;
        minValue = Integer.MAX_VALUE;

        // Asignar pesos aleatorios a los barriles y determinar el valor máximo y mínimo
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(181) + 20;
            originalArray[i] = array[i]; // Guardar los valores originales
            barrelPositions[i] = 2; // Colocar barriles en la transportadora inicialmente

            if (array[i] > maxValue) maxValue = array[i];
            if (array[i] < minValue) minValue = array[i];
        }

        // Selección aleatoria de personajes y fondos
        Random random = new Random();
        int index = random.nextInt(pairs.length);
        String personaje = pairs[index][0];
        String fondo = pairs[index][1];

        // Asignar color del tema según el personaje y fondo seleccionados
        themeColor = themeColors[index];

        // Cargar las imágenes de los personajes y fondos
        try {
            barrelImage = new ImageIcon(personaje).getImage()
                    .getScaledInstance(BARREL_WIDTH, BARREL_HEIGHT, Image.SCALE_SMOOTH);
            backgroundImage = new ImageIcon(fondo).getImage();
            eyeOpenImage = new ImageIcon("ojo_abierto.png").getImage()
                    .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            eyeClosedImage = new ImageIcon("ojo_cerrado.png").getImage()
                    .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            // Si no se pueden cargar las imágenes, se usa una representación simple
            System.out.println("No se pudieron cargar las imágenes, usando representación simple");
        }

        setBackground(Color.WHITE);
        setOpaque(true);
        int panelWidth = Math.max(800, size * (BARREL_WIDTH + 10) + 200);
        setPreferredSize(new Dimension(panelWidth, 600));

        setupUI(); // Configurar la interfaz de usuario
    }

    private void setupUI() {
        // Crear el panel de botones para controlar la interfaz
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);

        // Botón para mostrar/ocultar los pesos de los barriles
        if (eyeOpenImage != null) {
            eyeButton = new JButton(new ImageIcon(eyeOpenImage));
        } else {
            eyeButton = new JButton("👁");
        }
        eyeButton.setToolTipText("Mostrar/Ocultar Pesos");
        eyeButton.addActionListener(e -> toggleWeights());

        // Botón para cambiar entre modo manual y automático
        modeButton = new JButton("Manual");
        modeButton.setToolTipText("Cambiar Modo");
        modeButton.addActionListener(e -> toggleMode());

        // Botón para reiniciar las posiciones de los barriles
        resetButton = new JButton("Reset");
        resetButton.setToolTipText("Reiniciar Posiciones");
        resetButton.addActionListener(e -> resetPositions());

        // Agregar los botones al panel
        buttons.add(eyeButton);
        buttons.add(modeButton);
        buttons.add(resetButton);

        // Crear la ventana del programa
        JFrame frame = new JFrame("Insertion Sort – Espacios Definidos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(buttons, BorderLayout.NORTH); // Agregar los botones en la parte superior
        frame.add(this, BorderLayout.CENTER);  // Agregar el panel de la animación en el centro
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Agregar listeners para eventos de ratón (para arrastrar los barriles en modo manual)
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { onPress(e); }
            public void mouseReleased(MouseEvent e) { onRelease(e); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { onDrag(e); }
        });
    }

    private void toggleWeights() {
        showWeights = !showWeights; // Cambiar el estado de la visibilidad de los pesos
        if (eyeOpenImage != null) {
            eyeButton.setIcon(new ImageIcon(showWeights ? eyeOpenImage : eyeClosedImage));
        } else {
            eyeButton.setText(showWeights ? "👁" : "👁‍🗨");
        }
        repaint(); // Redibujar la interfaz para reflejar los cambios
    }

    private void resetPositions() {
        // Resetear todas las posiciones de los barriles
        for (int i = 0; i < array.length; i++) {
            barrelPositions[i] = 2; // Volver todos los barriles a la transportadora
        }
        for (int i = 0; i < 12; i++) {
            shelfSlots[i] = -1; // Vaciar los slots del estante
        }
        scaleLeft = scaleRight = -1; // Restablecer las posiciones de la balanza
        sortedCount = 0;
        victoryShown = false; // Restablecer el estado de victoria
        dragging = false;
        dragIndex = -1;
        repaint();
    }

    private void toggleMode() {
        autoMode = !autoMode; // Cambiar entre el modo automático y manual
        modeButton.setText(autoMode ? "Automático" : "Manual");
        scaleLeft = scaleRight = -1;
        dragging = false;
        if (autoMode) startAuto(); // Iniciar la animación automática
        else if (animationTimer != null) animationTimer.stop(); // Detener la animación si está en manual
    }

    private void startAuto() {
        // Reiniciar posiciones de los barriles y estante en la animación automática
        for (int i = 0; i < array.length; i++) {
            barrelPositions[i] = 2; // Volver a la transportadora
        }
        for (int i = 0; i < 12; i++) {
            shelfSlots[i] = -1; // Vaciar el estante
        }

        // Iniciar el temporizador de la animación automática
        animationTimer = new Timer(DELAY * 4, new ActionListener() {
            int currentStep = 0;
            int[] sortedArray = new int[array.length];
            int[] sortedIndices = new int[array.length];
            boolean initialized = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!initialized) {
                    // Inicializar los arreglos para el ordenamiento
                    for (int i = 0; i < array.length; i++) {
                        sortedArray[i] = originalArray[i];
                        sortedIndices[i] = i;
                    }
                    // Realizar el ordenamiento por el algoritmo de inserción
                    for (int i = 1; i < sortedArray.length; i++) {
                        int keyValue = sortedArray[i];
                        int keyIndex = sortedIndices[i];
                        int j = i - 1;
                        while (j >= 0 && sortedArray[j] > keyValue) {
                            sortedArray[j + 1] = sortedArray[j];
                            sortedIndices[j + 1] = sortedIndices[j];
                            j--;
                        }
                        sortedArray[j + 1] = keyValue;
                        sortedIndices[j + 1] = keyIndex;
                    }
                    initialized = true;
                    return;
                }

                if (currentStep < array.length) {
                    // Mostrar la comparación de barriles en la balanza
                    int barrelIndex = sortedIndices[currentStep];

                    // Animación de la comparación
                    scaleLeft = barrelIndex;
                    if (currentStep > 0) {
                        scaleRight = sortedIndices[currentStep - 1];
                    } else {
                        scaleRight = -1;
                    }

                    barrelPositions[barrelIndex] = 1; // Mover a la balanza
                    if (scaleRight >= 0) {
                        barrelPositions[scaleRight] = 1; // Mover a la balanza
                    }

                    repaint();

                    // Después de un momento, mover el barril al estante en la posición correcta
                    Timer moveTimer = new Timer(DELAY * 2, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            ((Timer)evt.getSource()).stop();

                            // Colocar el barril en el slot correcto del estante
                            shelfSlots[currentStep] = barrelIndex;
                            barrelPositions[barrelIndex] = 0; // Mover al estante

                            // Quitar el barril de la balanza
                            if (scaleLeft == barrelIndex) scaleLeft = -1;
                            if (scaleRight == barrelIndex) scaleRight = -1;

                            // Si hay un barril en scaleRight, devolverlo al estante
                            if (scaleRight >= 0) {
                                // Encontrar su posición correcta en el estante
                                for (int slot = 0; slot < currentStep; slot++) {
                                    if (shelfSlots[slot] == scaleRight) {
                                        barrelPositions[scaleRight] = 0;
                                        break;
                                    }
                                }
                                scaleRight = -1;
                            }

                            currentStep++;
                            repaint();
                        }
                    });
                    moveTimer.setRepeats(false);
                    moveTimer.start();

                } else {
                    // Si todos los barriles han sido ordenados, detener la animación
                    animationTimer.stop();
                    scaleLeft = scaleRight = -1;

                    SwingUtilities.invokeLater(() -> {
                        autoMode = false;
                        modeButton.setText("Manual");
                        repaint();

                        // Verificar si la animación está completada correctamente
                        checkIfCompleted();
                    });
                }
            }
        });
        animationTimer.start();
    }

    private void onPress(MouseEvent e) {
        if (!autoMode) {
            Point p = e.getPoint();
            dragging = false;
            dragIndex = -1;

            // Verificar barriles en la transportadora
            int baseY = getHeight() - BARREL_HEIGHT - 10;
            for (int i = 0; i < array.length; i++) {
                if (barrelPositions[i] == 2) {
                    int x = getConveyorPosition(i);
                    Rectangle r = new Rectangle(x, baseY, BARREL_WIDTH, BARREL_HEIGHT);
                    if (r.contains(p)) {
                        startDragging(i, p, x, baseY);
                        return;
                    }
                }
            }

            // Verificar barriles en el estante
            int shelfY = getHeight() - BARREL_HEIGHT - 120;
            for (int slot = 0; slot < 12; slot++) {
                int barrelIndex = shelfSlots[slot];
                if (barrelIndex >= 0) {
                    int x = getShelfSlotPosition(slot);
                    Rectangle r = new Rectangle(x, shelfY, BARREL_WIDTH, BARREL_HEIGHT);
                    if (r.contains(p)) {
                        startDragging(barrelIndex, p, x, shelfY);
                        // Liberar el slot
                        shelfSlots[slot] = -1;
                        return;
                    }
                }
            }

            // Verificar barriles en la balanza
            checkScaleBarrels(p);
        }
    }

    private void startDragging(int index, Point p, int x, int y) {
        dragging = true;
        dragIndex = index;
        dragOffset = new Point(p.x - x, p.y - y);
    }

    private void checkScaleBarrels(Point p) {
        int center = getWidth() / 2;
        int pivotY = 80, armY = pivotY + 30, beam = 100;
        int y = armY + 10;

        // Platillo izquierdo
        if (scaleLeft >= 0) {
            int lx = center - beam - BARREL_WIDTH/2;
            int ly = y - BARREL_HEIGHT;
            Rectangle r = new Rectangle(lx, ly, BARREL_WIDTH, BARREL_HEIGHT);
            if (r.contains(p)) {
                startDragging(scaleLeft, p, lx, ly);
                scaleLeft = -1;
                return;
            }
        }

        // Platillo derecho
        if (scaleRight >= 0) {
            int rx = center + beam - BARREL_WIDTH/2;
            int ry = y - BARREL_HEIGHT;
            Rectangle r = new Rectangle(rx, ry, BARREL_WIDTH, BARREL_HEIGHT);
            if (r.contains(p)) {
                startDragging(scaleRight, p, rx, ry);
                scaleRight = -1;
                return;
            }
        }
    }

    private void onDrag(MouseEvent e) {
        if (!autoMode && dragging) repaint();
    }

    private void onRelease(MouseEvent e) {
        if (!autoMode && dragging && dragIndex >= 0) {
            Point p = e.getPoint();

            // Verificar si cae en algún slot del estante
            if (tryPlaceInShelf(p)) {
                dragging = false;
                dragIndex = -1;
                dragOffset = null;
                repaint();
                return;
            }

            // Verificar si cae en la balanza
            if (tryPlaceInScale(p)) {
                dragging = false;
                dragIndex = -1;
                dragOffset = null;
                repaint();
                return;
            }

            // Verificar si cae en la transportadora
            if (tryPlaceInConveyor(p)) {
                dragging = false;
                dragIndex = -1;
                dragOffset = null;
                repaint();
                return;
            }

            // Si no cae en ninguna zona válida, volver a transportadora
            barrelPositions[dragIndex] = 2;
            dragging = false;
            dragIndex = -1;
            dragOffset = null;
            repaint();
        }
    }

    private boolean tryPlaceInShelf(Point p) {
        int shelfY = getHeight() - BARREL_HEIGHT - 120;
        Rectangle shelfArea = new Rectangle(0, shelfY - 20, getWidth(), BARREL_HEIGHT + 40);

        if (shelfArea.contains(p)) {
            // Encontrar el slot más cercano
            int bestSlot = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int slot = 0; slot < 12; slot++) {
                if (shelfSlots[slot] == -1) { // Slot vacío
                    int slotX = getShelfSlotPosition(slot);
                    int distance = Math.abs(p.x - (slotX + BARREL_WIDTH/2));
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestSlot = slot;
                    }
                }
            }

            if (bestSlot >= 0) {
                shelfSlots[bestSlot] = dragIndex;
                barrelPositions[dragIndex] = 0;
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceInScale(Point p) {
        int center = getWidth() / 2;
        int pivotY = 80, armY = pivotY + 30, beam = 100;
        int margin = 40;

        Rectangle leftPan = new Rectangle(
                center - beam - BARREL_WIDTH/2 - margin,
                armY - BARREL_HEIGHT - margin,
                BARREL_WIDTH + margin*2,
                BARREL_HEIGHT + margin*2
        );
        Rectangle rightPan = new Rectangle(
                center + beam - BARREL_WIDTH/2 - margin,
                armY - BARREL_HEIGHT - margin,
                BARREL_WIDTH + margin*2,
                BARREL_HEIGHT + margin*2
        );

        if (leftPan.contains(p) && scaleLeft == -1) {
            barrelPositions[dragIndex] = 1;
            scaleLeft = dragIndex;
            return true;
        } else if (rightPan.contains(p) && scaleRight == -1) {
            barrelPositions[dragIndex] = 1;
            scaleRight = dragIndex;
            return true;
        }
        return false;
    }

    private boolean tryPlaceInConveyor(Point p) {
        Rectangle conveyorArea = new Rectangle(
                0, getHeight() - BARREL_HEIGHT - 40,
                getWidth(), BARREL_HEIGHT + 40
        );

        if (conveyorArea.contains(p)) {
            barrelPositions[dragIndex] = 2;
            return true;
        }
        return false;
    }

    private void drawWeightText(Graphics g, String text, int x, int y) {
        Font boldFont = new Font("Arial", Font.BOLD, 12);
        g.setFont(boldFont);

        // Contorno negro para el texto
        g.setColor(Color.BLACK);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    g.drawString(text, x + dx, y + dy);
                }
            }
        }

        // Texto principal
        g.setColor(themeColor.equals(Color.WHITE) ? Color.BLACK : themeColor);
        g.drawString(text, x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        drawScale(g);
        drawShelf(g);
        drawConveyor(g);

        // Verificar si la animación ha terminado
        checkIfCompleted();

        // Dibujo del barril siendo arrastrado
        if (!autoMode && dragging && dragIndex >= 0) {
            Point m = getMousePosition();
            if (m != null && dragOffset != null) {
                int x = m.x - dragOffset.x;
                int y = m.y - dragOffset.y;
                drawBarrel(g, x, y, dragIndex, true);
            }
        }
    }

    private void drawBarrel(Graphics g, int x, int y, int barrelIndex, boolean highlight) {
        if (barrelImage != null) {
            g.drawImage(barrelImage, x, y, this);
        } else {
            // Representación simple si no hay imagen
            g.setColor(themeColor);
            g.fillRect(x, y, BARREL_WIDTH, BARREL_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, BARREL_WIDTH, BARREL_HEIGHT);
        }

        if (showWeights) {
            drawWeightText(g, String.valueOf(originalArray[barrelIndex]), x + 20, y - 5);
        }

        if (highlight) {
            g.setColor(Color.RED);
            g.drawRect(x - 2, y - 2, BARREL_WIDTH + 4, BARREL_HEIGHT + 4);
        }
    }

    private void drawScale(Graphics g) {
        int center = getWidth() / 2;
        int pivotY = 80, armY = pivotY + 30, beam = 100;

        // Poste de la balanza
        g.setColor(themeColor.darker());
        g.fillRect(center - 5, pivotY - 50, 10, 100);

        // Brazos de la balanza
        g.setColor(themeColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(center, pivotY, center - beam, armY);
        g2d.drawLine(center, pivotY, center + beam, armY);
        g2d.setStroke(new BasicStroke(1));

        // Platillos de la balanza
        int w = 60, h = 10, y = armY + 10;
        g.setColor(themeColor.brighter());
        g.fillRect(center - beam - w/2, y, w, h);
        g.fillRect(center + beam - w/2, y, w, h);

        g.setColor(themeColor.darker());
        g.drawRect(center - beam - w/2, y, w, h);
        g.drawRect(center + beam - w/2, y, w, h);

        // Inclinación de la balanza según los barriles
        int offL = 0, offR = 0;
        if (scaleLeft >= 0 && scaleRight >= 0) {
            int diff = originalArray[scaleLeft] - originalArray[scaleRight];
            offL = diff > 0 ? 10 : diff < 0 ? -10 : 0;
            offR = -offL;
        }

        // Dibujar barriles en los platillos de la balanza
        if (scaleLeft >= 0) {
            int lx = center - beam - BARREL_WIDTH/2;
            int ly = y - BARREL_HEIGHT + offL;
            drawBarrel(g, lx, ly, scaleLeft, false);
        }
        if (scaleRight >= 0) {
            int rx = center + beam - BARREL_WIDTH/2;
            int ry = y - BARREL_HEIGHT + offR;
            drawBarrel(g, rx, ry, scaleRight, false);
        }
    }

    private void drawShelf(Graphics g) {
        int shelfY = getHeight() - BARREL_HEIGHT - 120;

        // Dibujar el estante
        g.setColor(new Color(139, 69, 19));
        g.fillRect(0, shelfY + BARREL_HEIGHT, getWidth(), 15);

        // Dibujar los slots numerados en el estante
        g.setFont(new Font("Arial", Font.BOLD, 12));
        for (int slot = 0; slot < 12; slot++) {
            int x = getShelfSlotPosition(slot);

            // Dibujar el número del slot
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(slot + 1), x + BARREL_WIDTH/2 - 5, shelfY + BARREL_HEIGHT + 30);

            // Dibujar indicadores para el barril más pequeño (MIN) y más grande (MAX)
            if (slot == 0) { // Slot para el barril más pequeño
                g.setColor(Color.GREEN);
                g.drawString("MIN", x + 15, shelfY - 10);
                g.drawRect(x - 2, shelfY - 2, BARREL_WIDTH + 4, BARREL_HEIGHT + 4);
            } else if (slot == 11) { // Slot para el barril más grande
                g.setColor(Color.RED);
                g.drawString("MAX", x + 15, shelfY - 10);
                g.drawRect(x - 2, shelfY - 2, BARREL_WIDTH + 4, BARREL_HEIGHT + 4);
            } else {
                // Dibujar el contorno del slot
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, shelfY, BARREL_WIDTH, BARREL_HEIGHT);
            }

            // Dibujar el barril en el slot si está presente
            int barrelIndex = shelfSlots[slot];
            if (barrelIndex >= 0) {
                drawBarrel(g, x, shelfY, barrelIndex, false);
            }
        }
    }

    private void drawConveyor(Graphics g) {
        int conveyorY = getHeight() - BARREL_HEIGHT - 10;

        // Dibujar la línea de la transportadora
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, conveyorY + BARREL_HEIGHT, getWidth(), 5);

        // Dibujar los barriles en la transportadora
        for (int i = 0; i < array.length; i++) {
            if (barrelPositions[i] == 2) {
                int x = getConveyorPosition(i);
                drawBarrel(g, x, conveyorY, i, false);
            }
        }
    }

    private int getConveyorPosition(int index) {
        int unorderedIndex = 0;
        for (int i = 0; i < index; i++) {
            if (barrelPositions[i] == 2) {
                unorderedIndex++;
            }
        }
        return unorderedIndex * (BARREL_WIDTH + 10); // Posición horizontal en la transportadora
    }

    private int getShelfSlotPosition(int slot) {
        int totalWidth = 12 * BARREL_WIDTH + 11 * 10; // 12 barriles + 11 espacios
        int startX = (getWidth() - totalWidth) / 2; // Calcular posición horizontal del slot
        return startX + slot * (BARREL_WIDTH + 10);
    }

    private void checkIfCompleted() {
        // Verificar si todos los barriles están en el estante
        boolean allInShelf = true;
        for (int i = 0; i < array.length; i++) {
            if (barrelPositions[i] != 0) { // 0 = estante
                allInShelf = false;
                break;
            }
        }

        if (!allInShelf || victoryShown) {
            return; // Si no todos están en el estante o ya se mostró la victoria, salir
        }

        // Verificar si los barriles están ordenados correctamente
        boolean isCorrectlyOrdered = true;
        int[] shelfValues = new int[12];

        // Llenar un arreglo con los valores de los barriles en el estante
        for (int slot = 0; slot < 12; slot++) {
            int barrelIndex = shelfSlots[slot];
            if (barrelIndex >= 0) {
                shelfValues[slot] = originalArray[barrelIndex];
            } else {
                shelfValues[slot] = Integer.MAX_VALUE; // Los slots vacíos se asignan con el valor máximo
            }
        }

        // Verificar que el arreglo esté ordenado en orden ascendente
        for (int i = 0; i < 11; i++) {
            if (shelfValues[i] != Integer.MAX_VALUE && shelfValues[i + 1] != Integer.MAX_VALUE) {
                if (shelfValues[i] > shelfValues[i + 1]) {
                    isCorrectlyOrdered = false;
                    break;
                }
            }
        }

        // Si los barriles están ordenados, mostrar el mensaje de victoria
        if (isCorrectlyOrdered) {
            victoryShown = true;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "¡Lo has conseguido!\n\nEl Mundo de OOO está agradecido contigo",
                        "¡Victoria!",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new InsertionSortBarrelAnimation(12));
    }

    public void addWindowListener(WindowAdapter windowAdapter) {

    }
}
