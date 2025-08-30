import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuOrdenamiento extends JFrame {

    private Image imagenFondo;
    private JLabel titulo;
    private JButton[] botones;
    private final String[] nombresAlgoritmos = {
            "Selection Sort", "Bubble Sort", "Insertion Sort",
            "Merge Sort", "Quick Sort", "Heap Sort",
            "Counting Sort", "Radix Sort", "Bucket Sort"
    };

    public MenuOrdenamiento() {
        setTitle("Visualizador de Métodos de Ordenamiento");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Para mover componentes libremente

        imagenFondo = new ImageIcon(getClass().getResource("/resources/background1.jpg")).getImage();

        FondoPanel fondoPanel = new FondoPanel();
        setContentPane(fondoPanel);

        // Título
        titulo = new JLabel("Métodos de Ordenamiento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);
        titulo.setSize(600, 50);
        titulo.setLocation(-600, -50); // Posición inicial fuera del frame
        fondoPanel.add(titulo);

        // Crear botones ocultos
        botones = new JButton[nombresAlgoritmos.length];
        for (int i = 0; i < nombresAlgoritmos.length; i++) {
            botones[i] = crearBoton(nombresAlgoritmos[i]);
            botones[i].setSize(200, 50);
            botones[i].setLocation(-200, -50); // Posición inicial fuera del frame
            fondoPanel.add(botones[i]);
        }

        // Iniciar animación
        animarEnEspiral();

        setVisible(true);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setBackground(new Color(0, 8, 120));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setBorder(BorderFactory.createLineBorder(new Color(24, 31, 219), 2));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(23, 41, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(0, 8, 120));
            }
        });

        boton.addActionListener(e -> mostrarAlgoritmo(texto));
        return boton;
    }

    private void animarEnEspiral() {
        Timer animTimer = new Timer(10, null);
        final int[] paso = {0};
        final int totalComponentes = botones.length + 1; // 1 por el título
        final Point[] destino = new Point[totalComponentes];

        // Posiciones destino
        destino[0] = new Point(320, 80); // Título
        for (int i = 0; i < botones.length; i++) {
            destino[i + 1] = new Point(380, 160 + i * 60);
        }

        animTimer.addActionListener(new ActionListener() {
            int ticks = 0;
            double angle = 0;
            double radio = 500;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (paso[0] >= totalComponentes) {
                    animTimer.stop();
                    return;
                }

                int x = (int) (getWidth() / 2 + radio * Math.cos(angle));
                int y = (int) (getHeight() / 2 + radio * Math.sin(angle));

                if (paso[0] == 0) {
                    titulo.setLocation(x, y);
                } else {
                    botones[paso[0] - 1].setLocation(x, y);
                }

                angle += 0.1;
                radio *= 0.97;

                // Cuando el radio sea pequeño, fija el componente en su lugar
                if (radio < 10) {
                    if (paso[0] == 0) {
                        titulo.setLocation(destino[0]);
                    } else {
                        botones[paso[0] - 1].setLocation(destino[paso[0]]);
                    }

                    paso[0]++;
                    angle = 0;
                    radio = 500;
                }
            }
        });

        animTimer.start();
    }

    private void mostrarAlgoritmo(String algoritmo) {
        switch (algoritmo) {
            case "Merge Sort":
                //Creado por Jose Oswaldo Tienda Mendoza


                AnimacionOrdenamientoMerge ventana = new AnimacionOrdenamientoMerge(8);
                ventana.setVisible(true);
                this.setVisible(false);
                ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                break;




            case "Quick Sort":

                //Creada por Barbie Viridiana LUmbreras Olvera

                QuickSortFlores bar = new QuickSortFlores();
                bar.setVisible(true);
                this.setVisible(false);
                bar.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                break;

            case "Counting Sort":

                ///Creado por gabriel

                CountingSortVisualizer ventanasort = new CountingSortVisualizer();
                ventanasort.setVisible(true);
                this.setVisible(false);
                ventanasort.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                break;
            case "Selection Sort":

                //Debe ser creado en equipo
                PacManSelectionSort pac = new PacManSelectionSort();
                pac.setVisible(true);
                this.setVisible(false);
                pac.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                break;

            case "Bubble Sort":
                BurbujaGUI bu = new BurbujaGUI();
                bu.setVisible(true);
                this.setVisible(false);

                bu.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                //Debe ser creado ene equipo

                break;

            case "Insertion Sort":

                InsertionSortBarrelAnimation ventanaInsertion = new InsertionSortBarrelAnimation(8);
                ventanaInsertion.setVisible(true);
                this.setVisible(false);

                ventanaInsertion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                break;




            case "Heap Sort":
                InterfazGrafica mon = new InterfazGrafica();
                mon.setVisible(true);
                this.setVisible(false);

                mon.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
                //Debe ser creado por moncada

                break;

            case "Radix Sort":
                // Creado por Myranda
                JFrame myFrame = new VentanaGym();   // <-- usa la clase que extiende JFrame
                myFrame.setVisible(true);
                this.setVisible(false);

                myFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true);            // volver al menú al cerrar
                    }
                });
                break;


            case "Bucket Sort":
                // Creado por Hector
                JFrame hecFrame = new JFrame("Bucket Sort");
                hecFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                hecFrame.setContentPane(new BucketSort()); // BucketSort es JPanel
                hecFrame.pack();                           // usa el preferredSize del panel
                hecFrame.setLocationRelativeTo(null);
                hecFrame.setVisible(true);

                this.setVisible(false);

                hecFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(true); // volver al menú al cerrar
                    }
                });
                break;


            default:
                JOptionPane.showMessageDialog(this, "Algoritmo no disponible aún.");
        }
    }

    // Fondo personalizado
    class FondoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuOrdenamiento::new);
    }
}
