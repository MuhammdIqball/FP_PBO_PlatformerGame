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

    // LEVEL 1 pakai teks (16 kolom x 12 baris kalau tileSize = 50 dan screen 800x600)
    private String[] level1 = {
        "................G",  // row 0
        "................G",  // row 1
        "................G",  // row 2
        "................G",  // row 3
        ".........FFF....G",  // row 4
        "..XPPPPPPGGGP...G", // row 5
        "..G.........F...G", // row 6
        "............P...G",  // row 7
        ".....FF.....F...G",  // row 8
        "....PGG..PGGG...G", // row 9  -> J = spawn player
        "J.........GGG...G",  // row 10
        "GGG.G..GG.GGG.GGG",  // row 10
        "GGGSSSSSSSSSSSGGG"   // row 11 -> G = ground bawah
    };
    private String[] level2 = {
        "................G",  // row 0
        "................G",  // row 1
        "......X.........G",  // row 2
        "......G..P......G",  // row 3  
        ".....S....P.....G",  // row 4
        "....GGG..........",  // row 5  
        "........P.......G",  // row 6
        "..........GGG....G", // row 7  
        ".......P........G",  // row 8
        "..........G.....G",  // row 9
        "J.......G.......G",  // row 10 -> harus lompat ke platform sebelum ke goal
        "GGGGGGGGGGGGGGGGG"   // row 11
    };

    private String[] level3 = {
        "................G",  // row 0
        "................G",  // row 1
        "................G",  // row 2
        "................G",  // row 3
        "................G",  // row 4
        "..........X.....G", // row 5
        "................G", // row 6
        ".......G........G",  // row 7
        "......G.........G",  // row 8
        ".....G..........G", // row 9  
        "J...G...........G",  // row 10
        "GGGGGGGGGGGGGGGGG"   // row 11 -> G = ground bawah
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
        player = new Player(100, 400, 26, 26, this, keyH);
        
        // spawn player
        loadLevelFromText(level1);
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

    public void nextGame() {
        loadLevelFromText(level1);
        player.respawn(100, 400);
    }

    private void loadCurrentLevel() {
        switch (currLvl) {
            case 1 -> loadLevelFromText(level1);
            case 2 -> loadLevelFromText(level2);
            case 3 -> loadLevelFromText(level3);
            default -> {
                // kalau sampai sini, dianggap sudah menang semua
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

        loadCurrentLevel(); // ini akan panggil loadLevelFromText(levelX)
    }


    //Assets Manager
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
        imgFruit = loadImage("assets/fruit.png");
    }


    // Bikin level dari String[] map
    private void loadLevelFromText(String[] map) {
        platforms.clear();
        spikes.clear();
        platforms2.clear();
        goal = null;

        // Reset spawn ke default dulu (jaga-jaga kalau di map nggak ada 'J')
        spawnX = 100;
        spawnY = 400;

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
                        platforms2.add(new Platform2(x, y +30, 48, 5, imgPlatform2));
                        break;

                    case 'S': // spike
                        spikes.add(new Spike(x, y, 50, 52, imgSpike));
                        break;

                    case 'X': // goal
                        goal = new Goal(x, y - 10, 40, 60, imgGoal);
                        break;

                    case 'J': // spawn player
                        spawnX = x;
                        spawnY = y - 10;
                        break;
                    
                    case 'F':
                        fruit.add(new Fruit(x+20, y+20, 12, 17, imgFruit));
                        break;

                    case '.':
                    default:
                        // kosong
                        break;
                }
            }
        }

        // PERHATIKAN: DI SINI KITA TIDAK BIKIN PLAYER BARU TIAP LEVEL
        if (player == null) {
            // HANYA SEKALI, di level pertama
            player = new Player(spawnX, spawnY, 32, 48, this, keyH);
        } else {
            // Pindah level → cukup respawn
            player.respawn(spawnX, spawnY);
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    
    @Override
    public void run() {
        final double drawInterval = 1000000000.0 / 60.0; // 60 FPS
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
        // Kalau game sudah selesai (menang/kalah)
        if (!isRunning) {
            if (keyH.restartPressed) {
                restartGame();
                keyH.restartPressed = false;
            }
            return;
        }

        // Toggle pause
        if (keyH.pausePressed) {
            isPaused = !isPaused;
            keyH.pausePressed = false;
        }

        if (isPaused) return;

        if (player != null) {
            player.update();
        }

        for (Platform p : platforms) {
            p.update();
        }

        for (Platform2 p2 : platforms2) {
            p2.update();
        }

        // Cek collision dengan spike & goal
        if (player != null) {
            Rectangle pr = player.hitbox;

            // Spike
            for (Spike s : spikes) {
                if (pr.intersects(s.hitbox)) {
                    lives--;
                    if (lives <= 0) {
                        statusMsg = "GAME OVER (R to Restart)";
                        isRunning = false;
                    } else {
                        player.respawn(100, 400);
                    }
                    break;
                }
            }

            for (Fruit f : fruit) {
                if (!f.collected && pr.intersects(f.hitbox)) {
                    score++;
                    f.collected = true; 
                    break;
            }
}

            // Goal
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

        // World
        for (Platform p : platforms) {
            p.draw(g2);
        }
        for (Spike s : spikes) {
            s.draw(g2);
        }
        for (Platform2 p2 : platforms2) {
            p2.draw(g2);
        }
        for (Fruit f : fruit) {
            f.draw(g2);
        }
        if (goal != null) goal.draw(g2);
        if (player != null) player.draw(g2);

        // UI
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Lives: " + lives, 20, 30);
        g2.drawString("Score: " + score, 20, 60);

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
            g2.drawString(statusMsg, 150, screenHeight / 2);
        }

        g2.dispose();
    }
}