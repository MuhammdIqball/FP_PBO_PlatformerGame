import java.awt.*;
import java.awt.image.BufferedImage;

public class Platform {

    // DIPERTAHANKAN karena LevelManager & Player memakainya
    public enum Type { STATIC, MOVING, CRACKED }

    public int x, y, width, height;
    public Rectangle hitbox;
    public BufferedImage image;
    public Type type;

    // --- VARIABEL UNTUK PLATFORM BERGERAK ---
    public int moveSpeed = 2;
    public int moveRange = 100;
    public boolean movingRight = true;
    private int startX;

    // --- VARIABEL UNTUK PLATFORM RETAK ---
    public boolean isActive = true;
    private int fadeTimer = 0;

    public Platform(int x, int y, int w, int h, BufferedImage img, Type type) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        this.startX = x;
        this.image = img;
        this.type = type;

        this.hitbox = new Rectangle(x, y, w, h);
    }

    public void update() {
        // LOGIKA GERAK PLATFORM MOVING
        if (type == Type.MOVING) {
            if (movingRight) {
                x += moveSpeed;
            } else {
                x -= moveSpeed;
            }

            if (x > startX + moveRange) {
                movingRight = false;
            }
            if (x < startX - moveRange) {
                movingRight = true;
            }
        }

        // LOGIKA PLATFORM RETAK (CRACKED)
        if (type == Type.CRACKED) {
            fadeTimer++;
            if (fadeTimer > 100) {
                isActive = !isActive; // mati/hidup bergantian
                fadeTimer = 0;
            }
        }

        // UPDATE HITBOX
        hitbox.x = x;
        hitbox.y = y;
    }

    public void draw(Graphics2D g2) {
        // Kalau cracked dan sedang “mati”, gambar transparan tipis / outline
        if (type == Type.CRACKED && !isActive) {
            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRect(x, y, width, height);
            return;
        }

        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        } else {
            // fallback kalau gambar null
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, width, height);
        }
    }
}
