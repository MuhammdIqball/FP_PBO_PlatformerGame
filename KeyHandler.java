/**
 * Write a description of class KeyHandler here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    
    GamePanel gp;
    public boolean up, down, left, right;
    public boolean isPaused = false;
    
    // Cheat Buffer
    String cheatBuffer = "";

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        cheatBuffer += e.getKeyChar();
        if (cheatBuffer.length() > 4) cheatBuffer = cheatBuffer.substring(cheatBuffer.length()-4);
        
        if (cheatBuffer.equals("help")) {
            if (gp.player != null) {
                gp.player.isFlying = !gp.player.isFlying;
                System.out.println("CHEAT ACTIVATED: FLY MODE " + gp.player.isFlying);
            }
            cheatBuffer = "";
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_ESCAPE) isPaused = !isPaused;
        if (isPaused) return;

        // KONTROL: A, D, SPASI
        if (code == KeyEvent.VK_SPACE) up = true;
        if (code == KeyEvent.VK_S) down = true; // Untuk cheat terbang turun
        if (code == KeyEvent.VK_A) left = true;
        if (code == KeyEvent.VK_D) right = true;
        
        if (code == KeyEvent.VK_R && !gp.isRunning) {
            gp.restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE) up = false;
        if (code == KeyEvent.VK_S) down = false;
        if (code == KeyEvent.VK_A) left = false;
        if (code == KeyEvent.VK_D) right = false;
    }
}