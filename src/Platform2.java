import java.awt.*;
import java.awt.image.BufferedImage;

public class Platform2 {

    public int x, y, width, height;
    public Rectangle hitbox;
    private BufferedImage image;

    public Platform2(int x, int y, int w, int h, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.image = img;
        this.hitbox = new Rectangle(x, y, w, h);
    }

    public void update() {
        // platform statis, tidak bergerak
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(x, y, width, height);
        }
    }
}