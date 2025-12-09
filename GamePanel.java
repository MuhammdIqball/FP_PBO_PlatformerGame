import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    final int screenWidth = 800;
    final int screenHeight = 600;
    final int tileSize = 50;
    
    Thread gameThread;
    
    // --- PISAH TANGGUNG JAWAB ---
    public KeyHandler keyH = new KeyHandler(this);     
    public LevelManager levelMgr = new LevelManager(this); 
    
    public Player player;
    public ArrayList<Platform> platforms = new ArrayList<>();
    public ArrayList<Spike> spikes = new ArrayList<>();
    public Goal goal;
    
    public BufferedImage imgBg, imgPlayer, imgBrick, imgCloud, imgCracked, imgGoal, imgSpike;
    
    int currentLevel = 1;
    int lives = 99;
    int timer = 90 * 60;
    boolean isRunning = true;
    String statusMsg = "";

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        loadImages();              // pastikan gambar di-load sebelum level
        levelMgr.loadLevel(currentLevel);
    }

    /**
     * Helper untuk load satu gambar dan print error jelas kalau gagal.
     */
    private BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("FILE TIDAK DITEMUKAN: " + path);
                return null;
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("GAGAL LOAD IMAGE: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    public void loadImages() {
        // SESUAIKAN nama file ini dengan isi folder assets-mu
        imgBg     = loadImage("assets/background.png");
        imgPlayer = loadImage("assets/player.png");
        imgBrick  = loadImage("assets/brick.png");
        imgCloud  = loadImage("assets/cloud.png");
        imgCracked= loadImage("assets/cracked.png");
        imgGoal   = loadImage("assets/goal.png");
        imgSpike  = loadImage("assets/spike.png");
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void restartGame() {
        currentLevel = 1; lives = 3; isRunning = true; statusMsg = "";
        levelMgr.loadLevel(1);
    }
    
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            statusMsg = "GAME OVER (R to Restart)";
            isRunning = false;
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/60;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while(gameThread != null) {
            update();
            repaint();
            try {
                double remaining = nextDrawTime - System.nanoTime();
                if(remaining < 0) remaining = 0;
                Thread.sleep((long)(remaining/1000000));
                nextDrawTime += drawInterval;
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public void update() {
        if (keyH.isPaused || !isRunning) return;
        
        if (timer > 0) timer--;
        else { statusMsg = "TIME UP!"; isRunning = false; }
        
        if(player != null) player.update();
        for (Platform p : platforms) p.update();
        
        if (goal != null && player != null && player.hitbox.intersects(goal.hitbox)) {
            if (currentLevel < 3) { 
                currentLevel++;
                levelMgr.loadLevel(currentLevel);
            } else {
                statusMsg = "YOU WIN ALL LEVELS!";
                isRunning = false;
            }
        }
        
        for (Spike s : spikes) {
            if (player.hitbox.intersects(s.hitbox)) {
                loseLife();
                player.respawn();
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        if (imgBg != null) {
            g2.drawImage(imgBg, 0, 0, screenWidth, screenHeight, null);
        } else {
            g2.setColor(new Color(135, 206, 235));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }
        
        for (Platform p : platforms) p.draw(g2);
        for (Spike s : spikes) s.draw(g2);
        if (goal != null) goal.draw(g2);
        if (player != null) player.draw(g2);
        
        // UI
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        if (timer < 15 * 60) g2.setColor(Color.RED); else g2.setColor(Color.YELLOW);
        g2.drawString("Time: " + (timer/60), 20, 30);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Lives: " + lives, 20, 55);
        g2.drawString("Level: " + currentLevel, 20, 80);
        
        // PAUSE & STATUS
        if (keyH.isPaused) {
            g2.setColor(new Color(0,0,0,150));
            g2.fillRect(0,0,screenWidth, screenHeight);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("PAUSED", 300, 300);
        }
        
        if (!statusMsg.isEmpty()) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString(statusMsg, 150, 300);
        }
        
        g2.dispose();
    }
}
