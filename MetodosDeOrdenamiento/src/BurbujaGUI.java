import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class BurbujaGUI extends JFrame {
    
    static class ItemCuracion {
        String nombre;
        int vida;
        String imagen;
        
        public ItemCuracion(String nombre, int vida, String imagen) {
            this.nombre = nombre;
            this.vida = vida;
            this.imagen = imagen;
        }
        
        @Override
        public String toString() {
            return nombre + " (+" + vida + " HP)";
        }
    }
    
    private ItemCuracion[] items;
    private JPanel panelItems;
    private JButton btnOrdenar, btnReiniciar;
    private boolean ordenando = false;
    
    public BurbujaGUI() {
        initializeItems();
        setupGUI();
    }
    
    private void initializeItems() {
        items = new ItemCuracion[]{
            new ItemCuracion("Botiquín", 200, "botiquin.png"),
            new ItemCuracion("Vendaje", 15, "vendaje.png"),
            new ItemCuracion("Escudo Grande", 50, "escudogrande.png"),
            new ItemCuracion("Pez", 75, "pez.png"),
            new ItemCuracion("Miniescudo", 25, "miniescudo.png"),
        };
    }
    
    private void setupGUI() {
        setTitle("🎮 FORTNITE - ORDENAMIENTO BURBUJA 🎮");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 25, 112));
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(138, 43, 226));
        JLabel titulo = new JLabel("ORGANIZADOR DE ITEMS DE CURACIÓN", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        panelTitulo.add(titulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con items
        panelItems = new JPanel(new FlowLayout());
        panelItems.setBackground(new Color(25, 25, 112));
        actualizarPanelItems();
        
        JScrollPane scrollItems = new JScrollPane(panelItems);
        scrollItems.setPreferredSize(new Dimension(850, 350)); // Aumentar para acomodar imágenes más grandes
        add(scrollItems, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(25, 25, 112));
        
        btnOrdenar = new JButton("🔄 ORDENAR POR HP");
        btnOrdenar.setBackground(new Color(255, 165, 0));
        btnOrdenar.setForeground(Color.BLACK);
        btnOrdenar.setFont(new Font("Arial", Font.BOLD, 14));
        btnOrdenar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ordenando) {
                    ordenarConAnimacion();
                }
            }
        });
        
        btnReiniciar = new JButton("🔄 REINICIAR");
        btnReiniciar.setBackground(new Color(255, 69, 0));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ordenando) {
                    reiniciar();
                }
            }
        });
        
        panelBotones.add(btnOrdenar);
        panelBotones.add(btnReiniciar);
        add(panelBotones, BorderLayout.EAST);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void actualizarPanelItems() {
        panelItems.removeAll();
        
        for (ItemCuracion item : items) {
            JPanel itemPanel = crearPanelItem(item);
            panelItems.add(itemPanel);
        }
        
        panelItems.revalidate();
        panelItems.repaint();
    }
    
    private JPanel crearPanelItem(ItemCuracion item) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(130, 130)); // Aumentar tamaño para las imágenes
        panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        
        // Color según rareza del item
        Color colorFondo = getColorPorHP(item.vida);
        panel.setBackground(colorFondo);
        
        // Cargar imagen real
        JLabel iconLabel = cargarImagenItem(item.imagen);
        panel.add(iconLabel, BorderLayout.CENTER);
        
        // Nombre del item
        JLabel nombreLabel = new JLabel(item.nombre, JLabel.CENTER);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 10));
        nombreLabel.setForeground(Color.WHITE);
        panel.add(nombreLabel, BorderLayout.NORTH);
        
        // HP
        JLabel hpLabel = new JLabel("+" + item.vida + " HP", JLabel.CENTER);
        hpLabel.setFont(new Font("Arial", Font.BOLD, 12));
        hpLabel.setForeground(Color.WHITE);
        panel.add(hpLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel cargarImagenItem(String nombreImagen) {
        try {
            // Primero intentar con el path completo del directorio actual
            File archivoImagen = new File(System.getProperty("user.dir") + File.separator + nombreImagen);
            
            // Si no existe, intentar con solo el nombre de archivo
            if (!archivoImagen.exists()) {
                archivoImagen = new File(nombreImagen);
            }
            
            // Debug: imprimir si el archivo existe
            System.out.println("Buscando imagen: " + nombreImagen);
            System.out.println("Ruta completa: " + archivoImagen.getAbsolutePath());
            System.out.println("¿Existe? " + archivoImagen.exists());
            
            if (archivoImagen.exists()) {
                ImageIcon iconoOriginal = new ImageIcon(archivoImagen.getAbsolutePath());
                
                // Verificar que la imagen se cargó correctamente
                if (iconoOriginal.getIconWidth() > 0 && iconoOriginal.getIconHeight() > 0) {
                    // Redimensionar imagen a tamaño apropiado
                    Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                    ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
                    
                    JLabel label = new JLabel(iconoEscalado, JLabel.CENTER);
                    return label;
                }
            }
            
            // Si llegamos aquí, usar emoji como respaldo
            System.out.println("Usando emoji para: " + nombreImagen);
            JLabel label = new JLabel(getEmojiPorItem(nombreImagen), JLabel.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 40));
            return label;
            
        } catch (Exception e) {
            System.err.println("Error cargando imagen " + nombreImagen + ": " + e.getMessage());
            // En caso de error, usar emoji
            JLabel label = new JLabel(getEmojiPorItem(nombreImagen), JLabel.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 40));
            return label;
        }
    }
    
    private String getEmojiPorItem(String nombreImagen) {
        // Emojis como respaldo si no se encuentra la imagen
        switch (nombreImagen.toLowerCase()) {
            case "vendaje.png":
                return "🩹";
            case "botiquin.png":
                return "🏥";
            case "miniescudo.png":
                return "🛡️";
            case "escudogrande.png":
                return "⚡";
            case "pez.png":
                return "🐟";
            case "fondo.png":
                return "💊";
            default:
                return "💊";
        }
    }
    
    private Color getColorPorHP(int hp) {
        if (hp >= 100) return new Color(255, 215, 0); // Dorado (Legendario)
        else if (hp >= 50) return new Color(128, 0, 128); // Púrpura (Épico)
        else if (hp >= 25) return new Color(0, 100, 255); // Azul (Raro)
        else return new Color(128, 128, 128); // Gris (Común)
    }
    
    private void ordenarConAnimacion() {
        ordenando = true;
        btnOrdenar.setEnabled(false);
        btnReiniciar.setEnabled(false);
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                int n = items.length;
                boolean intercambio;
                
                for (int i = 0; i < n - 1; i++) {
                    intercambio = false;
                    
                    for (int j = 0; j < n - 1 - i; j++) {
                        Thread.sleep(1500); // Pausa para visualizar
                        
                        if (items[j].vida > items[j + 1].vida) {
                            // Intercambiar elementos
                            ItemCuracion temp = items[j];
                            items[j] = items[j + 1];
                            items[j + 1] = temp;
                            intercambio = true;
                            
                            // Actualizar interfaz
                            SwingUtilities.invokeLater(() -> actualizarPanelItems());
                            Thread.sleep(1000);
                        }
                    }
                    
                    if (!intercambio) {
                        break;
                    }
                }
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                // Método vacío ya que no usamos log
            }
            
            @Override
            protected void done() {
                ordenando = false;
                btnOrdenar.setEnabled(true);
                btnReiniciar.setEnabled(true);
                JOptionPane.showMessageDialog(BurbujaGUI.this, 
                    "¡Inventario organizado exitosamente! 🏆", 
                    "Ordenamiento Completo", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        
        worker.execute();
    }
    
    private void reiniciar() {
        initializeItems();
        actualizarPanelItems();
        JOptionPane.showMessageDialog(this, 
            "Sistema reiniciado. Listo para ordenar.", 
            "Sistema Reiniciado", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BurbujaGUI().setVisible(true);
        });
    }
}