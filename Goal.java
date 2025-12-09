/**
 * Write a description of class Goal here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.*;
import java.awt.image.BufferedImage;

public class Goal {
    int x, y;
    public Rectangle hitbox;
    BufferedImage image;
    
    public Goal(int x, int y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.image = img;
        this.hitbox = new Rectangle(x, y, 50, 50);
    }
    
    public void draw(Graphics2D g2) {
        if (image != null) g2.drawImage(image, x, y, 50, 50, null);
        else {
            g2.setColor(Color.YELLOW);
            g2.fillRect(x, y, 50, 50);
        }
    }
}