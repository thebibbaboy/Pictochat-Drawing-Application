//Enhanced DrawingPanel.java with thread-safe stroke access and erase mode
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;

//GUI panel responsible for user drawing and rendering graphics
public class DrawingPanel extends JPanel {
    private final List<DrawingData> strokes = new ArrayList<>();
    //Stream to send serialized DrawingData
    private ObjectOutputStream out;

    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private int lastX = -1, lastY = -1;
    private boolean eraseMode = false;

    private JSlider redSlider, greenSlider, blueSlider, sizeSlider;
    private JPanel colorPreview;
    private JButton eraserButton;

    //Stream to send serialized DrawingData
    public DrawingPanel(ObjectOutputStream out) {
        this.out = out;
        setLayout(new BorderLayout());

        //Top panel with sliders and eraser
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        redSlider = createSlider("Red", 0);
        greenSlider = createSlider("Green", 0);
        blueSlider = createSlider("Blue", 0);
        sizeSlider = new JSlider(1, 50, brushSize);
        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);

        colorPreview = new JPanel();
        colorPreview.setBackground(currentColor);
        colorPreview.setPreferredSize(new Dimension(50, 50));

        eraserButton = new JButton("Erase Mode");
        eraserButton.addActionListener(e -> {
                eraseMode = !eraseMode;
                eraserButton.setBackground(eraseMode ? Color.LIGHT_GRAY : null);
            });

        controlPanel.add(new JLabel("Brush Color: "));
        controlPanel.add(redSlider);
        controlPanel.add(greenSlider);
        controlPanel.add(blueSlider);
        controlPanel.add(colorPreview);
        controlPanel.add(new JLabel("Brush Size: "));
        controlPanel.add(sizeSlider);
        controlPanel.add(eraserButton);
        add(controlPanel, BorderLayout.NORTH);

        //This method takes the pixels from the array list and applies it to the canvas
        JPanel drawArea = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    synchronized (strokes) {
                        //Apply pixel data from the list of strokes 
                        for (DrawingData stroke : strokes) {
                            g.setColor(stroke.color);
                            g.fillOval(stroke.x, stroke.y, stroke.size, stroke.size);
                        }
                    }
                }
            };
        drawArea.setBackground(Color.WHITE);
        drawArea.addMouseMotionListener(new MouseMotionAdapter() {
                //Triggered when the mouse is dragged to draw lines
                //Setup mouse events
                public void mouseDragged(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();

                    if (lastX != -1 && lastY != -1) {
                        double distance = Point.distance(lastX, lastY, x, y);
                        int steps = (int)(distance / 1.5);
                        for (int i = 1; i <= steps; i++) {
                            int ix = lastX + (x - lastX) * i / steps;
                            int iy = lastY + (y - lastY) * i / steps;
                            drawAndSend(ix, iy);
                        }
                    } else {
                        drawAndSend(x, y);
                    }
                    lastX = x;
                    lastY = y;
                }
            });
        drawArea.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    lastX = -1;
                    lastY = -1;
                }
            });
        add(drawArea, BorderLayout.CENTER);

        ChangeListener updateSettings = e -> {
                currentColor = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
                brushSize = sizeSlider.getValue();
                colorPreview.setBackground(currentColor);
            };
        redSlider.addChangeListener(updateSettings);
        greenSlider.addChangeListener(updateSettings);
        blueSlider.addChangeListener(updateSettings);
        sizeSlider.addChangeListener(updateSettings);
    }

    //Get the drawing data and send it to the server and the pixel array above.
    private void drawAndSend(int x, int y) {
        Color drawColor = eraseMode ? Color.WHITE : currentColor;
        //Call method from DrawingData
        DrawingData stroke = new DrawingData(x, y, drawColor, brushSize);
        synchronized (strokes) {
            strokes.add(stroke);
        }
        repaint();
        try {
            out.writeObject(stroke);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Method to make sliders 
    private JSlider createSlider(String name, int init) {
        JSlider slider = new JSlider(0, 255, init);
        slider.setMajorTickSpacing(85);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    //Adding strokes to list
    public void addRemoteStroke(DrawingData stroke) {
        synchronized (strokes) {
            strokes.add(stroke);
        }
        repaint();
    }
}