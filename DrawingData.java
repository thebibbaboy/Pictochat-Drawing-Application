import java.awt.*;
import java.io.*;

//Class to setup pixel input areas alongside color
//Serializable class representing a single drawing action
public class DrawingData implements Serializable {
    public int x, y, size;
    public Color color;

    public DrawingData(int x, int y, Color color, int size) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
    }
}