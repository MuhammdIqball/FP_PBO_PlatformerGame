import java.awt.*;
import java.awt.image.BufferedImage;

public class Spike {

    public int x, y, width, height;
    public Rectangle hitbox;
    private BufferedImage image;

    public Spike(int x, int y, int w, int h, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.image = img;
        this.hitbox = new Rectangle(x, y, w, h);
    }

    public void update() {
        // statis
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            // Gambar digambar seukuran width/height yang di-pass saat new Spike(...)
            // Jadi kalau width=20, gambar akan mengecil jadi 20px
            g2.drawImage(image, x, y, width, height, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(x, y, width, height);
        }
    }
}