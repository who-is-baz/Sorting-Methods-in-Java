//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class BucketSort extends JPanel implements MouseListener, MouseMotionListener {
    private static final int PANEL_LEFT_WIDTH = 150;
    private static final int BARREL_W = 70;
    private static final int BARREL_H = 60;
    private static final int SLOT_TOP = 80;
    private static final int SCALE_BASE_Y = 280;
    private static final int SCALE_SIZE = 180;
    private List<Barril> barriles = new ArrayList();
    private Barril sel1;
    private Barril sel2;
    private Barril dragging;
    private int offsetX;
    private int offsetY;
    private int comparaciones;
    private int swaps;
    private Barril barrilIzquierdo = null;
    private Barril barrilDerecho = null;
    private BufferedImage fondo;
    private BufferedImage imgBarril;
    private SwingWorker<Void, Void> sorter;
    private JLabel lblComp = new JLabel("Comparaciones: 0");
    private JLabel lblMov = new JLabel("Movimientos: 0");
    private JButton btnRes = new JButton("Resolver");
    private JButton btnEnd = new JButton("Terminado");
    private JButton btnNew = new JButton("Reiniciar");
    private JComboBox<Integer> comboCnt;

    public BucketSort() {
        this.setLayout((LayoutManager)null);
        this.setPreferredSize(new Dimension(800, 500));
        this.cargarRecursos();
        this.montarControles();
        this.generarBarriles(5);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    private void cargarRecursos() {
        try {
            this.fondo = ImageIO.read(this.getClass().getResource("fondo_cars.jpg"));
            this.imgBarril = ImageIO.read(this.getClass().getResource("tractor_cars.jpg"));
        } catch (IllegalArgumentException | IOException ex) {
            System.err.println("Error cargando imágenes Cars: " + ((Exception)ex).getMessage());
        }

    }

    private void montarControles() {
        this.lblComp.setBounds(160, 10, 200, 20);
        this.lblMov.setBounds(360, 10, 200, 20);
        this.lblComp.setForeground(new Color(255, 120, 120));
        this.lblMov.setForeground(new Color(120, 160, 200));
        this.add(this.lblComp);
        this.add(this.lblMov);
        this.btnRes.setBounds(160, 40, 100, 30);
        this.btnEnd.setBounds(270, 40, 100, 30);
        this.btnNew.setBounds(380, 40, 100, 30);
        this.btnRes.setBackground(new Color(255, 130, 130));
        this.btnRes.setForeground(Color.WHITE);
        this.btnEnd.setBackground(new Color(255, 235, 120));
        this.btnEnd.setForeground(Color.BLACK);
        this.btnNew.setBackground(new Color(130, 170, 210));
        this.btnNew.setForeground(Color.WHITE);
        this.add(this.btnRes);
        this.add(this.btnEnd);
        this.add(this.btnNew);
        this.btnRes.addActionListener((e) -> this.iniciarOrdenacion());
        this.btnNew.addActionListener((e) -> {
            if (this.sorter != null) {
                this.sorter.cancel(true);
            }

            this.generarBarriles((Integer)this.comboCnt.getSelectedItem());
        });
        this.btnEnd.addActionListener((e) -> this.checkManualOrder());
        this.comboCnt = new JComboBox();

        for(int i = 3; i <= 9; ++i) {
            this.comboCnt.addItem(i);
        }

        this.comboCnt.setSelectedItem(5);
        this.comboCnt.setBounds(490, 40, 60, 30);
        this.comboCnt.addActionListener((e) -> {
            if (this.sorter != null) {
                this.sorter.cancel(true);
            }

            this.generarBarriles((Integer)this.comboCnt.getSelectedItem());
        });
        this.add(this.comboCnt);
    }

    private void generarBarriles(int n) {
        this.barriles.clear();
        this.comparaciones = this.swaps = 0;
        this.sel1 = this.sel2 = this.dragging = null;
        this.barrilIzquierdo = this.barrilDerecho = null;
        Random rnd = new Random();
        int baseX0 = 200;
        int y0 = this.getHeight() - 60 - 30;

        for(int i = 0; i < n; ++i) {
            int peso = rnd.nextInt(100) + 1;
            int x = baseX0 + i * 90;
            this.barriles.add(new Barril(peso, x, y0));
        }

        this.actualizarContadores();
        this.repaint();
    }

    private void actualizarContadores() {
        this.lblComp.setText("Comparaciones: " + this.comparaciones);
        this.lblMov.setText("Movimientos:   " + this.swaps);
    }

    private void iniciarOrdenacion() {
        if (this.sorter == null || this.sorter.isDone()) {
            this.sorter = new SwingWorker<Void, Void>() {
                protected Void doInBackground() throws Exception {
                    int n = BucketSort.this.barriles.size();

                    for(int i = 0; i < n - 1; ++i) {
                        for(int j = 0; j < n - i - 1; ++j) {
                            Barril a = (Barril)BucketSort.this.barriles.get(j);
                            Barril b = (Barril)BucketSort.this.barriles.get(j + 1);
                            ++BucketSort.this.comparaciones;
                            SwingUtilities.invokeLater(() -> {
                                BucketSort.this.sel1 = a;
                                BucketSort.this.sel2 = b;
                                BucketSort.this.actualizarContadores();
                                BucketSort.this.repaint();
                            });
                            Thread.sleep(300L);
                            if (a.peso > b.peso) {
                                ++BucketSort.this.swaps;
                                Collections.swap(BucketSort.this.barriles, j, j + 1);
                                int tx = a.x;
                                a.x = b.x;
                                b.x = tx;
                                SwingUtilities.invokeLater(() -> {
                                    BucketSort.this.sel1 = a;
                                    BucketSort.this.sel2 = b;
                                    BucketSort.this.actualizarContadores();
                                    BucketSort.this.repaint();
                                });
                                Thread.sleep(300L);
                            }
                        }
                    }

                    return null;
                }

                protected void done() {
                    BucketSort.this.sel1 = BucketSort.this.sel2 = null;
                    BucketSort.this.repaint();

                    try {
                        this.get();
                    } catch (ExecutionException | InterruptedException var2) {
                    }

                    JOptionPane.showMessageDialog(BucketSort.this, "¡Ka-chiga! Ordenación automática completada", "¡Velocidad!", 1);
                }
            };
            this.sorter.execute();
        }
    }

    private void checkManualOrder() {
        int n = this.barriles.size();
        int areaH = this.getHeight() - 80 - 30;
        int slotSpace = n > 0 ? areaH / n : 0;
        Barril[] slots = new Barril[n];

        for(Barril b : this.barriles) {
            if (b.x < 150) {
                int relY = b.y - 80 + slotSpace / 2;
                int idx = Math.max(0, Math.min(n - 1, relY / slotSpace));
                slots[idx] = b;
            }
        }

        for(int i = 0; i < n; ++i) {
            if (slots[i] == null) {
                JOptionPane.showMessageDialog(this, "¡Oye! Te faltan tractores en los slots.\nColócalos todos antes de terminar.", "¡Espera un momento!", 2);
                return;
            }
        }

        List<Integer> errores = new ArrayList();

        for(int i = 0; i < n - 1; ++i) {
            if (slots[i].peso > slots[i + 1].peso) {
                errores.add(i + 1);
            }
        }

        if (errores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "¡Ka-chiga! ¡Los tractores están en orden perfecto!\n¡Eres más rápido que Lightning McQueen!", "¡Piston Cup para ti!", 1);
        } else {
            JOptionPane.showMessageDialog(this, "¡Rayos! Hay errores en las posiciones: " + String.valueOf(errores) + "\nRevisa esas posiciones como Doc Hudson te enseñó.", "¡A practicar más!", 0);
        }

    }

    private void regresarBarrilAMesa(Barril barril) {
        int baseX = 200;
        int y0 = this.getHeight() - 60 - 30;
        int indice = this.barriles.indexOf(barril);
        barril.x = baseX + indice * 90;
        barril.y = y0;
        if (this.barrilIzquierdo == barril) {
            this.barrilIzquierdo = null;
        }

        if (this.barrilDerecho == barril) {
            this.barrilDerecho = null;
        }

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (this.fondo != null) {
            g.drawImage(this.fondo, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRect(150, 0, this.getWidth() - 150, this.getHeight());
        } else {
            GradientPaint gradient = new GradientPaint(0.0F, 0.0F, new Color(255, 235, 205), (float)this.getWidth(), (float)this.getHeight(), new Color(230, 200, 170));
            g2d.setPaint(gradient);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        GradientPaint shopGradient = new GradientPaint(0.0F, 0.0F, new Color(150, 120, 90), 150.0F, (float)this.getHeight(), new Color(180, 140, 110));
        g2d.setPaint(shopGradient);
        g.fillRect(0, 0, 150, this.getHeight());
        g.setColor(new Color(200, 160, 130));
        g.drawRect(0, 0, 150, this.getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", 1, 14));
        g.drawString("\ud83c\udfc1 DOC'S GARAGE", 15, 25);
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Arrastra tractores aquí", 10, 70);
        int n = this.barriles.size();
        int areaH = this.getHeight() - 80 - 30;
        int slotSpace = n > 0 ? areaH / n : 0;

        for(int i = 0; i < n; ++i) {
            int y = 80 + i * slotSpace;
            GradientPaint slotGradient = new GradientPaint(10.0F, (float)y, new Color(220, 220, 220), 80.0F, (float)(y + 60), new Color(190, 190, 190));
            g2d.setPaint(slotGradient);
            g.fillRect(10, y, 70, 60);
            g.setColor(new Color(160, 160, 160));
            g.drawRect(10, y, 70, 60);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 12));
            g.drawString(String.valueOf(i + 1), 15, y + 15);
        }

        int baseX = 150 + (this.getWidth() - 150) / 2;
        int baseY = 280;
        GradientPaint baseGradient = new GradientPaint((float)(baseX - 140), (float)baseY, new Color(150, 120, 90), (float)(baseX + 140), (float)(baseY + 15), new Color(130, 100, 80));
        g2d.setPaint(baseGradient);
        g.fillRect(baseX - 140, baseY, 280, 15);
        g.setColor(new Color(110, 80, 60));
        g.drawRect(baseX - 140, baseY, 280, 15);
        GradientPaint poleGradient = new GradientPaint((float)(baseX - 8), (float)(baseY - 120), new Color(140, 140, 140), (float)(baseX + 8), (float)baseY, new Color(110, 110, 110));
        g2d.setPaint(poleGradient);
        g.fillRect(baseX - 8, baseY - 120, 16, 120);
        g.setColor(new Color(90, 90, 90));
        g.drawRect(baseX - 8, baseY - 120, 16, 120);
        double inclinacion = (double)0.0F;
        if (this.sel1 != null && this.sel2 != null) {
            inclinacion = this.sel1.peso > this.sel2.peso ? (double)-15.0F : (double)15.0F;
        } else if (this.barrilIzquierdo != null && this.barrilDerecho != null) {
            if (this.barrilIzquierdo.peso > this.barrilDerecho.peso) {
                inclinacion = (double)-20.0F;
            } else if (this.barrilDerecho.peso > this.barrilIzquierdo.peso) {
                inclinacion = (double)20.0F;
            } else {
                inclinacion = (double)0.0F;
            }
        } else if (this.barrilIzquierdo != null) {
            inclinacion = (double)-15.0F;
        } else if (this.barrilDerecho != null) {
            inclinacion = (double)15.0F;
        }

        Graphics2D g2Rotated = (Graphics2D)g2d.create();
        g2Rotated.rotate(Math.toRadians(inclinacion), (double)baseX, (double)(baseY - 60));
        GradientPaint armGradient = new GradientPaint((float)(baseX - 160), (float)(baseY - 65), new Color(130, 130, 130), (float)(baseX + 160), (float)(baseY - 53), new Color(110, 110, 110));
        g2Rotated.setPaint(armGradient);
        g2Rotated.fillRect(baseX - 160, baseY - 65, 320, 12);
        g2Rotated.setStroke(new BasicStroke(2.0F));
        g2Rotated.setColor(new Color(90, 90, 90));
        g2Rotated.drawRect(baseX - 160, baseY - 65, 320, 12);
        GradientPaint plateGradient = new GradientPaint(0.0F, (float)(baseY - 50), new Color(255, 235, 120), 0.0F, (float)(baseY - 38), new Color(220, 190, 100));
        g2Rotated.setPaint(plateGradient);
        g2Rotated.fillOval(baseX - 180, baseY - 50, 60, 12);
        g2Rotated.fillOval(baseX + 120, baseY - 50, 60, 12);
        g2Rotated.setColor(new Color(200, 160, 80));
        g2Rotated.drawOval(baseX - 180, baseY - 50, 60, 12);
        g2Rotated.drawOval(baseX + 120, baseY - 50, 60, 12);
        g2Rotated.drawOval(baseX + 120, baseY - 50, 60, 12);
        g2Rotated.dispose();
        if (this.dragging != null) {
            g.setColor(new Color(180, 255, 180, 100));
            Rectangle cazIzq = new Rectangle(baseX - 110, baseY - 110, 80, 90);
            Rectangle cazDer = new Rectangle(baseX + 30, baseY - 110, 80, 90);
            g.fillRect(cazIzq.x, cazIzq.y, cazIzq.width, cazIzq.height);
            g.fillRect(cazDer.x, cazDer.y, cazDer.width, cazDer.height);
            g.setColor(new Color(100, 180, 100));
            g2d.setStroke(new BasicStroke(3.0F, 1, 1));
            g2d.drawRect(cazIzq.x, cazIzq.y, cazIzq.width, cazIzq.height);
            g2d.drawRect(cazDer.x, cazDer.y, cazDer.width, cazDer.height);
        }

        for(Barril b : this.barriles) {
            if (this.imgBarril != null) {
                g.drawImage(this.imgBarril, b.x, b.y, 70, 60, (ImageObserver)null);
            } else {
                GradientPaint tractorGradient = new GradientPaint((float)b.x, (float)b.y, new Color(120, 200, 120), (float)(b.x + 70), (float)(b.y + 60), new Color(80, 160, 80));
                g2d.setPaint(tractorGradient);
                g.fillRect(b.x, b.y, 70, 60);
                g.setColor(new Color(60, 140, 60));
                g.drawRect(b.x, b.y, 70, 60);
            }
        }

        if (this.barrilIzquierdo != null && this.barrilDerecho != null) {
            GradientPaint msgGradient = new GradientPaint((float)(baseX - 90), (float)(baseY - 140), new Color(255, 255, 255, 230), (float)(baseX + 90), (float)(baseY - 110), new Color(250, 250, 250, 230));
            g2d.setPaint(msgGradient);
            g.fillRoundRect(baseX - 90, baseY - 140, 180, 40, 10, 10);
            g.setColor(new Color(255, 130, 130));
            g2d.setStroke(new BasicStroke(2.0F));
            g2d.drawRoundRect(baseX - 90, baseY - 140, 180, 40, 10, 10);
            Color colorTexto = Color.BLACK;
            String mensaje;
            if (this.barrilIzquierdo.peso > this.barrilDerecho.peso) {
                mensaje = "⬅️ IZQUIERDA MÁS PESADA";
                colorTexto = new Color(255, 120, 120);
            } else if (this.barrilIzquierdo.peso < this.barrilDerecho.peso) {
                mensaje = "MÁS PESADA DERECHA ➡️";
                colorTexto = new Color(120, 160, 200);
            } else {
                mensaje = "\ud83c\udfc1 MISMO PESO \ud83c\udfc1";
                colorTexto = new Color(255, 215, 120);
            }

            g.setColor(colorTexto);
            g.setFont(new Font("Arial", 1, 12));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(mensaje);
            g.drawString(mensaje, baseX - textWidth / 2, baseY - 118);
            g.setColor(new Color(100, 100, 100));
            g.setFont(new Font("Arial", 0, 11));
            String pesos = this.barrilIzquierdo.peso + " vs " + this.barrilDerecho.peso;
            FontMetrics fm2 = g.getFontMetrics();
            int textWidth2 = fm2.stringWidth(pesos);
            g.drawString(pesos, baseX - textWidth2 / 2, baseY - 105);
        }

    }

    public void mousePressed(MouseEvent e) {
        if (this.sorter == null || this.sorter.isDone()) {
            for(Barril b : this.barriles) {
                if (b.getBounds().contains(e.getPoint())) {
                    this.dragging = b;
                    this.offsetX = e.getX() - b.x;
                    this.offsetY = e.getY() - b.y;
                    this.sel1 = this.sel2 = null;
                    ++this.swaps;
                    this.actualizarContadores();
                    break;
                }
            }

        }
    }

    public void mouseDragged(MouseEvent e) {
        if (this.dragging != null) {
            this.dragging.x = e.getX() - this.offsetX;
            this.dragging.y = e.getY() - this.offsetY;
            this.repaint();
        }

    }

    public void mouseReleased(MouseEvent e) {
        if (this.dragging != null) {
            int x = this.dragging.x;
            if (x < 150) {
                int n = this.barriles.size();
                int areaH = this.getHeight() - 80 - 30;
                int slotSpace = n > 0 ? areaH / n : 0;
                int relY = this.dragging.y - 80 + slotSpace / 2;
                int idx = Math.max(0, Math.min(n - 1, relY / slotSpace));
                this.dragging.x = 10;
                this.dragging.y = 80 + idx * slotSpace;
                if (this.barrilIzquierdo == this.dragging) {
                    this.barrilIzquierdo = null;
                }

                if (this.barrilDerecho == this.dragging) {
                    this.barrilDerecho = null;
                }
            } else {
                int baseX = 150 + (this.getWidth() - 150) / 2;
                Rectangle cazIzq = new Rectangle(baseX - 110, 170, 80, 90);
                Rectangle cazDer = new Rectangle(baseX + 30, 170, 80, 90);
                Point puntoSuelta = e.getPoint();
                if (cazIzq.contains(puntoSuelta)) {
                    if (this.barrilIzquierdo != null && this.barrilIzquierdo != this.dragging) {
                        this.regresarBarrilAMesa(this.barrilIzquierdo);
                    }

                    this.barrilIzquierdo = this.dragging;
                    this.dragging.x = cazIzq.x + 10;
                    this.dragging.y = cazIzq.y + 10;
                    if (this.barrilDerecho == this.dragging) {
                        this.barrilDerecho = null;
                    }

                    if (this.barrilDerecho != null) {
                        ++this.comparaciones;
                        this.actualizarContadores();
                    }
                } else if (cazDer.contains(puntoSuelta)) {
                    if (this.barrilDerecho != null && this.barrilDerecho != this.dragging) {
                        this.regresarBarrilAMesa(this.barrilDerecho);
                    }

                    this.barrilDerecho = this.dragging;
                    this.dragging.x = cazDer.x + 10;
                    this.dragging.y = cazDer.y + 10;
                    if (this.barrilIzquierdo == this.dragging) {
                        this.barrilIzquierdo = null;
                    }

                    if (this.barrilIzquierdo != null) {
                        ++this.comparaciones;
                        this.actualizarContadores();
                    }
                } else {
                    this.regresarBarrilAMesa(this.dragging);
                }
            }

            this.dragging = null;
            this.repaint();
        }

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("\ud83c\udfc1 Lightning McQueen's Tractor Sorting Challenge \ud83c\udfc1");
            frame.setDefaultCloseOperation(3);
            frame.setContentPane(new BucketSort());
            frame.pack();
            frame.setLocationRelativeTo((Component)null);
            frame.setVisible(true);
        });
    }

    private static class Barril {
        int peso;
        int x;
        int y;

        Barril(int peso, int x, int y) {
            this.peso = peso;
            this.x = x;
            this.y = y;
        }

        Rectangle getBounds() {
            return new Rectangle(this.x, this.y, 70, 60);
        }
    }
}