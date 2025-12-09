/**
 * Write a description of class Player here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    GamePanel gp;
    int x, y, width=40, height=40;
    int startX, startY;
    
    int speed = 5;
    int vSpeed = 0;
    int gravity = 1;
    int jumpStrength = 20;
    boolean onGround = false;
    
    public boolean isFlying = false;
    Rectangle hitbox;
    BufferedImage image;

    public Player(int startX, int startY, GamePanel gp, BufferedImage img) {
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
        this.gp = gp;
        this.image = img;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public void update() {
        // --- INPUT DARI KEYHANDLER ---
        if (isFlying) {
            if (gp.keyH.up) y -= speed;
            if (gp.keyH.down) y += speed;
            if (gp.keyH.left) x -= speed;
            if (gp.keyH.right) x += speed;
            hitbox.x = x; hitbox.y = y;
            return; 
        }

        if (gp.keyH.left) x -= speed;
        if (gp.keyH.right) x += speed;
        
        hitbox.x = x;
        checkCollisions(true);
        
        if (gp.keyH.up && onGround) {
            vSpeed = -jumpStrength;
            onGround = false;
        }
        
        vSpeed += gravity;
        y += vSpeed;
        
        hitbox.y = y;
        checkCollisions(false);
        
        if (y > gp.screenHeight) {
            gp.loseLife();
            respawn();
        }
    }
    
    private void checkCollisions(boolean isHorizontal) {
        if (!isHorizontal) onGround = false;
        for (Platform p : gp.platforms) {
            if (p.type == Platform.Type.CRACKED && !p.isActive) continue;
            
            if (hitbox.intersects(p.hitbox)) {
                if (isHorizontal) {
                    if (gp.keyH.right) x = p.hitbox.x - width;
                    if (gp.keyH.left) x = p.hitbox.x + p.hitbox.width;
                    hitbox.x = x;
                } else {
                    if (vSpeed > 0) { // Mendarat
                        y = p.hitbox.y - height;
                        vSpeed = 0;
                        onGround = true;
                        
                        // IKUT PLATFORM BERGERAK
                        if (p.type == Platform.Type.MOVING) {
                            if (p.movingRight) x += p.moveSpeed;
                            else x -= p.moveSpeed;
                        }
                    } else if (vSpeed < 0) { 
                        y = p.hitbox.y + p.hitbox.height;
                        vSpeed = 0;
                    }
                    hitbox.y = y;
                }
            }
        }
    }
    
    public void respawn() {
        x = startX; y = startY; vSpeed = 0;
    }
    
    public void draw(Graphics2D g2) {
        if (image != null) g2.drawImage(image, x, y, width, height, null);
        else { g2.setColor(Color.BLUE); g2.fillRect(x, y, width, height); }
    }
}