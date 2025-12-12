import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    public final int screenWidth = 800;
    public final int screenHeight = 600;
    public final int tileSize = 50;

    // LEVEL 1: Tutorial Dasar (Tetap sama)
    private String[] level1 = {
        "................G",  
        "................G",  
        "................G",  
        "................G",  
        ".........FFF....G",  
        "..XPPPPPPGGGP...G", 
        "..G.........F...G", 
        "............P...G",  
        ".....FF.....F...G",  
        "....PGG..PGGG...G", 
        "J.........GGG...G",  
        "GGG.G..GG.GGG.GGG",  
        "GGGSSSSSSSSSSSGGG"   
    };

// LEVEL 2 REVISI: Platform lebih rendah agar bisa dilompati
    private String[] level2 = {
        "................", 
        ".............X..", // Row 1: Goal
        "............GGG.", // Row 2: Pijakan Goal
        ".........P.P....", // Row 3: Spike kecil
        "......PPP.s.....", // Row 4: Platform Atas (Tujuan akhir sebelum goal)
        "....PP..........", // Row 5
        "..PP............", // Row 6: Spike kecil
        ".....P..PPP...s.", // Row 7: Platform Kanan (Lompatan kedua)
        "...PP.......PP..", // Row 8
        "..P.............", // Row 9: [PENTING] Platform Kiri DITURUNKAN ke sini agar sampai
        "J..S...S..S.S..G", // Row 10: Player Start
        "GGGGGGGGGGGGGGGG"  // Row 11: Tanah
    };

    // LEVEL 3: REVISI (Sesuai gambar: Tangga "Floor is Lava")
    private String[] level3 = {
        "................", 
        "........X.......", 
        ".....PP.......PP.", // Goal di puncak kiri
        "PPP.......PP....", // Tangga 5 (Teratas)
        ".....PP.........", 
        "........PP......", // Tangga 4
        "....PP..........", 
        ".......PP..P..PP", // Tangga 3
        ".............P..", 
        "..J..P.PP.PPP...", // Tangga 2
        ".PPP............", // Start di pojok kanan bawah
        "SSSSSSSSSSSSSSSS"  // Lantai penuh duri (Mati kalau jatuh)
    };

    private int currLvl = 1;
    private int maxLvl = 3;
    private int spawnY = 0;
    private int spawnX = 0;

    private Thread gameThread;
    public KeyHandler keyH = new KeyHandler(this);

    //Object List
    public Player player;
    public ArrayList<Platform> platforms = new ArrayList<>();
    public ArrayList<Spike> spikes = new ArrayList<>();
    public ArrayList<Platform2> platforms2 = new ArrayList<>();
    public ArrayList<Fruit> fruit = new ArrayList<>();
    public Goal goal;

    public BufferedImage imgBg, imgPlayer, imgPlatform, imgSpike, imgGoal, imgPlatform2, imgFruit;

    public int lives = 3;
    public int score = 0;
    public boolean isRunning = true;
    public boolean isPaused = false;
    public String statusMsg = "";

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        loadImages();   // load semua asset
        
        // Load level 1 dulu untuk inisialisasi awal
        loadLevelFromText(level1); 
        
        // Player dibuat SETELAH loadLevel supaya tahu spawnX/spawnY dari map
        if (player == null) {
            player = new Player(spawnX, spawnY, 26, 40, this, keyH); 
        }
    }

    // Game Function
    public void restartGame() {
        lives = 3;
        score = 0;
        isRunning = true;
        isPaused = false;
        statusMsg = "";
        currLvl = 1;
        loadCurrentLevel();
    }

    private void loadCurrentLevel() {
        switch (currLvl) {
            case 1 -> loadLevelFromText(level1);
            case 2 -> loadLevelFromText(level2);
            case 3 -> loadLevelFromText(level3);
            default -> {
                statusMsg = "YOU WIN ALL LEVELS! (R to Restart)";
                isRunning = false;
            }
        }
    }

    public void nextLevel() {
        currLvl++;
        if (currLvl > maxLvl) {
            statusMsg = "YOU WIN ALL LEVELS! (R to Restart)";
            isRunning = false;
            return;
        }
        loadCurrentLevel(); 
    }

    // Assets Manager
    private BufferedImage loadImage(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                System.err.println("FILE TIDAK DITEMUKAN: " + path);
                return null;
            }
            return ImageIO.read(f);
        } catch (IOException e) {
            System.err.println("GAGAL LOAD IMAGE: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private void loadImages() {
        imgBg       = loadImage("assets/background.png");
        imgPlayer   = loadImage("assets/player.png");
        imgPlatform = loadImage("assets/brick.png");
        imgSpike    = loadImage("assets/spike.png");
        imgGoal     = loadImage("assets/goal.png");
        imgPlatform2 = loadImage("assets/plat.png");
        imgFruit    = loadImage("assets/fruit.png");
    }

    // Bikin level dari String[] map
    private void loadLevelFromText(String[] map) {
        platforms.clear();
        spikes.clear();
        platforms2.clear();
        fruit.clear();
        goal = null;

        spawnX = 100; // default
        spawnY = 400; // default

        for (int row = 0; row < map.length; row++) {
            String line = map[row];
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);

                int x = col * tileSize;
                int y = row * tileSize;

                switch (c) {
                    case 'G': // ground/platform
                        platforms.add(new Platform(x, y, tileSize, tileSize, imgPlatform));
                        break;

                    case 'P': // platform melayang
                        platforms2.add(new Platform2(x, y + 30, 48, 10, imgPlatform2));
                        break;

                    case 'S': // SPIKE BESAR (LANTAI)
                        // Hitbox dikecilkan jadi 30x30 agar lebih fair
                        // Posisi digeser ke tengah bawah (x+10, y+20)
                        spikes.add(new Spike(x + 10, y + 20, 30, 30, imgSpike));
                        break;

                    case 's': // SPIKE KECIL (UNTUK DI ATAS PLATFORM)
                        // Hitbox mini 20x20
                        // Posisi digeser supaya pas di atas platform (x+15, y+30)
                        spikes.add(new Spike(x + 15, y + 30, 20, 20, imgSpike));
                        break;

                    case 'X': // goal
                        goal = new Goal(x, y - 10, 40, 60, imgGoal);
                        break;

                    case 'J': // spawn player
                        spawnX = x;
                        spawnY = y - 10;
                        break;
                    
                    case 'F':
                        fruit.add(new Fruit(x+15, y+15, 20, 20, imgFruit));
                        break;

                    default:
                        break;
                }
            }
        }

        // Respawn player ke posisi baru
        if (player != null) {
            player.respawn(spawnX, spawnY);
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run() {
        final double drawInterval = 1000000000.0 / 60.0; 
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remaining = nextDrawTime - System.nanoTime();
                if (remaining < 0) remaining = 0;
                Thread.sleep((long)(remaining / 1000000));
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (!isRunning) {
            if (keyH.restartPressed) {
                restartGame();
                keyH.restartPressed = false;
            }
            return;
        }

        if (keyH.pausePressed) {
            isPaused = !isPaused;
            keyH.pausePressed = false;
        }

        if (isPaused) return;

        if (player != null) player.update();

        for (Platform p : platforms) p.update();
        for (Platform2 p2 : platforms2) p2.update();
        for (Spike s : spikes) s.update(); // (Sebenarnya kosong, tapi good practice)

        // Collision Check
        if (player != null) {
            Rectangle pr = player.hitbox;

            // Spike Check
            for (Spike s : spikes) {
                if (pr.intersects(s.hitbox)) {
                    lives--;
                    if (lives <= 0) {
                        statusMsg = "GAME OVER (R to Restart)";
                        isRunning = false;
                    } else {
                        // Respawn di awal level yang sama
                        player.respawn(spawnX, spawnY);
                    }
                    break;
                }
            }

            // Fruit Check
            for (Fruit f : fruit) {
                if (!f.collected && pr.intersects(f.hitbox)) {
                    score++;
                    f.collected = true; 
                    break; // ambil satu per frame
                }
            }

            // Goal Check
            if (goal != null && pr.intersects(goal.hitbox)) {
                nextLevel();
                return;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Background
        if (imgBg != null) {
            g2.drawImage(imgBg, 0, 0, screenWidth, screenHeight, null);
        } else {
            g2.setColor(new Color(135, 206, 235));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }

        // Draw Objects
        for (Platform p : platforms) p.draw(g2);
        for (Spike s : spikes) s.draw(g2);
        for (Platform2 p2 : platforms2) p2.draw(g2);
        for (Fruit f : fruit) f.draw(g2);
        if (goal != null) goal.draw(g2);
        if (player != null) player.draw(g2);

        // UI
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Lives: " + lives, 20, 30);
        g2.drawString("Score: " + score, 20, 60);
        g2.drawString("Level: " + currLvl, 20, 90);

        if (isPaused) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("PAUSED", screenWidth / 2 - 80, screenHeight / 2);
        }

        if (!statusMsg.isEmpty()) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            // Center text simple calculation
            int stringLen = (int) g2.getFontMetrics().getStringBounds(statusMsg, g2).getWidth();
            int start = screenWidth / 2 - stringLen / 2;
            g2.drawString(statusMsg, start, screenHeight / 2);
        }

        g2.dispose();
    }
}