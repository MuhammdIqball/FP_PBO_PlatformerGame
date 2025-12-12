import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {

    public int x, y, width, height;
    public Rectangle hitbox;

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
    private BufferedImage image;
    private boolean facingRight = true;

    public Player(int x, int y, int w, int h, GamePanel gp, KeyHandler keyH) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.gp = gp;
        this.keyH = keyH;
        this.hitbox = new Rectangle(x, y, w, h);
        this.image = gp.imgPlayer; 
    }

    public void respawn(int x, int y) {
        this.x = x;
        this.y = y;
        vx = 0;
        vy = 0;
        onGround = false;
        updateHitbox();
    }

    public void update() {
        // 1. Input Horizontal
        vx = 0;
        if (keyH.left) {
            vx = -moveSpeed;
            facingRight = false;
        }
        if (keyH.right) {
            vx = moveSpeed;
            facingRight = true;
        }

        // 2. Jump
        if (keyH.jump && onGround) {
            vy = jumpSpeed;
            onGround = false;
        }

        // 3. Gravity
        vy += gravity;
        if (vy > maxFallSpeed) vy = maxFallSpeed;

        // 4. Move Horizontal & Collision
        x += (int) Math.round(vx);
        updateHitbox();
        checkHorizontalCollisions();

        // 5. Move Vertical & Collision
        y += (int) Math.round(vy);
        updateHitbox();
        checkVerticalCollisions();

        // 6. BARU: Cek agar tidak keluar layar
        checkScreenBounds();
    }

    private void checkScreenBounds() {
        // Batas Kiri
        if (x < 0) {
            x = 0;
        }
        // Batas Kanan
        if (x + width > gp.screenWidth) {
            x = gp.screenWidth - width;
        }
        // Jangan lupa update hitbox setelah posisi dikoreksi paksa
        updateHitbox();
    }

    private void checkHorizontalCollisions() {
        for (Platform p : gp.platforms) {
            if (hitbox.intersects(p.hitbox)) {
                if (vx > 0) { // gerak kanan -> nabrak sisi kiri platform
                    x = p.hitbox.x - width;
                } else if (vx < 0) { // gerak kiri -> nabrak sisi kanan platform
                    x = p.hitbox.x + p.hitbox.width;
                }
                updateHitbox();
            }
        }
    }

    private void checkVerticalCollisions() {
        onGround = false;
        // Platform Utama
        for (Platform p : gp.platforms) {
            if (hitbox.intersects(p.hitbox)) {
                if (vy > 0) { // jatuh
                    y = p.hitbox.y - height;
                    vy = 0;
                    onGround = true;
                } else if (vy < 0) { // jedot kepala
                    y = p.hitbox.y + p.hitbox.height;
                    vy = 0;
                }
                updateHitbox();
            }
        }
        // Platform Melayang (One-way platform biasa / Solid)
        // Logic ini membuatnya solid box, kalau mau tembus dari bawah perlu logic beda.
        // Di sini saya buat solid box sederhana sesuai request awal.
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

    private void updateHitbox() {
        hitbox.x = x;
        hitbox.y = y;
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            if (facingRight) {
                g2.drawImage(image, x, y, width, height, null);
            } else {
                g2.drawImage(image, x + width, y, -width, height, null);
            }
        } else {
            g2.setColor(Color.BLUE);
            g2.fillRect(x, y, width, height);
        }
    }
}