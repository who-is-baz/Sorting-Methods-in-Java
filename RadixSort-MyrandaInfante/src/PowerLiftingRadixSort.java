/**
 * PROYECTO FINAL - ALGORITMO RADIX SORT CON TEMA DE GYM
 *
 * Hice este programa para mostrar como funciona el algoritmo Radix Sort
 * pero de una manera mas divertida, como si fuera un gimnasio donde
 * organizamos pesas por peso. Me parecio una buena idea para que no
 * sea tan aburrido ver solo numeros.
 *
 * Materia: Programacion Orientada a Objetos
 * Estudiante: MYRANDA BELEN INFANTE CASTILLO
 * Profesor:
 * Fecha: Agosto 2025
 */

// Aqui importo todas las librerias que necesito para hacer la interfaz
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Esta es mi clase principal donde esta el metodo main
 * Aqui es donde inicia todo el programa
 */
public class PowerLiftingRadixSort {

    /**
     * Metodo main - aqui empieza todo
     * Lo que hace es configurar la ventana y mostrarla
     */
    public static void main(String[] args) {
        // Esto es para que se vea mejor en cada sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si no funciona, no pasa nada, seguimos con el estilo normal
            System.out.println("No se pudo cambiar el estilo, pero no importa");
        }

        // Creo la ventana del gimnasio y la muestro
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VentanaGym().setVisible(true);
            }
        });
    }
}

/**
 * Esta es la ventana principal de mi gimnasio virtual
 * Aqui pongo todos los botones y controles
 */
class VentanaGym extends JFrame {

    // Aqui declaro todos los componentes que voy a usar
    private PanelGym panelGym;                    // El panel donde pasa la accion
    private JButton btnEmpezar;                   // Boton para empezar
    private JButton btnPausar;                    // Boton para pausar
    private JButton btnReiniciar;                 // Boton para reiniciar todo
    private JButton btnSiguiente;                 // Boton para ir paso a paso
    private JSlider velocidad;                    // Para controlar que tan rapido va
    private JLabel estadoActual;                  // Para mostrar que esta pasando
    private JLabel contadorSeries;                // Cuantas veces hemos repetido
    private JLabel contadorMovimientos;           // Cuantos movimientos hemos hecho
    private JLabel digitoActual;                  // Que digito estamos viendo
    private JSpinner numeroPesas;                 // Cuantas pesas queremos
    private JComboBox<String> tipoPesas;          // Que tipo de pesas usar
    private JTextArea instrucciones;              // Para explicar que pasa

    // Variables para controlar la animacion
    private javax.swing.Timer timer;              // Para hacer que se mueva solo
    private boolean pausado = false;              // Si esta pausado o no
    private boolean corriendo = false;            // Si esta corriendo o no

    /**
     * Constructor - aqui configuro toda la ventana
     */
    public VentanaGym() {
        configurarVentana();                      // Configurar propiedades basicas
        crearComponentes();                       // Crear todos los botones y eso
        organizarTodo();                          // Poner todo en su lugar
        conectarEventos();                        // Hacer que los botones funcionen
        estadoActual.setText("Listo para empezar"); // Estado inicial
    }

    /**
     * Aqui configuro como se ve la ventana
     */
    private void configurarVentana() {
        setTitle("POWERLIFTING GYM - Mi Simulador de Radix Sort");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);                       // Tamaño de la ventana
        setLocationRelativeTo(null);              // Centrar en pantalla
        getContentPane().setBackground(new Color(40, 40, 40)); // Fondo oscuro como gym
    }

    /**
     * Aqui creo todos los botones y componentes
     */
    private void crearComponentes() {
        // Creo el panel principal donde va la animacion
        panelGym = new PanelGym();
        panelGym.setVentana(this);                // Le digo cual es su ventana padre

        // Creo los botones con colores que me gustan
        btnEmpezar = crearBoton("EMPEZAR GYM", "Para empezar el ordenamiento", new Color(50, 150, 50));
        btnPausar = crearBoton("PAUSAR", "Para pausar y ver que pasa", new Color(200, 150, 0));
        btnReiniciar = crearBoton("REINICIAR", "Para volver a empezar", new Color(150, 50, 50));
        btnSiguiente = crearBoton("SIGUIENTE", "Para ir de a poquito", new Color(50, 100, 150));

        // Al principio algunos botones no funcionan
        btnPausar.setEnabled(false);
        btnSiguiente.setEnabled(false);

        // Control para la velocidad
        velocidad = new JSlider(1, 10, 5);
        velocidad.setMajorTickSpacing(2);
        velocidad.setMinorTickSpacing(1);
        velocidad.setPaintTicks(true);
        velocidad.setPaintLabels(true);
        velocidad.setBackground(new Color(40, 40, 40));
        velocidad.setForeground(Color.WHITE);

        // Control para cuantas pesas queremos
        numeroPesas = new JSpinner(new SpinnerNumberModel(8, 3, 15, 1));
        numeroPesas.setFont(new Font("Arial", Font.BOLD, 12));

        // Selector de tipo de pesas
        String[] tipos = {"Pesas Normales", "Pesas al Reves", "Pesas Pesadas", "Pesas Iguales"};
        tipoPesas = new JComboBox<>(tipos);
        tipoPesas.setBackground(new Color(60, 60, 60));
        tipoPesas.setForeground(Color.WHITE);

        // Etiquetas para mostrar informacion
        estadoActual = crearEtiqueta("Estado: Listo");
        contadorSeries = crearEtiqueta("Series: 0");
        contadorMovimientos = crearEtiqueta("Movimientos: 0");
        digitoActual = crearEtiqueta("Digito: Ninguno");

        // Area de texto para explicaciones
        instrucciones = new JTextArea(5, 30);
        instrucciones.setEditable(false);
        instrucciones.setWrapStyleWord(true);
        instrucciones.setLineWrap(true);
        instrucciones.setBackground(new Color(30, 30, 30));
        instrucciones.setForeground(Color.WHITE);
        instrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instrucciones.setText(
                "BIENVENIDO A MI GIMNASIO VIRTUAL!\n\n" +
                        "Asi funciona:\n" +
                        "1. Las pesas son los numeros que vamos a ordenar\n" +
                        "2. Los organizamos por digitos (1, 10, 100, etc)\n" +
                        "3. Los ponemos en estantes del 0 al 9\n" +
                        "4. Los recogemos en orden\n" +
                        "5. Repetimos hasta que queden ordenados\n\n" +
                        "Elige cuantas pesas quieres y dale a EMPEZAR!"
        );

        // Timer para la animacion (me costo entender esto)
        timer = new javax.swing.Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelGym.siguientePaso();
            }
        });
    }

    /**
     * Metodo para crear botones que se vean bien
     */
    private JButton crearBoton(String texto, String tooltip, Color color) {
        JButton boton = new JButton(texto);
        boton.setToolTipText(tooltip);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(140, 40));

        // Efecto cuando pones el mouse encima
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    /**
     * Para crear etiquetas de informacion
     */
    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Courier", Font.BOLD, 12));
        etiqueta.setOpaque(true);
        etiqueta.setBackground(new Color(60, 60, 60));
        etiqueta.setForeground(Color.YELLOW);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return etiqueta;
    }

    /**
     * Aqui organizo donde va cada cosa en la ventana
     */
    private void organizarTodo() {
        setLayout(new BorderLayout(10, 10));

        // Panel de arriba con los controles
        JPanel panelArriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelArriba.setBackground(new Color(30, 30, 30));
        panelArriba.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "CONTROLES DEL GYM",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), Color.WHITE));

        panelArriba.add(btnEmpezar);
        panelArriba.add(btnPausar);
        panelArriba.add(btnReiniciar);
        panelArriba.add(btnSiguiente);

        // Separador
        panelArriba.add(new JSeparator(SwingConstants.VERTICAL));

        // Control de velocidad
        JLabel lblVel = new JLabel("Velocidad:");
        lblVel.setForeground(Color.WHITE);
        panelArriba.add(lblVel);
        panelArriba.add(velocidad);

        panelArriba.add(new JSeparator(SwingConstants.VERTICAL));

        // Configuracion
        JLabel lblPesas = new JLabel("Pesas:");
        lblPesas.setForeground(Color.WHITE);
        panelArriba.add(lblPesas);
        panelArriba.add(numeroPesas);

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setForeground(Color.WHITE);
        panelArriba.add(lblTipo);
        panelArriba.add(tipoPesas);

        add(panelArriba, BorderLayout.NORTH);

        // Panel del centro donde pasa la accion
        add(panelGym, BorderLayout.CENTER);

        // Panel de la derecha con estadisticas
        JPanel panelDerecha = new JPanel();
        panelDerecha.setLayout(new BoxLayout(panelDerecha, BoxLayout.Y_AXIS));
        panelDerecha.setBackground(new Color(25, 25, 25));
        panelDerecha.setPreferredSize(new Dimension(200, 0));
        panelDerecha.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "ESTADISTICAS",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11), Color.WHITE));

        panelDerecha.add(Box.createVerticalStrut(10));
        panelDerecha.add(estadoActual);
        panelDerecha.add(Box.createVerticalStrut(5));
        panelDerecha.add(digitoActual);
        panelDerecha.add(Box.createVerticalStrut(5));
        panelDerecha.add(contadorSeries);
        panelDerecha.add(Box.createVerticalStrut(5));
        panelDerecha.add(contadorMovimientos);
        panelDerecha.add(Box.createVerticalStrut(15));

        // Informacion del algoritmo
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setBackground(new Color(40, 40, 40));
        info.setForeground(Color.CYAN);
        info.setText(
                "RADIX SORT:\n\n" +
                        "- No comparativo\n" +
                        "- O(d*(n+k))\n" +
                        "- Estable: SI\n" +
                        "- Rapido para enteros\n\n" +
                        "d = digitos\n" +
                        "n = elementos\n" +
                        "k = rango (0-9)"
        );
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelDerecha.add(info);
        panelDerecha.add(Box.createVerticalGlue());

        add(panelDerecha, BorderLayout.EAST);

        // Panel de abajo con instrucciones
        JPanel panelAbajo = new JPanel(new BorderLayout());
        panelAbajo.setBackground(new Color(30, 30, 30));
        panelAbajo.setPreferredSize(new Dimension(0, 140));
        panelAbajo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "INSTRUCCIONES",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), Color.WHITE));

        JScrollPane scroll = new JScrollPane(instrucciones);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelAbajo.add(scroll, BorderLayout.CENTER);

        add(panelAbajo, BorderLayout.SOUTH);
    }

    /**
     * Aqui conecto los botones con lo que tienen que hacer
     */
    private void conectarEventos() {
        // Boton empezar
        btnEmpezar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                empezarGym();
            }
        });

        // Boton pausar
        btnPausar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pausarGym();
            }
        });

        // Boton reiniciar
        btnReiniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reiniciarGym();
            }
        });

        // Boton siguiente
        btnSiguiente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pausado && corriendo) {
                    panelGym.siguientePaso();
                }
            }
        });

        // Control de velocidad
        velocidad.addChangeListener(e -> {
            int vel = velocidad.getValue();
            int delay = 1200 - (vel * 100); // Mas alto = mas rapido
            timer.setDelay(delay);
        });

        // Selector de numero de pesas
        numeroPesas.addChangeListener(e -> {
            if (!corriendo) {
                int cantidad = (Integer) numeroPesas.getValue();
                int tipo = tipoPesas.getSelectedIndex();
                panelGym.generarPesas(cantidad, tipo);
            }
        });

        // Selector de tipo de pesas
        tipoPesas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!corriendo) {
                    int cantidad = (Integer) numeroPesas.getValue();
                    int tipo = tipoPesas.getSelectedIndex();
                    panelGym.generarPesas(cantidad, tipo);
                }
            }
        });
    }

    /**
     * Para empezar el entrenamiento
     */
    private void empezarGym() {
        if (!corriendo) {
            panelGym.empezarRadixSort();
            corriendo = true;
            pausado = false;

            btnEmpezar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnSiguiente.setEnabled(false);

            timer.start();
            estadoActual.setText("Entrenando...");

            instrucciones.setText(
                    "ENTRENAMIENTO INICIADO!\n\n" +
                            "El algoritmo esta organizando las pesas paso a paso.\n" +
                            "Fijate como:\n" +
                            "1. Primero analiza un digito de cada pesa\n" +
                            "2. Las mueve a estantes segun ese digito\n" +
                            "3. Las recoge en orden\n" +
                            "4. Repite con el siguiente digito\n\n" +
                            "Podes pausar para ver mejor que pasa!"
            );
        }
    }

    /**
     * Para pausar o seguir
     */
    private void pausarGym() {
        if (corriendo) {
            if (pausado) {
                timer.start();
                pausado = false;
                btnPausar.setText("PAUSAR");
                btnSiguiente.setEnabled(false);
                estadoActual.setText("Siguiendo...");
            } else {
                timer.stop();
                pausado = true;
                btnPausar.setText("SEGUIR");
                btnSiguiente.setEnabled(true);
                estadoActual.setText("Pausado");
            }
        }
    }

    /**
     * Para reiniciar todo
     */
    private void reiniciarGym() {
        timer.stop();
        corriendo = false;
        pausado = false;

        panelGym.reiniciar();

        btnEmpezar.setEnabled(true);
        btnPausar.setEnabled(false);
        btnPausar.setText("PAUSAR");
        btnSiguiente.setEnabled(false);

        estadoActual.setText("Reiniciado");
        actualizarEstadisticas(0, 0, "Ninguno");

        instrucciones.setText(
                "GIMNASIO REINICIADO!\n\n" +
                        "Todo volvio al principio.\n" +
                        "Podes cambiar cuantas pesas queres y que tipo.\n" +
                        "Cuando estes listo, dale a EMPEZAR!\n\n" +
                        "Tipos disponibles:\n" +
                        "- Normales: numeros al azar\n" +
                        "- Al reves: de mayor a menor\n" +
                        "- Pesadas: numeros mas grandes\n" +
                        "- Iguales: algunos numeros repetidos"
        );
    }

    /**
     * Para actualizar las estadisticas
     */
    public void actualizarEstadisticas(int series, int movimientos, String digito) {
        contadorSeries.setText("Series: " + series);
        contadorMovimientos.setText("Movimientos: " + movimientos);
        digitoActual.setText("Digito: " + digito);
    }

    /**
     * Para cambiar las instrucciones
     */
    public void cambiarInstrucciones(String texto) {
        instrucciones.setText(texto);
        instrucciones.setCaretPosition(0);
    }

    /**
     * Cuando termina todo
     */
    public void terminado() {
        timer.stop();
        corriendo = false;
        pausado = false;

        btnEmpezar.setEnabled(true);
        btnPausar.setEnabled(false);
        btnPausar.setText("PAUSAR");
        btnSiguiente.setEnabled(false);

        estadoActual.setText("TERMINADO!");

        cambiarInstrucciones(
                "FELICITACIONES!\n\n" +
                        "Todas las pesas estan ordenadas correctamente!\n" +
                        "El algoritmo Radix Sort termino exitosamente.\n\n" +
                        "Que aprendiste:\n" +
                        "- Como funciona el ordenamiento por digitos\n" +
                        "- Que es un algoritmo estable\n" +
                        "- Como se usa la recursion en la practica\n\n" +
                        "Podes probar con diferentes configuraciones!"
        );

        JOptionPane.showMessageDialog(this,
                "ENTRENAMIENTO COMPLETADO!\n\n" +
                        "Todas las pesas fueron organizadas correctamente.\n" +
                        "Revisa las estadisticas y proba con otros numeros!",
                "Gimnasio - Exito!",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * Este es el panel donde pasa toda la accion
 * Aqui dibuje las pesas y los estantes
 */
class PanelGym extends JPanel {

    // Tamaños de las cosas que dibujo
    private static final int ANCHO_PESA = 60;
    private static final int ALTO_PESA = 18;
    private static final int ANCHO_ESTANTE = 80;
    private static final int ALTO_ESTANTE = 100;
    private static final int MARGEN = 20;

    // Listas para guardar las pesas y estantes
    private List<Pesa> pesas;                     // Todas las pesas
    private List<List<Pesa>> estantes;            // 10 estantes (0-9)
    private int digitoActual;                     // Que digito estamos mirando
    private int posicionDigito;                   // 1, 10, 100, etc
    private boolean terminado;                    // Si ya terminamos
    private int contadorSeries;                   // Cuantas veces repetimos
    private int contadorMovimientos;              // Cuantos movimientos

    // Para controlar en que parte del algoritmo estamos
    private EstadoProceso estado;
    private int paso;

    // Referencia a la ventana principal
    private VentanaGym ventana;

    /**
     * Los diferentes estados del proceso
     */
    private enum EstadoProceso {
        INICIAL,              // Al principio
        VIENDO_DIGITO,        // Viendo que digito procesar
        MOVIENDO_PESAS,       // Moviendo a estantes
        JUNTANDO_PESAS,       // Juntando de vuelta
        TERMINADO             // Ya esta todo ordenado
    }

    /**
     * Constructor del panel
     */
    public PanelGym() {
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "AREA DE ENTRENAMIENTO",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        // Inicializo las listas
        pesas = new ArrayList<>();
        estantes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            estantes.add(new ArrayList<>());
        }

        // Estado inicial
        estado = EstadoProceso.INICIAL;
        terminado = false;
        contadorSeries = 0;
        contadorMovimientos = 0;
        paso = 0;

        // Genero pesas iniciales
        generarPesas(8, 0);
    }

    /**
     * Para conectar con la ventana principal
     */
    public void setVentana(VentanaGym v) {
        this.ventana = v;
    }

    /**
     * Para generar nuevas pesas segun lo que eligio el usuario
     */
    public void generarPesas(int cantidad, int tipo) {
        // Limpio todo
        pesas.clear();
        limpiarEstantes();

        Random random = new Random();

        for (int i = 0; i < cantidad; i++) {
            int peso;
            Color color;

            switch (tipo) {
                case 0: // Pesas normales
                    peso = random.nextInt(999) + 1;
                    color = colorRandom();
                    break;

                case 1: // Al reves
                    peso = (cantidad - i) * 10 + random.nextInt(10);
                    float h = (float) i / cantidad;
                    color = Color.getHSBColor(h, 0.8f, 0.9f);
                    break;

                case 2: // Pesadas
                    peso = random.nextInt(9000) + 1000;
                    color = new Color(50, 100, random.nextInt(100) + 150);
                    break;

                case 3: // Algunas iguales
                    int[] pesosComunes = {111, 222, 333, 444, 555};
                    peso = pesosComunes[random.nextInt(pesosComunes.length)];
                    color = peso == 111 ? Color.RED :
                            peso == 222 ? Color.GREEN :
                                    peso == 333 ? Color.BLUE :
                                            peso == 444 ? Color.ORANGE : Color.MAGENTA;
                    break;

                default:
                    peso = random.nextInt(999) + 1;
                    color = Color.GRAY;
            }

            Pesa pesa = new Pesa(peso, color);
            int x = calcularPosX(i);
            int y = 70;
            pesa.setPosInicial(x, y);
            pesa.setPosActual(x, y);
            pesas.add(pesa);
        }

        // Reset del estado
        estado = EstadoProceso.INICIAL;
        terminado = false;
        contadorSeries = 0;
        contadorMovimientos = 0;
        paso = 0;

        repaint();
    }

    /**
     * Calculo donde poner cada pesa al principio
     */
    private int calcularPosX(int indice) {
        int espacio = getWidth() - 2 * MARGEN;
        int cantidad = Math.max(pesas.size(), 1);
        int separacion = espacio / cantidad;
        return MARGEN + indice * separacion + (separacion - ANCHO_PESA) / 2;
    }

    /**
     * Para generar colores bonitos
     */
    private Color colorRandom() {
        Random r = new Random();
        float h = r.nextFloat();
        float s = 0.7f + r.nextFloat() * 0.3f;
        float b = 0.8f + r.nextFloat() * 0.2f;
        return Color.getHSBColor(h, s, b);
    }

    /**
     * Para empezar el algoritmo Radix Sort
     */
    public void empezarRadixSort() {
        // Busco el numero mas grande para saber cuantos digitos tiene
        int maximo = 0;
        for (Pesa p : pesas) {
            if (p.getPeso() > maximo) {
                maximo = p.getPeso();
            }
        }

        digitoActual = 1;
        posicionDigito = 1;
        estado = EstadoProceso.VIENDO_DIGITO;
        paso = 0;

        if (ventana != null) {
            ventana.actualizarEstadisticas(contadorSeries, contadorMovimientos, "Unidades");
        }

        repaint();
    }

    /**
     * Para ir paso a paso en el algoritmo
     */
    public void siguientePaso() {
        switch (estado) {
            case VIENDO_DIGITO:
                verDigito();
                break;
            case MOVIENDO_PESAS:
                moverPesas();
                break;
            case JUNTANDO_PESAS:
                juntarPesas();
                break;
            case TERMINADO:
                if (ventana != null) {
                    ventana.terminado();
                }
                break;
        }
        repaint();
    }

    /**
     * Fase 1: Ver que digito vamos a usar
     */
    private void verDigito() {
        // Marco el digito en cada pesa
        for (Pesa pesa : pesas) {
            int digito = sacarDigito(pesa.getPeso(), posicionDigito);
            pesa.setDigitoMarcado(digito);
        }

        estado = EstadoProceso.MOVIENDO_PESAS;

        if (ventana != null) {
            String nombreDigito = nombreDelDigito(posicionDigito);
            ventana.actualizarEstadisticas(contadorSeries, contadorMovimientos, nombreDigito);
            ventana.cambiarInstrucciones(
                    "VIENDO DIGITO: " + nombreDigito + "\n\n" +
                            "Ahora el algoritmo esta mirando el digito " + nombreDigito.toLowerCase() +
                            " de cada pesa. Fijate que cada pesa tiene un numero amarillo " +
                            "que muestra cual es su digito.\n\n" +
                            "En el proximo paso, las pesas van a moverse a los estantes " +
                            "segun este digito (0 va al estante 0, 1 al estante 1, etc)."
            );
        }
    }

    /**
     * Fase 2: Mover las pesas a los estantes
     */
    private void moverPesas() {
        // Limpio los estantes
        limpiarEstantes();

        // Muevo cada pesa al estante que le corresponde
        for (Pesa pesa : pesas) {
            int digito = sacarDigito(pesa.getPeso(), posicionDigito);
            estantes.get(digito).add(pesa);

            // Calculo donde ponerla en el estante
            int x = calcularPosEstanteX(digito);
            int y = calcularPosEstanteY(digito, estantes.get(digito).size() - 1);

            pesa.setPosDestino(x, y);
            contadorMovimientos++;
        }

        estado = EstadoProceso.JUNTANDO_PESAS;

        if (ventana != null) {
            ventana.actualizarEstadisticas(contadorSeries, contadorMovimientos, nombreDelDigito(posicionDigito));
            ventana.cambiarInstrucciones(
                    "MOVIENDO A ESTANTES\n\n" +
                            "Las pesas se estan moviendo a sus estantes segun su digito:\n" +
                            "- Las que tienen digito 0 van al estante 0\n" +
                            "- Las que tienen digito 1 van al estante 1\n" +
                            "- Y asi hasta el estante 9\n\n" +
                            "Fijate que si varias pesas van al mismo estante, " +
                            "mantienen el orden en que llegaron (por eso es estable).\n\n" +
                            "Movimientos hechos: " + contadorMovimientos
            );
        }
    }

    /**
     * Fase 3: Juntar las pesas de vuelta
     */
    private void juntarPesas() {
        // Creo una nueva lista para juntar todas
        List<Pesa> pesasOrdenadas = new ArrayList<>();

        // Las junto en orden del estante 0 al 9
        for (List<Pesa> estante : estantes) {
            pesasOrdenadas.addAll(estante);
        }

        // Las pongo de vuelta arriba
        for (int i = 0; i < pesasOrdenadas.size(); i++) {
            Pesa pesa = pesasOrdenadas.get(i);
            int x = calcularPosX(i);
            pesa.setPosDestino(x, 70);
            pesa.setDigitoMarcado(-1); // Saco la marca
        }

        pesas = pesasOrdenadas;

        // Veo si tengo que seguir con mas digitos
        posicionDigito *= 10;
        int maximo = 0;
        for (Pesa p : pesas) {
            if (p.getPeso() > maximo) {
                maximo = p.getPeso();
            }
        }

        if (maximo / posicionDigito > 0) {
            // Sigo con el siguiente digito
            estado = EstadoProceso.VIENDO_DIGITO;
            paso++;
        } else {
            // Ya termine!
            estado = EstadoProceso.TERMINADO;
            terminado = true;
        }

        if (ventana != null) {
            ventana.cambiarInstrucciones(
                    "JUNTANDO PESAS\n\n" +
                            "Las pesas se juntan de los estantes en orden (estante 0, " +
                            "luego estante 1, luego estante 2, etc).\n\n" +
                            "Resultado: Las pesas quedan parcialmente ordenadas segun " +
                            "el digito que acabamos de procesar.\n\n" +
                            (terminado ?
                                    "LISTO! Todos los digitos fueron procesados. " +
                                            "Las pesas estan completamente ordenadas!" :
                                    "CONTINUANDO: Ahora vamos a procesar el siguiente digito. " +
                                            "El proceso se repite hasta que esten todas ordenadas.")
            );
        }
    }

    /**
     * Para sacar un digito especifico de un numero
     * Esto me costo entender al principio pero ya se como funciona
     */
    private int sacarDigito(int numero, int posicion) {
        return (numero / posicion) % 10;
    }

    /**
     * Para ponerle nombre al digito
     */
    private String nombreDelDigito(int pos) {
        if (pos == 1) return "Unidades";
        if (pos == 10) return "Decenas";
        if (pos == 100) return "Centenas";
        if (pos == 1000) return "Miles";
        return "Posicion " + pos;
    }

    /**
     * Calculo donde va cada estante
     */
    private int calcularPosEstanteX(int num) {
        int espacio = getWidth() - 2 * MARGEN;
        int separacion = espacio / 10;
        return MARGEN + num * separacion + (separacion - ANCHO_ESTANTE) / 2;
    }

    /**
     * Calculo donde poner cada pesa en el estante
     */
    private int calcularPosEstanteY(int numEstante, int posEnEstante) {
        return 280 + posEnEstante * 20; // Las apilo una arriba de otra
    }

    /**
     * Para limpiar todos los estantes
     */
    private void limpiarEstantes() {
        for (List<Pesa> estante : estantes) {
            estante.clear();
        }
    }

    /**
     * Para reiniciar todo
     */
    public void reiniciar() {
        limpiarEstantes();
        estado = EstadoProceso.INICIAL;
        terminado = false;
        contadorSeries = 0;
        contadorMovimientos = 0;
        paso = 0;

        // Pongo las pesas en su lugar original
        for (int i = 0; i < pesas.size(); i++) {
            Pesa pesa = pesas.get(i);
            int x = calcularPosX(i);
            pesa.setPosActual(x, 70);
            pesa.setDigitoMarcado(-1);
        }

        repaint();
    }

    /**
     * Aqui dibujo todo en la pantalla
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Para que se vea mas suave
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarEstantes(g2);
        dibujarPesas(g2);
        dibujarInfo(g2);

        g2.dispose();
    }

    /**
     * Dibujar los estantes numerados
     */
    private void dibujarEstantes(Graphics2D g2) {
        for (int i = 0; i < 10; i++) {
            int x = calcularPosEstanteX(i);
            int y = 230;

            // Fondo del estante
            g2.setColor(new Color(100, 100, 100, 100));
            g2.fillRoundRect(x, y, ANCHO_ESTANTE, ALTO_ESTANTE, 10, 10);

            // Borde
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, ANCHO_ESTANTE, ALTO_ESTANTE, 10, 10);

            // Numero del estante
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String num = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (ANCHO_ESTANTE - fm.stringWidth(num)) / 2;
            int textY = y + 25;

            g2.setColor(Color.BLACK);
            g2.drawString(num, textX + 1, textY + 1); // Sombra
            g2.setColor(Color.WHITE);
            g2.drawString(num, textX, textY);

            // Cuantas pesas tiene
            if (!estantes.get(i).isEmpty()) {
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String cant = "(" + estantes.get(i).size() + ")";
                g2.setColor(Color.CYAN);
                g2.drawString(cant, x + 5, y + ALTO_ESTANTE - 5);
            }
        }
    }

    /**
     * Dibujar todas las pesas
     */
    private void dibujarPesas(Graphics2D g2) {
        for (Pesa pesa : pesas) {
            dibujarUnaPesa(g2, pesa);
        }
    }

    /**
     * Dibujar una pesa individual
     */
    private void dibujarUnaPesa(Graphics2D g2, Pesa pesa) {
        int x = pesa.getPosActualX();
        int y = pesa.getPosActualY();

        // Animacion suave (esto me gusto como quedo)
        if (pesa.tieneDestino()) {
            int deltaX = pesa.getPosDestinoX() - x;
            int deltaY = pesa.getPosDestinoY() - y;

            if (Math.abs(deltaX) > 2 || Math.abs(deltaY) > 2) {
                x += deltaX * 0.15; // Muevo de a poquito
                y += deltaY * 0.15;
                pesa.setPosActual(x, y);
            } else {
                pesa.setPosActual(pesa.getPosDestinoX(), pesa.getPosDestinoY());
                pesa.limpiarDestino();
            }
        }

        // Sombra
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(x + 2, y + 2, ANCHO_PESA, ALTO_PESA);

        // Pesa principal
        g2.setColor(pesa.getColor());
        g2.fillOval(x, y, ANCHO_PESA, ALTO_PESA);

        // Borde
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(x, y, ANCHO_PESA, ALTO_PESA);

        // Numero de la pesa
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        String peso = String.valueOf(pesa.getPeso());
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (ANCHO_PESA - fm.stringWidth(peso)) / 2;
        int textY = y + (ALTO_PESA + fm.getAscent()) / 2 - 2;

        g2.setColor(Color.BLACK);
        g2.drawString(peso, textX + 1, textY + 1); // Sombra
        g2.setColor(Color.WHITE);
        g2.drawString(peso, textX, textY);

        // Digito marcado si lo hay
        if (pesa.getDigitoMarcado() >= 0) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String digito = String.valueOf(pesa.getDigitoMarcado());
            FontMetrics fmD = g2.getFontMetrics();
            int dX = x + (ANCHO_PESA - fmD.stringWidth(digito)) / 2;
            int dY = y + ALTO_PESA + 15;

            // Fondo amarillo
            g2.fillRoundRect(dX - 5, dY - fmD.getAscent() - 1,
                    fmD.stringWidth(digito) + 10, fmD.getHeight() + 2, 5, 5);

            g2.setColor(Color.BLACK);
            g2.drawString(digito, dX, dY);
        }
    }

    /**
     * Dibujar informacion del estado
     */
    private void dibujarInfo(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.WHITE);

        String info = "";
        switch (estado) {
            case INICIAL:
                info = "GIMNASIO LISTO - Configura y presiona EMPEZAR";
                break;
            case VIENDO_DIGITO:
                info = "ANALIZANDO DIGITOS - Posicion: " + nombreDelDigito(posicionDigito);
                break;
            case MOVIENDO_PESAS:
                info = "ORGANIZANDO PESAS - Moviendo a estantes";
                break;
            case JUNTANDO_PESAS:
                info = "JUNTANDO PESAS - Formando secuencia ordenada";
                break;
            case TERMINADO:
                info = "ENTRENAMIENTO TERMINADO - Todas las pesas organizadas!";
                break;
        }

        g2.setColor(Color.BLACK);
        g2.drawString(info, 16, 27); // Sombra
        g2.setColor(Color.WHITE);
        g2.drawString(info, 15, 25);

        // Info adicional
        if (estado != EstadoProceso.INICIAL && estado != EstadoProceso.TERMINADO) {
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            String extra = "Paso: " + (paso + 1) + " | Procesando: " + nombreDelDigito(posicionDigito);
            g2.setColor(Color.BLACK);
            g2.drawString(extra, 16, 42);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(extra, 15, 40);
        }
    }
}

/**
 * Esta clase representa una pesa individual
 * Tiene peso, color y posicion
 */
class Pesa {
    private final int peso;                       // El numero de la pesa
    private final Color color;                    // Su color

    // Posiciones para la animacion
    private int posInicialX, posInicialY;         // Donde empezo
    private int posActualX, posActualY;           // Donde esta ahora
    private int posDestinoX, posDestinoY;         // Hacia donde va
    private boolean tieneDestino;                 // Si se esta moviendo

    // Para mostrar el digito
    private int digitoMarcado = -1;               // Que digito marcar (-1 = ninguno)

    /**
     * Constructor de la pesa
     */
    public Pesa(int peso, Color color) {
        this.peso = peso;
        this.color = color;
        this.tieneDestino = false;
    }

    // Getters simples
    public int getPeso() { return peso; }
    public Color getColor() { return color; }

    // Manejo de posiciones
    public void setPosInicial(int x, int y) {
        this.posInicialX = x;
        this.posInicialY = y;
    }

    public void setPosActual(int x, int y) {
        this.posActualX = x;
        this.posActualY = y;
    }

    public void setPosDestino(int x, int y) {
        this.posDestinoX = x;
        this.posDestinoY = y;
        this.tieneDestino = true;
    }

    public void limpiarDestino() {
        this.tieneDestino = false;
    }

    public boolean tieneDestino() { return tieneDestino; }

    public int getPosActualX() { return posActualX; }
    public int getPosActualY() { return posActualY; }
    public int getPosDestinoX() { return posDestinoX; }
    public int getPosDestinoY() { return posDestinoY; }

    // Manejo del digito marcado
    public void setDigitoMarcado(int digito) { this.digitoMarcado = digito; }
    public int getDigitoMarcado() { return digitoMarcado; }
}

