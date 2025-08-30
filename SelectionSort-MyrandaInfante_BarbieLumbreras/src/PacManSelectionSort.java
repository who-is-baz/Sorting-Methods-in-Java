import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Clase principal que implementa una animación del algoritmo Selection Sort
 * con temática de Pac-Man usando Java Swing y JFrame.
 *
 * El programa muestra fantasmas de diferentes colores que representan números
 * y Pac-Man los "come" en orden durante el proceso de ordenamiento.
 *
 * @author Estudiante de Programación
 * @version 3.0 - Fantasmas pixelados y controles de velocidad
 */
public class PacManSelectionSort extends JFrame {

    // Constantes para el diseño de la ventana y animación
    private static final int VENTANA_ANCHO = 1000;     // Ancho de la ventana principal
    private static final int VENTANA_ALTO = 750;       // Alto de la ventana principal aumentado
    private static final int FANTASMA_ANCHO = 50;      // Ancho de cada fantasma
    private static final int FANTASMA_ALTO = 60;       // Alto de cada fantasma
    private static final int PACMAN_TAMAÑO = 45;       // Tamaño de Pac-Man

    // Variables para controlar la velocidad de animación
    private int velocidadAnimacion = 1200;              // Milisegundos entre pasos (valor por defecto)
    private static final int VELOCIDAD_MIN = 300;      // Velocidad mínima (más rápido)
    private static final int VELOCIDAD_MAX = 3000;     // Velocidad máxima (más lento)

    // Arreglo que contiene los números a ordenar
    private int[] numeros;

    // Variable para la cantidad de elementos seleccionada por el usuario
    private int cantidadElementos = 5;

    // Variables para controlar el estado del algoritmo
    private int indiceActual = 0;           // Índice actual en el algoritmo
    private int indiceMenor = 0;            // Índice del elemento menor encontrado
    private int indiceComparacion = 0;      // Índice del elemento que se está comparando
    private boolean algoritmoTerminado = false;  // Flag para saber si terminó el ordenamiento
    private boolean animacionEnPausa = false;    // Flag para pausar la animación

    // Variables para estadísticas del algoritmo
    private int totalComparaciones = 0;     // Contador de comparaciones realizadas
    private int comparacionesMaximas = 0;   // Total de comparaciones que se realizarán

    // Posición de Pac-Man en la pantalla
    private int pacmanX = 100;
    private int pacmanY = 350;

    // Variables para la animación de Pac-Man
    private boolean pacmanBocaAbierta = true;
    private int contadorAnimacion = 0;

    // Timer para controlar la animación frame por frame
    private Timer timerAnimacion;

    // Panel personalizado donde se dibuja la animación
    private PanelAnimacion panelDibujo;

    // Componentes de control para la interfaz
    private JButton botonIniciar, botonPausar, botonReiniciar, botonNuevosNumeros;
    private JSpinner selectorCantidad;
    private JSlider sliderVelocidad;        // Control deslizante para velocidad
    private JProgressBar barraProgreso;     // Barra de progreso del algoritmo

    /**
     * Constructor principal que inicializa la ventana y sus componentes
     */
    public PacManSelectionSort() {
        // Configuración básica de la ventana principal
        setTitle("Pac-Man Selection Sort - Algoritmo de Ordenamiento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Cierra el programa al cerrar ventana
        setSize(VENTANA_ANCHO, VENTANA_ALTO);            // Establece el tamaño
        setResizable(false);                             // No permite redimensionar
        setLocationRelativeTo(null);                     // Centra la ventana en pantalla

        // Inicializa el arreglo con números aleatorios
        generarNumerosAleatorios();

        // Calcula el número máximo de comparaciones para la barra de progreso
        calcularComparacionesMaximas();

        // Configura los componentes de la interfaz
        configurarInterfaz();

        // Configura el timer que controla la velocidad de animación
        configurarTimer();

        // Hace visible la ventana
        setVisible(true);
    }

    /**
     * Calcula el número máximo de comparaciones que realizará el algoritmo Selection Sort
     * Fórmula: n*(n-1)/2 donde n es la cantidad de elementos
     */
    private void calcularComparacionesMaximas() {
        int n = numeros.length;
        comparacionesMaximas = n * (n - 1) / 2;  // Fórmula matemática para comparaciones
    }

    /**
     * Genera un arreglo de números aleatorios entre 1 y 99
     * para ser ordenados por el algoritmo
     */
    private void generarNumerosAleatorios() {
        numeros = new int[cantidadElementos];  // Creamos un arreglo con la cantidad seleccionada
        Random generador = new Random();

        // Llena el arreglo con números aleatorios únicos
        for (int i = 0; i < numeros.length; i++) {
            int numeroNuevo;
            boolean numeroRepetido;

            // Genera números hasta encontrar uno que no esté repetido
            do {
                numeroNuevo = generador.nextInt(99) + 1;  // Números del 1 al 99
                numeroRepetido = false;

                // Verifica si el número ya existe en el arreglo
                for (int j = 0; j < i; j++) {
                    if (numeros[j] == numeroNuevo) {
                        numeroRepetido = true;
                        break;
                    }
                }
            } while (numeroRepetido);

            numeros[i] = numeroNuevo;  // Asigna el número único al arreglo
        }
    }

    /**
     * Configura todos los elementos de la interfaz gráfica:
     * panel de dibujo, botones de control y layout general
     */
    private void configurarInterfaz() {
        // Crear el panel principal con BorderLayout
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(Color.BLACK);
        panelTitulo.setPreferredSize(new Dimension(VENTANA_ANCHO, 50));

        JLabel titulo = new JLabel("PAC-MAN SELECTION SORT");
        titulo.setForeground(Color.YELLOW);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTitulo.add(titulo);

        // Panel de animación personalizado
        panelDibujo = new PanelAnimacion();
        panelDibujo.setBackground(Color.BLACK);
        panelDibujo.setPreferredSize(new Dimension(VENTANA_ANCHO, 450));

        // Panel inferior con controles (más alto para incluir más elementos)
        JPanel panelControles = crearPanelControles();

        // Agregar todos los paneles a la ventana principal
        add(panelTitulo, BorderLayout.NORTH);      // Título en la parte superior
        add(panelDibujo, BorderLayout.CENTER);     // Animación en el centro
        add(panelControles, BorderLayout.SOUTH);   // Controles en la parte inferior
    }

    /**
     * Crea el panel con los botones de control de la animación y controles de velocidad
     * @return JPanel configurado con todos los botones y controles
     */
    private JPanel crearPanelControles() {
        // Panel principal que contendrá dos filas de controles
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.DARK_GRAY);
        panelPrincipal.setPreferredSize(new Dimension(VENTANA_ANCHO, 130));

        // Primera fila: controles básicos
        JPanel primeraFila = new JPanel(new FlowLayout());
        primeraFila.setBackground(Color.DARK_GRAY);

        // Selector de cantidad de elementos
        JLabel labelCantidad = new JLabel("Elementos:");
        labelCantidad.setForeground(Color.WHITE);
        labelCantidad.setFont(new Font("Arial", Font.BOLD, 12));

        selectorCantidad = new JSpinner(new SpinnerNumberModel(5, 3, 10, 1));
        selectorCantidad.setPreferredSize(new Dimension(60, 25));
        selectorCantidad.addChangeListener(e -> {
            cantidadElementos = (Integer) selectorCantidad.getValue();
            generarNuevosNumeros();
        });

        // Control de velocidad con slider
        JLabel labelVelocidad = new JLabel("Velocidad:");
        labelVelocidad.setForeground(Color.WHITE);
        labelVelocidad.setFont(new Font("Arial", Font.BOLD, 12));

        // Slider invertido: menor valor = más rápido
        sliderVelocidad = new JSlider(VELOCIDAD_MIN, VELOCIDAD_MAX, velocidadAnimacion);
        sliderVelocidad.setBackground(Color.DARK_GRAY);
        sliderVelocidad.setForeground(Color.WHITE);
        sliderVelocidad.setPreferredSize(new Dimension(200, 30));
        sliderVelocidad.addChangeListener(e -> {
            velocidadAnimacion = sliderVelocidad.getValue();
            if (timerAnimacion != null) {
                timerAnimacion.setDelay(velocidadAnimacion);  // Actualiza la velocidad en tiempo real
            }
        });

        // Etiquetas para el slider de velocidad
        JLabel labelRapido = new JLabel("Rápido");
        labelRapido.setForeground(Color.CYAN);
        labelRapido.setFont(new Font("Arial", Font.PLAIN, 10));

        JLabel labelLento = new JLabel("Lento");
        labelLento.setForeground(Color.CYAN);
        labelLento.setFont(new Font("Arial", Font.PLAIN, 10));

        // Agregar elementos a la primera fila
        primeraFila.add(labelCantidad);
        primeraFila.add(selectorCantidad);
        primeraFila.add(Box.createHorizontalStrut(20));
        primeraFila.add(labelVelocidad);
        primeraFila.add(labelRapido);
        primeraFila.add(sliderVelocidad);
        primeraFila.add(labelLento);

        // Segunda fila: botones de control con colores de fantasmas
        JPanel segundaFila = new JPanel(new FlowLayout());
        segundaFila.setBackground(Color.DARK_GRAY);

        // Botón para iniciar - Color verde (como fantasma verde)
        botonIniciar = new JButton("Iniciar");
        botonIniciar.setBackground(new Color(0, 255, 0));    // Verde brillante
        botonIniciar.setForeground(Color.BLACK);
        botonIniciar.setFont(new Font("Arial", Font.BOLD, 14));
        botonIniciar.addActionListener(e -> iniciarAnimacion());

        // Botón para pausar - Color naranja/rojo (como fantasma rojo)
        botonPausar = new JButton("Pausar");
        botonPausar.setBackground(new Color(255, 69, 0));    // Rojo-naranja
        botonPausar.setForeground(Color.WHITE);
        botonPausar.setFont(new Font("Arial", Font.BOLD, 14));
        botonPausar.setEnabled(false);  // Inicialmente deshabilitado
        botonPausar.addActionListener(e -> pausarReanudarAnimacion());

        // Botón para reiniciar - Color azul (como fantasma azul)
        botonReiniciar = new JButton("Reiniciar");
        botonReiniciar.setBackground(new Color(0, 191, 255)); // Azul cielo
        botonReiniciar.setForeground(Color.BLACK);
        botonReiniciar.setFont(new Font("Arial", Font.BOLD, 14));
        botonReiniciar.addActionListener(e -> reiniciarAnimacion());

        // Botón para nuevos números - Color rosa/magenta (como fantasma rosa)
        botonNuevosNumeros = new JButton("Nuevos Números");
        botonNuevosNumeros.setBackground(new Color(255, 192, 203)); // Rosa
        botonNuevosNumeros.setForeground(Color.BLACK);
        botonNuevosNumeros.setFont(new Font("Arial", Font.BOLD, 14));
        botonNuevosNumeros.addActionListener(e -> generarNuevosNumeros());

        // Agregar botones a la segunda fila
        segundaFila.add(botonIniciar);
        segundaFila.add(botonPausar);
        segundaFila.add(botonReiniciar);
        segundaFila.add(botonNuevosNumeros);

        // Tercera fila: barra de progreso
        JPanel terceraFila = new JPanel(new FlowLayout());
        terceraFila.setBackground(Color.DARK_GRAY);

        JLabel labelProgreso = new JLabel("Progreso del algoritmo:");
        labelProgreso.setForeground(Color.WHITE);
        labelProgreso.setFont(new Font("Arial", Font.BOLD, 12));

        // Barra de progreso para mostrar el avance del algoritmo
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setPreferredSize(new Dimension(400, 20));
        barraProgreso.setStringPainted(true);  // Muestra el porcentaje como texto
        barraProgreso.setString("0%");
        barraProgreso.setForeground(Color.YELLOW);      // Color del texto
        barraProgreso.setBackground(Color.BLACK);       // Color de fondo

        terceraFila.add(labelProgreso);
        terceraFila.add(barraProgreso);

        // Ensamblar el panel principal con las tres filas
        panelPrincipal.add(primeraFila, BorderLayout.NORTH);
        panelPrincipal.add(segundaFila, BorderLayout.CENTER);
        panelPrincipal.add(terceraFila, BorderLayout.SOUTH);

        return panelPrincipal;
    }

    /**
     * Configura el Timer que controla la velocidad de animación
     * Se ejecuta cada velocidadAnimacion milisegundos
     */
    private void configurarTimer() {
        timerAnimacion = new Timer(velocidadAnimacion, e -> {
            if (!animacionEnPausa && !algoritmoTerminado) {
                ejecutarPasoSelectionSort();  // Ejecuta un paso del algoritmo
                contadorAnimacion++;           // Incrementa contador para animación
                actualizarBarraProgreso();     // Actualiza la barra de progreso
                panelDibujo.repaint();         // Redibuja la pantalla
            }
        });
    }

    /**
     * Actualiza la barra de progreso basándose en las comparaciones realizadas
     */
    private void actualizarBarraProgreso() {
        if (comparacionesMaximas > 0) {
            // Calcula el porcentaje de progreso
            int porcentaje = (int) ((totalComparaciones * 100.0) / comparacionesMaximas);
            porcentaje = Math.min(porcentaje, 100); // Asegura que no exceda 100%

            barraProgreso.setValue(porcentaje);
            barraProgreso.setString(porcentaje + "% - Comparaciones: " + totalComparaciones);
        }
    }

    /**
     * Ejecuta un paso del algoritmo Selection Sort
     * Implementa la lógica paso a paso del ordenamiento por selección
     */
    private void ejecutarPasoSelectionSort() {
        // Si ya terminamos de ordenar, detener la animación
        if (indiceActual >= numeros.length - 1) {
            algoritmoTerminado = true;
            timerAnimacion.stop();
            barraProgreso.setValue(100);        // Completar barra de progreso
            barraProgreso.setString("100% - Completado!");
            mostrarMensajeTerminado();
            return;
        }

        // Si acabamos de empezar una nueva iteración
        if (indiceComparacion <= indiceActual) {
            indiceMenor = indiceActual;           // El menor inicial es el actual
            indiceComparacion = indiceActual + 1; // Empezamos comparando con el siguiente

            // Mover Pac-Man hacia la posición actual
            int espacioEntreFan = Math.max(80, (VENTANA_ANCHO - 200) / numeros.length);
            pacmanX = 80 + (indiceActual * espacioEntreFan);
            return;
        }

        // Si no hemos terminado de revisar todos los elementos
        if (indiceComparacion < numeros.length) {
            // Incrementar contador de comparaciones
            totalComparaciones++;

            // Comparar el elemento actual con el menor encontrado hasta ahora
            if (numeros[indiceComparacion] < numeros[indiceMenor]) {
                indiceMenor = indiceComparacion;  // Actualizar el índice del menor
            }
            indiceComparacion++;  // Avanzar al siguiente elemento
        } else {
            // Ya revisamos todos los elementos, hacer el intercambio si es necesario
            if (indiceMenor != indiceActual) {
                intercambiarElementos(indiceActual, indiceMenor);
            }

            // Avanzar a la siguiente posición y reiniciar variables
            indiceActual++;
            indiceComparacion = indiceActual;
            indiceMenor = indiceActual;

            // Mover Pac-Man a la siguiente posición
            if (indiceActual < numeros.length) {
                int espacioEntreFan = Math.max(80, (VENTANA_ANCHO - 200) / numeros.length);
                pacmanX = 80 + (indiceActual * espacioEntreFan);
            }
        }
    }

    /**
     * Intercambia dos elementos del arreglo
     * @param indice1 Primer índice a intercambiar
     * @param indice2 Segundo índice a intercambiar
     */
    private void intercambiarElementos(int indice1, int indice2) {
        int temporal = numeros[indice1];
        numeros[indice1] = numeros[indice2];
        numeros[indice2] = temporal;
    }

    /**
     * Inicia la animación del algoritmo
     */
    private void iniciarAnimacion() {
        if (algoritmoTerminado) {
            reiniciarAnimacion();  // Si ya terminó, reiniciar automáticamente
        }

        timerAnimacion.start();           // Iniciar el timer
        botonIniciar.setEnabled(false);   // Deshabilitar botón iniciar
        botonPausar.setEnabled(true);     // Habilitar botón pausar
        animacionEnPausa = false;
        selectorCantidad.setEnabled(false); // Deshabilitar selector durante animación
        sliderVelocidad.setEnabled(true);   // Mantener slider habilitado para cambios en tiempo real
    }

    /**
     * Pausa o reanuda la animación
     */
    private void pausarReanudarAnimacion() {
        if (animacionEnPausa) {
            // Reanudar animación
            timerAnimacion.start();
            botonPausar.setText("Pausar");
            animacionEnPausa = false;
        } else {
            // Pausar animación
            timerAnimacion.stop();
            botonPausar.setText("Continuar");
            animacionEnPausa = true;
        }
    }

    /**
     * Reinicia la animación a su estado inicial
     */
    private void reiniciarAnimacion() {
        timerAnimacion.stop();             // Detener timer
        indiceActual = 0;                  // Reiniciar índices
        indiceMenor = 0;
        indiceComparacion = 0;
        totalComparaciones = 0;            // Reiniciar contador de comparaciones
        algoritmoTerminado = false;        // Reiniciar flags
        animacionEnPausa = false;
        pacmanX = 100;                     // Posición inicial de Pac-Man
        contadorAnimacion = 0;             // Reiniciar contador de animación

        // Reiniciar barra de progreso
        barraProgreso.setValue(0);
        barraProgreso.setString("0%");

        // Restaurar estado de botones
        botonIniciar.setEnabled(true);
        botonPausar.setEnabled(false);
        botonPausar.setText("Pausar");
        selectorCantidad.setEnabled(true);
        sliderVelocidad.setEnabled(true);

        panelDibujo.repaint();  // Redibujar pantalla
    }

    /**
     * Genera un nuevo conjunto de números aleatorios y reinicia el sistema
     */
    private void generarNuevosNumeros() {
        if (timerAnimacion != null && timerAnimacion.isRunning()) {
            timerAnimacion.stop();        // Detener animación actual
        }
        generarNumerosAleatorios();       // Crear nuevos números
        calcularComparacionesMaximas();   // Recalcular comparaciones máximas
        reiniciarAnimacion();             // Reiniciar estado
    }

    /**
     * Muestra un mensaje cuando el algoritmo termina
     */
    private void mostrarMensajeTerminado() {
        JOptionPane.showMessageDialog(this,
                "¡Algoritmo Selection Sort completado!\n\n" +
                        "Comparaciones realizadas: " + totalComparaciones + "\n" +
                        "Los fantasmas han sido ordenados correctamente.\n" +
                        "Pac-Man ha terminado su trabajo.",
                "Ordenamiento Completado",
                JOptionPane.INFORMATION_MESSAGE);

        // Habilitar controles para permitir reinicio
        botonIniciar.setEnabled(true);
        botonPausar.setEnabled(false);
        selectorCantidad.setEnabled(true);
        sliderVelocidad.setEnabled(true);
    }

    /**
     * Clase interna que representa el panel donde se dibuja la animación
     * Extiende JPanel y sobrescribe el método paintComponent para dibujar
     */
    private class PanelAnimacion extends JPanel {

        /**
         * Método que se ejecuta automáticamente cada vez que se necesita
         * redibujar el panel (cuando se llama a repaint())
         * @param g Objeto Graphics para dibujar en el panel
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);  // Llama al método padre para limpiar el panel

            // Casting a Graphics2D para mejores capacidades de dibujo
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar el fondo del "laberinto" de Pac-Man
            dibujarFondoPacMan(g2d);

            // Dibujar los fantasmas (números del arreglo)
            dibujarFantasmas(g2d);

            // Dibujar a Pac-Man mejorado
            dibujarPacManMejorado(g2d);

            // Dibujar información adicional
            dibujarInformacionEstado(g2d);
        }

        /**
         * Dibuja el fondo estilo laberinto de Pac-Man original
         * @param g2d Objeto Graphics2D para dibujar
         */
        private void dibujarFondoPacMan(Graphics2D g2d) {
            // Fondo negro principal
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Dibujar bordes del laberinto en azul
            g2d.setColor(new Color(0, 100, 255));
            g2d.setStroke(new BasicStroke(4));

            // Borde superior
            g2d.drawLine(20, 20, getWidth() - 20, 20);
            // Borde inferior
            g2d.drawLine(20, getHeight() - 20, getWidth() - 20, getHeight() - 20);
            // Borde izquierdo
            g2d.drawLine(20, 20, 20, getHeight() - 20);
            // Borde derecho
            g2d.drawLine(getWidth() - 20, 20, getWidth() - 20, getHeight() - 20);

            // Líneas horizontales internas del laberinto
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(50, 100, getWidth() - 50, 100);
            g2d.drawLine(50, getHeight() - 100, getWidth() - 50, getHeight() - 100);

            // Líneas verticales decorativas
            for (int i = 150; i < getWidth() - 100; i += 100) {
                g2d.drawLine(i, 80, i, 120);
                g2d.drawLine(i, getHeight() - 120, i, getHeight() - 80);
            }

            // Puntos decorativos amarillos (píldoras de Pac-Man)
            g2d.setColor(Color.YELLOW);
            for (int i = 80; i < getWidth() - 80; i += 40) {
                // Puntos pequeños en la parte superior
                g2d.fillOval(i, 150, 6, 6);
                // Puntos pequeños en la parte inferior
                g2d.fillOval(i, getHeight() - 150, 6, 6);
            }

            // Píldoras de poder (puntos grandes)
            g2d.fillOval(60, 140, 16, 16);
            g2d.fillOval(getWidth() - 80, 140, 16, 16);
            g2d.fillOval(60, getHeight() - 160, 16, 16);
            g2d.fillOval(getWidth() - 80, getHeight() - 160, 16, 16);

            // Línea del suelo donde están los fantasmas
            g2d.setColor(new Color(0, 150, 255));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(60, pacmanY + 70, getWidth() - 60, pacmanY + 70);
        }

        /**
         * Dibuja todos los fantasmas representando los números del arreglo
         * @param g2d Objeto Graphics2D para dibujar
         */
        private void dibujarFantasmas(Graphics2D g2d) {
            // Calcula el espacio disponible para distribuir los fantasmas
            int espacioDisponible = getWidth() - 200;
            int espacioEntreFan = Math.max(80, espacioDisponible / numeros.length);

            for (int i = 0; i < numeros.length; i++) {
                int x = 100 + (i * espacioEntreFan);  // Posición X del fantasma
                int y = pacmanY - 20;                 // Posición Y del fantasma

                // Determinar el color según el estado del algoritmo
                Color colorFantasma = determinarColorFantasma(i);

                // Dibujar el fantasma estilo pixel art de Pac-Man original
                dibujarFantasmaPixelado(g2d, x, y, colorFantasma, numeros[i], i);
            }
        }

        /**
         * Determina el color de un fantasma según su estado en el algoritmo
         * Colores basados en los fantasmas originales de Pac-Man
         * @param indice Índice del fantasma en el arreglo
         * @return Color correspondiente al estado
         */
        private Color determinarColorFantasma(int indice) {
            if (indice < indiceActual) {
                return new Color(255, 184, 82);    // Naranja (Clyde) - Ya ordenado
            } else if (indice == indiceMenor && !algoritmoTerminado) {
                return new Color(0, 255, 0);      // Verde (Sue en algunas versiones) - Menor encontrado
            } else if (indice == indiceComparacion - 1 && indiceComparacion <= numeros.length) {
                return new Color(255, 0, 0);      // Rojo (Blinky) - Comparando
            } else if (indice == indiceActual && !algoritmoTerminado) {
                return new Color(0, 255, 255);    // Cyan (Inky) - Posición actual
            } else {
                return new Color(255, 184, 255);  // Rosa (Pinky) - Sin procesar
            }
        }

        /**
         * Dibuja un fantasma estilo pixel art siguiendo el diseño original de Pac-Man
         * @param g2d Graphics2D para dibujar
         * @param x Posición X del fantasma
         * @param y Posición Y del fantasma
         * @param color Color principal del fantasma
         * @param numero Número que representa el fantasma
         * @param indice Índice del fantasma para determinar efectos especiales
         */
        private void dibujarFantasmaPixelado(Graphics2D g2d, int x, int y, Color color, int numero, int indice) {
            // Tamaño de cada "píxel" para el efecto pixelado
            int tamañoPixel = 2;

            // Dibujar sombra del fantasma
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.fillRect(x + 2, y + 48, FANTASMA_ANCHO, 8);

            // Cuerpo principal del fantasma (parte superior redondeada)
            g2d.setColor(color);

            // Fila 1 (parte superior del "casco")
            g2d.fillRect(x + 12*tamañoPixel, y + 2*tamañoPixel, 26*tamañoPixel, 2*tamañoPixel);

            // Fila 2-3 (ensanchamiento de la cabeza)
            g2d.fillRect(x + 8*tamañoPixel, y + 4*tamañoPixel, 34*tamañoPixel, 4*tamañoPixel);

            // Fila 4-15 (cuerpo principal)
            g2d.fillRect(x + 6*tamañoPixel, y + 8*tamañoPixel, 38*tamañoPixel, 24*tamañoPixel);

            // Fila 16-20 (parte inferior con ondas)
            g2d.fillRect(x + 6*tamañoPixel, y + 32*tamañoPixel, 38*tamañoPixel, 10*tamañoPixel);

            // Crear la parte inferior ondulada (picos del fantasma)
            // Pico 1
            g2d.fillRect(x + 6*tamañoPixel, y + 42*tamañoPixel, 6*tamañoPixel, 4*tamañoPixel);
            // Pico 2
            g2d.fillRect(x + 16*tamañoPixel, y + 42*tamañoPixel, 6*tamañoPixel, 4*tamañoPixel);
            // Pico 3
            g2d.fillRect(x + 26*tamañoPixel, y + 42*tamañoPixel, 6*tamañoPixel, 4*tamañoPixel);
            // Pico 4
            g2d.fillRect(x + 36*tamañoPixel, y + 42*tamañoPixel, 8*tamañoPixel, 4*tamañoPixel);

            // Dibujar los ojos blancos (base)
            g2d.setColor(Color.WHITE);

            // Ojo izquierdo (base blanca)
            g2d.fillRect(x + 12*tamañoPixel, y + 12*tamañoPixel, 8*tamañoPixel, 12*tamañoPixel);

            // Ojo derecho (base blanca)
            g2d.fillRect(x + 28*tamañoPixel, y + 12*tamañoPixel, 8*tamañoPixel, 12*tamañoPixel);

            // Dibujar las pupilas (puntos negros)
            g2d.setColor(Color.BLACK);

            // Determinar dirección de la mirada hacia Pac-Man
            int direccionPupila = 0;
            if (timerAnimacion != null && timerAnimacion.isRunning()) {
                if (pacmanX > x) {
                    direccionPupila = 2*tamañoPixel;  // Mirar a la derecha
                } else if (pacmanX < x) {
                    direccionPupila = -2*tamañoPixel; // Mirar a la izquierda
                }
            }

            // Pupila izquierda
            g2d.fillRect(x + 14*tamañoPixel + direccionPupila, y + 16*tamañoPixel,
                    4*tamañoPixel, 6*tamañoPixel);

            // Pupila derecha
            g2d.fillRect(x + 30*tamañoPixel + direccionPupila, y + 16*tamañoPixel,
                    4*tamañoPixel, 6*tamañoPixel);

            // Si el fantasma está en modo "asustado" (siendo comparado), cambiar expresión
            if (indice == indiceComparacion - 1 && indiceComparacion <= numeros.length &&
                    timerAnimacion != null && timerAnimacion.isRunning()) {

                // Dibujar boca asustada (línea ondulada)
                g2d.setColor(Color.BLACK);
                for (int i = 0; i < 4; i++) {
                    int bocaX = x + 16*tamañoPixel + i*4*tamañoPixel;
                    int bocaY = y + 26*tamañoPixel + (i % 2) * 2*tamañoPixel;
                    g2d.fillRect(bocaX, bocaY, 2*tamañoPixel, 2*tamañoPixel);
                }
            }

            // Dibujar el número del fantasma con efecto pixelado
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int anchoTexto = fm.stringWidth(String.valueOf(numero));

            // Sombra del texto para mejor contraste
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(numero),
                    x + (FANTASMA_ANCHO - anchoTexto) / 2 + 1,
                    y + FANTASMA_ALTO - 6);

            // Texto principal del número
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(numero),
                    x + (FANTASMA_ANCHO - anchoTexto) / 2,
                    y + FANTASMA_ALTO - 7);
        }

        /**
         * Dibuja a Pac-Man mejorado con animación de boca estilo pixel art
         * @param g2d Graphics2D para dibujar
         */
        private void dibujarPacManMejorado(Graphics2D g2d) {
            // Tamaño de píxel para Pac-Man
            int tamañoPixel = 2;

            // Dibujar sombra de Pac-Man
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval(pacmanX + 3, pacmanY + 3, PACMAN_TAMAÑO, PACMAN_TAMAÑO);

            // Color base de Pac-Man
            g2d.setColor(Color.YELLOW);

            // Dibujar el cuerpo circular de Pac-Man con píxeles
            for (int fila = 0; fila < PACMAN_TAMAÑO; fila += tamañoPixel) {
                for (int col = 0; col < PACMAN_TAMAÑO; col += tamañoPixel) {
                    // Calcular distancia desde el centro para hacer forma circular
                    int centroX = PACMAN_TAMAÑO / 2;
                    int centroY = PACMAN_TAMAÑO / 2;
                    double distancia = Math.sqrt(Math.pow(col - centroX, 2) + Math.pow(fila - centroY, 2));

                    // Si está dentro del radio, dibujar píxel
                    if (distancia <= PACMAN_TAMAÑO / 2 - 2) {
                        g2d.fillRect(pacmanX + col, pacmanY + fila, tamañoPixel, tamañoPixel);
                    }
                }
            }

            // Animación de la boca
            if (timerAnimacion != null && timerAnimacion.isRunning()) {
                // La boca se abre y cierra cada 2 frames
                pacmanBocaAbierta = (contadorAnimacion / 2) % 2 == 0;
            }

            // Dibujar la boca de Pac-Man
            g2d.setColor(Color.BLACK);
            if (pacmanBocaAbierta) {
                // Boca abierta (triángulo pixelado)
                int[] bocaX = {
                        pacmanX + PACMAN_TAMAÑO,
                        pacmanX + PACMAN_TAMAÑO - 16,
                        pacmanX + PACMAN_TAMAÑO
                };
                int[] bocaY = {
                        pacmanY + 12,
                        pacmanY + PACMAN_TAMAÑO/2,
                        pacmanY + 30
                };
                g2d.fillPolygon(bocaX, bocaY, 3);
            } else {
                // Boca cerrada (línea horizontal gruesa)
                g2d.fillRect(pacmanX + PACMAN_TAMAÑO - 4, pacmanY + PACMAN_TAMAÑO/2 - 1,
                        4, 2*tamañoPixel);
            }

            // Dibujar el ojo de Pac-Man con estilo pixelado
            g2d.setColor(Color.BLACK);
            g2d.fillRect(pacmanX + 12, pacmanY + 8, 6*tamañoPixel, 6*tamañoPixel);

            // Brillo en el ojo
            g2d.setColor(Color.WHITE);
            g2d.fillRect(pacmanX + 15, pacmanY + 10, 2*tamañoPixel, 2*tamañoPixel);
        }

        /**
         * Dibuja información sobre el estado actual del algoritmo
         * @param g2d Graphics2D para dibujar
         */
        private void dibujarInformacionEstado(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));

            // Información del estado actual
            String estadoTexto = "";
            if (algoritmoTerminado) {
                estadoTexto = "¡ORDENAMIENTO COMPLETADO!";
            } else if (animacionEnPausa) {
                estadoTexto = "ANIMACION PAUSADA";
            } else if (timerAnimacion != null && timerAnimacion.isRunning()) {
                estadoTexto = String.format("Ordenando posición %d de %d - Comparación %d",
                        indiceActual + 1, numeros.length, totalComparaciones);
            } else {
                estadoTexto = "Presiona INICIAR para comenzar";
            }

            // Centrar el texto en la parte superior
            FontMetrics fm = g2d.getFontMetrics();
            int anchoTexto = fm.stringWidth(estadoTexto);
            g2d.drawString(estadoTexto, (getWidth() - anchoTexto) / 2, 60);

            // Leyenda de colores actualizada con los colores de fantasmas originales
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            int yLeyenda = getHeight() - 60;

            // Naranja - Ordenado (Clyde)
            g2d.setColor(new Color(255, 184, 82));
            g2d.fillRect(50, yLeyenda, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Ordenado", 70, yLeyenda + 12);

            // Verde - Menor (Sue)
            g2d.setColor(new Color(0, 255, 0));
            g2d.fillRect(150, yLeyenda, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Menor", 170, yLeyenda + 12);

            // Cyan - Actual (Inky)
            g2d.setColor(new Color(0, 255, 255));
            g2d.fillRect(220, yLeyenda, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Actual", 240, yLeyenda + 12);

            // Rojo - Comparando (Blinky)
            g2d.setColor(new Color(255, 0, 0));
            g2d.fillRect(290, yLeyenda, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Comparando", 310, yLeyenda + 12);

            // Rosa - Sin procesar (Pinky)
            g2d.setColor(new Color(255, 184, 255));
            g2d.fillRect(400, yLeyenda, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Sin procesar", 420, yLeyenda + 12);

            // Mostrar el arreglo actual en la parte inferior
            StringBuilder arregloTexto = new StringBuilder("Arreglo: [");
            for (int i = 0; i < numeros.length; i++) {
                arregloTexto.append(numeros[i]);
                if (i < numeros.length - 1) {
                    arregloTexto.append(", ");
                }
            }
            arregloTexto.append("]");

            g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
            fm = g2d.getFontMetrics();
            int anchoArreglo = fm.stringWidth(arregloTexto.toString());
            g2d.drawString(arregloTexto.toString(), (getWidth() - anchoArreglo) / 2, getHeight() - 15);
        }
    }

    /**
     * Método main - Punto de entrada del programa
     * Crea una instancia de la aplicación en el hilo de eventos de Swing
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Ejecutar la aplicación en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Establecer el Look and Feel del sistema para una mejor apariencia
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si no se puede establecer el Look and Feel, continuar con el por defecto
                System.out.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
            }

            // Crear y mostrar la aplicación principal
            new PacManSelectionSort();
        });
    }
}