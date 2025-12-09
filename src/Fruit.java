import java.awt.*;
import java.awt.image.BufferedImage;

public class Fruit {

    public int x, y, width, height;
    public Rectangle hitbox;
    private BufferedImage image;

    // flag: true kalau buah sudah diambil
    public boolean collected = false;

    public Fruit(int x, int y, int w, int h, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.image = img;
        this.hitbox = new Rectangle(x, y, w, h);
    }

    public void update() {
        // kalau nanti buah bisa bergerak, update x/y di sini
        // lalu sync hitbox
        hitbox.x = x;
        hitbox.y = y;
    }

    public void draw(Graphics2D g2) {
        // kalau sudah di-collect, jangan digambar lagi
        if (collected) return;

        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(x, y, width, height);
        }
    }
}
