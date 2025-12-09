import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {

    public int x, y, width, height;
    public Rectangle hitbox;

    private GamePanel gp;
    private KeyHandler keyH;
    private BufferedImage image;

    private double vx = 0;
    private double vy = 0;
    private double moveSpeed = 3.0;
    private double jumpSpeed = -10.0;
    private double gravity = 0.5;
    private double maxFallSpeed = 12.0;

    private boolean onGround = false;

    public Player(int x, int y, int w, int h, GamePanel gp, KeyHandler keyH) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.gp = gp;
        this.keyH = keyH;
        this.image = gp.imgPlayer;
        this.hitbox = new Rectangle(x, y, w, h);
    }

    public void respawn(int x, int y) {
        this.x = x;
        this.y = y;
        vx = 0;
        vy = 0;
        hitbox.x = x;
        hitbox.y = y;
    }

    public void update() {
        // input horizontal
        vx = 0;
        if (keyH.left)  vx = -moveSpeed;
        if (keyH.right) vx =  moveSpeed;

        // lompat
        if (keyH.jump && onGround) {
            vy = jumpSpeed;
            onGround = false;
        }

        // gravity
        vy += gravity;
        if (vy > maxFallSpeed) vy = maxFallSpeed;

        // gerak horizontal + collision
        x += (int) vx;
        updateHitbox();
        checkHorizontalCollisions();

        // gerak vertical + collision
        y += (int) vy;
        updateHitbox();
        checkVerticalCollisions();
    }

    private void checkHorizontalCollisions() {
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
        for (Platform p : gp.platforms) {
            if (hitbox.intersects(p.hitbox)) {
                if (vy > 0) { // jatuh ke atas platform
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
        for (Platform2 p2 : gp.platforms2) {
            if (hitbox.intersects(p2.hitbox)) {
                if (vy > 0) { // jatuh ke atas platform
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
            g2.drawImage(image, x, y, width, height, null);
        } else {
            g2.setColor(Color.BLUE);
            g2.fillRect(x, y, width, height);
        }
    }
}
