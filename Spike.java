/**
 * Write a description of class Spike here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.*;
import java.awt.image.BufferedImage;

public class Spike {
    int x, y;
    public Rectangle hitbox;
    BufferedImage image;
    
    public Spike(int x, int y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.image = img;
        // Hitbox lebih kecil agar adil
        this.hitbox = new Rectangle(x + 10, y + 20, 30, 30);
    }
    
    public void draw(Graphics2D g2) {
        if (image != null) g2.drawImage(image, x, y, 50, 50, null);
        else {
            g2.setColor(Color.RED);
            g2.fillPolygon(new int[]{x, x+25, x+50}, new int[]{y+50, y, y+50}, 3);
        }
    }
}