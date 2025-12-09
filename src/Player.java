import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {

    // Posisi & ukuran
    public int x, y, width, height;
    public Rectangle hitbox;

    // Referensi ke game
    private GamePanel gp;
    private KeyHandler keyH;

    // Fisika
    private double vx = 0;
    private double vy = 0;
    private double moveSpeed = 3.0;
    private double jumpSpeed = -10.0;
    private double gravity = 0.5;
    private double maxFallSpeed = 12.0;

    private boolean onGround = false;

    // Gambar player
    private BufferedImage image;

    // Arah hadap
    private boolean facingRight = true; // default menghadap kanan

    public Player(int x, int y, int w, int h, GamePanel gp, KeyHandler keyH) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.gp = gp;
        this.keyH = keyH;

        this.hitbox = new Rectangle(x, y, w, h);

        // Ambil gambar dari GamePanel
        this.image = gp.imgPlayer; // pastikan sudah di-load di GamePanel.loadImages()
    }

    // Respawn ke posisi tertentu
    public void respawn(int x, int y) {
        this.x = x;
        this.y = y;
        vx = 0;
        vy = 0;
        onGround = false;
        updateHitbox();
    }

    // Versi tanpa argumen kalau masih dipakai di tempat lain
    public void respawn() {
        vx = 0;
        vy = 0;
        onGround = false;
        updateHitbox();
    }

    public void update() {
        // Input horizontal
        vx = 0;

        if (keyH.left) {
            vx = -moveSpeed;
            facingRight = false; // hadap kiri
        }
        if (keyH.right) {
            vx = moveSpeed;
            facingRight = true;  // hadap kanan
        }

        // Lompat
        if (keyH.jump && onGround) {
            vy = jumpSpeed;
            onGround = false;
        }

        // Gravity
        vy += gravity;
        if (vy > maxFallSpeed) vy = maxFallSpeed;

        // Gerak horizontal + collision
        x += (int) Math.round(vx);
        updateHitbox();
        checkHorizontalCollisions();

        // Gerak vertikal + collision
        y += (int) Math.round(vy);
        updateHitbox();
        checkVerticalCollisions();
    }

    private void checkHorizontalCollisions() {
        // Platform utama
        for (Platform p : gp.platforms) {
            if (hitbox.intersects(p.hitbox)) {
                if (vx > 0) {
                    x = p.hitbox.x - width;
                } else if (vx < 0) {
                    x = p.hitbox.x + p.hitbox.width;
                }
                updateHitbox();
            }
        }
    }

    private void checkVerticalCollisions() {
        onGround = false;

        // Platform utama
        for (Platform p : gp.platforms) {
            if (hitbox.intersects(p.hitbox)) {
                if (vy > 0) { // jatuh dari atas
                    y = p.hitbox.y - height;
                    vy = 0;
                    onGround = true;
                } else if (vy < 0) { // nabrak dari bawah
                    y = p.hitbox.y + p.hitbox.height;
                    vy = 0;
                }
                updateHitbox();
            }
        }

        // Platform2
        if (gp.platforms2 != null) {
            for (Platform2 p2 : gp.platforms2) {
                if (hitbox.intersects(p2.hitbox)) {
                    if (vy > 0) {
                        y = p2.hitbox.y - height;
                        vy = 0;
                        onGround = true;
                    }
                    updateHitbox();
                }
            }
        }
    }

    private void updateHitbox() {
        hitbox.x = x;
        hitbox.y = y;
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            if (facingRight) {
                // normal
                g2.drawImage(image, x, y, width, height, null);
            } else {
                // flip horizontal: geser x, width negatif
                g2.drawImage(image, x + width, y, -width, height, null);
            }
        } else {
            // fallback kalau image null
            g2.setColor(Color.BLUE);
            g2.fillRect(x, y, width, height);
        }
    }
}