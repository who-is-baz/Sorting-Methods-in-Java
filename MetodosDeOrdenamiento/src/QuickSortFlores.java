import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class QuickSortFlores extends JFrame {
    // Clase para representar un girasol
    public static class Girasol {
        int id;
        int tamanio;
        // En este constructor creamos el girasol nuevo
        public Girasol(int id, int tamanio) {
            this.id = id;
            this.tamanio = tamanio;
        }
    }

    private ArrayList<Girasol> misGirasoles;
    private int cuantosGirasoles = 5;
    private Random numeroRandom = new Random();

    // Componentes de la ventana principal
    private JPanel panelIzquierdo;
    private JPanel panelDerecho;
    private JButton botonOrdenar;
    private JButton botonMezclar;
    private JComboBox<String> menuCantidad;

    // Colores a utilizar
    private final Color AMARILLO = new Color(255, 215, 0);
    private final Color MARRON = new Color(139, 69, 19);
    private final Color VERDE = new Color(34, 139, 34);
    private final Color NARANJA = new Color(255, 140, 0);

    // En este constructor configuramos toda ventana al crearla
    public QuickSortFlores() {
        misGirasoles = new ArrayList<>(); // Creamos la lista de girasoles vacía
        configurarMiVentana(); // Configuro la ventana
        crearTodosLosComponentes(); // Se crean los componentes como los botones, paneles y así
        crearGirasolesNuevos(); // Se crean los primeros girasoles a mostrar
        setVisible(true); // Hacemos la ventana visible
    }

    // Método para configurar cómo se ve la ventana principal
    private void configurarMiVentana() {
        setTitle("QuickSort (Rápida)");  // Título
        setSize(900, 600); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Se cierra la ventana y se regresa al menú de Métodos de ordenamiento
        setLocationRelativeTo(null);// Centrar la ventana en la pantalla
        getContentPane().setBackground(new Color(255, 255, 255)); // Color de fondo
    }

    // Método para crear todos los componentes
    private void crearTodosLosComponentes() {
        setLayout(new BorderLayout());
        crearPanelIzquierdo(); //Panel izquierdo para controles
        crearPanelDerecho(); // Panel derecho para animación
        // Agregar los paneles a la ventana
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);
    }

    // Crear el panel del lado izquierdo con todos los controles
    private void crearPanelIzquierdo() {
        panelIzquierdo = new JPanel();
        panelIzquierdo.setBackground(Color.WHITE);
        panelIzquierdo.setPreferredSize(new Dimension(200, 600));
        panelIzquierdo.setLayout(new FlowLayout());
        panelIzquierdo.add(Box.createVerticalStrut(20)); // Agregamos un espacio en la parte de arriba

        // Texto que nos indica la cantidad de girasoles
        JLabel explicacion = new JLabel("Cantidad de girasoles:");
        explicacion.setFont(new Font("Arial", Font.PLAIN, 12));
        explicacion.setForeground(MARRON);
        panelIzquierdo.add(explicacion);
        panelIzquierdo.add(Box.createVerticalStrut(10));  // Espacio Pequeño

        crearMenuDesplegable(); // Menú desplegable para elegir la cantidad
        panelIzquierdo.add(Box.createVerticalStrut(30)); // Espacio

        // Crear los botones principales (Ordenar y Mezclar)
        crearBotonesPrincipales();
        panelIzquierdo.add(Box.createVerticalStrut(20)); // Espacio
    }

    // Menú desplegable para seleccionar la cantidad de girasoles
    private void crearMenuDesplegable() {
        String[] opciones = {"3", "4", "5", "6", "7", "8", "9", "10"}; // Creamos las opciones

        // Crear el menú desplegable
        menuCantidad = new JComboBox<>(opciones);
        menuCantidad.setSelectedItem("5");  // Comenzamos con 5
        menuCantidad.setPreferredSize(new Dimension(100, 30));
        menuCantidad.setFont(new Font("Arial", Font.PLAIN, 14));
        menuCantidad.setBackground(Color.WHITE);
        menuCantidad.setForeground(MARRON);

        // Hacer que funcione cuando cambio la selección
        menuCantidad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccionado = (String) menuCantidad.getSelectedItem();
                int nuevaCantidad = Integer.parseInt(seleccionado);
                cambiarCantidadDeGirasoles(nuevaCantidad);
            }
        });
        panelIzquierdo.add(menuCantidad); // Agregar el menú al panel
    }

    // Creamos los botones principales
    private void crearBotonesPrincipales() {
        // Botón para Ordenar
        botonOrdenar = new JButton("Ordenar");
        botonOrdenar.setPreferredSize(new Dimension(150, 35)); // Tamaño del botón
        botonOrdenar.setFont(new Font("Arial", Font.BOLD, 14)); // Letra del botón
        botonOrdenar.setBackground(AMARILLO);
        botonOrdenar.setForeground(MARRON);

        // Hacer que funcione cuando lo presiono
        botonOrdenar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ordenarGirasoles(); // Llamar mi método para ordenar
            }
        });

        // Agregar el botón al panel
        panelIzquierdo.add(botonOrdenar);
        panelIzquierdo.add(Box.createVerticalStrut(10)); // Espacio entre los botones

        // Botón para Mezclar
        botonMezclar = new JButton("Mezclar");
        botonMezclar.setPreferredSize(new Dimension(150, 35));
        botonMezclar.setFont(new Font("Arial", Font.BOLD, 14));
        botonMezclar.setBackground(NARANJA);
        botonMezclar.setForeground(Color.WHITE);

        // Hacer que funcione cuando lo presiono
        botonMezclar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mezclarGirasoles(); // Llamar mi método para mezclar
            }
        });
        panelIzquierdo.add(botonMezclar); // Agregar el botón al panel
    }

    // Crear el panel derecho donde voy a dibujar los girasoles
    private void crearPanelDerecho() {
        // Este panel es el encargado de dibujar
        panelDerecho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);// Primero limpiar el panel
                dibujarTodoMiCampo(g); // Luego dibujar mi campo
            }
        };
        panelDerecho.setBackground(Color.WHITE);
    }

    // Método donde dibujamos la parte central
    private void dibujarTodoMiCampo(Graphics g) {
        // Obtener el tamaño de mi panel
        int ancho = panelDerecho.getWidth();
        int alto = panelDerecho.getHeight();

        // Título
        g.setColor(MARRON);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String titulo = "QuickSort (Rápida)";
        FontMetrics medidas = g.getFontMetrics();
        int anchoTexto = medidas.stringWidth(titulo);
        g.drawString(titulo, (ancho - anchoTexto) / 2, 40);

        dibujarTodosLosGirasoles(g, ancho, alto);

        // Dibujar línea del suelo
        g.setColor(VERDE);
        g.drawLine(50, alto - 50, ancho - 50, alto - 50); // Línea horizontal
    }

    // Dibujar todos los girasoles en el panel
    private void dibujarTodosLosGirasoles(Graphics g, int ancho, int alto) {
        if (misGirasoles.isEmpty()) return; // Si no tengo girasoles, no dibujar nada

        // Calcular dónde poner cada girasol para que estén bien espaciados
        int espacioDisponible = ancho - 100;  // Dejar 50 píxeles a cada lado
        int espacioEntreGirasoles = espacioDisponible / (misGirasoles.size() + 1);

        // Dibujar cada girasol
        for (int i = 0; i < misGirasoles.size(); i++) {
            Girasol girasol = misGirasoles.get(i);

            // Calcular posición X del girasol
            int x = 50 + espacioEntreGirasoles * (i + 1);
            int y = alto - 150;  // Posición Y fija

            dibujarUnGirasol(g, girasol, x, y);
        }
    }

    // Dibujar un girasol individual en una posición específica
    private void dibujarUnGirasol(Graphics g, Girasol girasol, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tamanio = girasol.tamanio;

        // Dibujar el tallo
        g2d.setColor(VERDE);
        g2d.setStroke(new BasicStroke(4)); // Tallo más grueso
        g2d.drawLine(x, y, x, y + 40);

        // Dibujar los pétalos amarillos
        g2d.setColor(new Color(255, 215, 0));
        for (int i = 0; i < 16; i++) {
            // Calcular la posición de cada pétalo
            double angulo = Math.toRadians(i * 22.5); // 22.5 grados entre pétalos
            int distancia = tamanio / 2; // Distancia del centro
            int xPetalo = x + (int)(Math.cos(angulo) * distancia);
            int yPetalo = y + (int)(Math.sin(angulo) * distancia);

            // Dibujar pétalo como óvalo alargado
            int anchoPetalo = tamanio / 12;
            int altoPetalo = tamanio / 5;

            // Rotar el óvalo para que apunte hacia afuera
            g2d.translate(xPetalo, yPetalo);
            g2d.rotate(angulo + Math.PI/2);
            g2d.fillOval(-anchoPetalo/2, -altoPetalo/2, anchoPetalo, altoPetalo);

            // Contorno del pétalo para que se vea más definido
            g2d.setColor(new Color(240, 200, 0));// Amarillo más oscuro
            g2d.drawOval(-anchoPetalo/2, -altoPetalo/2, anchoPetalo, altoPetalo);
            g2d.setColor(new Color(255, 215, 0));// Volver al amarillo normal

            // Volver a la posición original
            g2d.rotate(-(angulo + Math.PI/2));
            g2d.translate(-xPetalo, -yPetalo);
        }

        // Dibujar el centro marrón con textura
        int tamanioCenter = tamanio / 2;

        // Centro principal
        g2d.setColor(new Color(101, 67, 33)); // Marrón oscuro
        g2d.fillOval(x - tamanioCenter/2, y - tamanioCenter/2, tamanioCenter, tamanioCenter);

        // Anillo interior más claro
        g2d.setColor(new Color(139, 69, 19)); // Marrón medio
        int anillo = tamanioCenter - 6;
        g2d.fillOval(x - anillo/2, y - anillo/2, anillo, anillo);

        // Centro más pequeño más oscuro
        g2d.setColor(new Color(80, 50, 25)); // Marrón muy oscuro
        int centroChico = tamanioCenter - 12;
        g2d.fillOval(x - centroChico/2, y - centroChico/2, centroChico, centroChico);

        // Escribir el número del tamaño
        g2d.setColor(MARRON);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        String texto = String.valueOf(tamanio);
        FontMetrics medidas = g2d.getFontMetrics();
        int anchoTexto = medidas.stringWidth(texto);
        g2d.drawString(texto, x - anchoTexto/2, y + 62);
    }

    // Crear girasoles nuevos con tamaños aleatorios
    private void crearGirasolesNuevos() {
        misGirasoles.clear();  // Vaciar la lista actual

        // Crear la cantidad de girasoles que elegí
        for (int i = 0; i < cuantosGirasoles; i++) {
            // Crear tamaño aleatorio entre 20 y 50
            int tamanioAleatorio = numeroRandom.nextInt(31) + 20;  // 31 posibilidades (0-30) + 20 = 20-50

            // Crear el girasol y agregarlo a mi lista
            Girasol nuevoGirasol = new Girasol(i, tamanioAleatorio);
            misGirasoles.add(nuevoGirasol);
        }
        panelDerecho.repaint(); // Redibujar el panel para mostrar los nuevos girasoles
    }

    // Cambiar la cantidad de girasoles
    private void cambiarCantidadDeGirasoles(int nuevaCantidad) {
        cuantosGirasoles = nuevaCantidad;// Guardar la nueva cantidad
        crearGirasolesNuevos();// Crear girasoles nuevos
    }

    // Método para mezclar tipo animación
    private void mezclarGirasoles() {
        // Deshabilitar botones temporalmente para evitar clicks múltiples
        botonMezclar.setEnabled(false);
        botonOrdenar.setEnabled(false);
        botonMezclar.setText("Mezclando...");

        // Hacer 4 cambios con pausa entre cada uno
        for (int vuelta = 0; vuelta < 4; vuelta++) {
            // Cambiar el tamaño de cada girasol
            for (Girasol girasol : misGirasoles) {
                girasol.tamanio = numeroRandom.nextInt(31) + 20;  // Nuevo tamaño aleatorio
            }

            // Redibujar para mostrar los cambios
            panelDerecho.repaint();

            // Pausa simple usando Thread.sleep (solo 500 milisegundos)
            try {
                Thread.sleep(500);  // Medio segundo de pausa
            } catch (InterruptedException e) {
                // Si algo interrumpe el sleep, no pasa nada grave
            }
        }

        // Reactivar botones al terminar
        botonMezclar.setEnabled(true);
        botonOrdenar.setEnabled(true);
        botonMezclar.setText("Mezclar");
    }

    // Método para ordenar los girasoles
    private void ordenarGirasoles() {
        // Deshabilitar botones temporalmente
        botonOrdenar.setEnabled(false);
        botonMezclar.setEnabled(false);
        botonOrdenar.setText("Ordenando...");

        // Ejecutar QuickSort de forma directa
        quickSort(0, misGirasoles.size() - 1);

        // Redibujar para mostrar el resultado final
        panelDerecho.repaint();

        // Reactivar botones
        botonOrdenar.setEnabled(true);
        botonMezclar.setEnabled(true);
        botonOrdenar.setText("Ordenar");
    }

    // Algoritmo QuickSort - método principal
    private void quickSort(int inicio, int fin) {
        // Si no hay elementos que ordenar, salir
        if (inicio >= fin) return;

        // Encontrar dónde queda el pivote después de particionar
        int posicionDelPivote = particionarLista(inicio, fin);

        // Ordenar la parte izquierda (elementos menores al pivote)
        quickSort(inicio, posicionDelPivote - 1);

        // Ordenar la parte derecha (elementos mayores al pivote)
        quickSort(posicionDelPivote + 1, fin);
    }

    // Función de partición para QuickSort
    private int particionarLista(int inicio, int fin) {
        // Tomar el último elemento como pivote (elemento de referencia)
        Girasol pivote = misGirasoles.get(fin);
        int indiceDePequenios = inicio - 1;  // Índice de elementos pequeños

        // Revisar todos los elementos excepto el pivote
        for (int j = inicio; j < fin; j++) {
            Girasol girasolActual = misGirasoles.get(j);

            // Si el girasol actual es menor o igual al pivote
            if (girasolActual.tamanio <= pivote.tamanio) {
                indiceDePequenios++;  // Incrementar índice de pequeños

                // Si están en posiciones diferentes, intercambiarlos
                if (indiceDePequenios != j) {
                    intercambiarGirasoles(indiceDePequenios, j);
                }
            }
        }

        // Poner el pivote en su posición correcta
        if (indiceDePequenios + 1 != fin) {
            intercambiarGirasoles(indiceDePequenios + 1, fin);
        }

        return indiceDePequenios + 1;  // Devolver la posición final del pivote
    }

    // Intercambiar dos girasoles en mi lista
    private void intercambiarGirasoles(int posicion1, int posicion2) {
        Girasol temporal = misGirasoles.get(posicion1);// Guardar temporalmente
        misGirasoles.set(posicion1, misGirasoles.get(posicion2)); // Poner el segundo en el primer lugar
        misGirasoles.set(posicion2, temporal); // Poner el primero en el segundo lugar
    }

    // Método principal
    public static void main(String[] args) {
        new QuickSortFlores();  // Crear mi ventana directamente
    }
}