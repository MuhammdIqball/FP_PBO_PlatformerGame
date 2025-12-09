/**
 * Write a description of class LevelManager here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class LevelManager {
    GamePanel gp;

    public LevelManager(GamePanel gp) {
        this.gp = gp;
    }

    public void loadLevel(int level) {
        gp.platforms.clear();
        gp.spikes.clear();
        gp.timer = 5400; // Reset Waktu 90 detik

        int[][] mapData = getMapData(level);
        int tileSize = gp.tileSize;
        
        // Loop Array untuk membuat Objek
        for (int row = 0; row < mapData.length; row++) {
            for (int col = 0; col < mapData[row].length; col++) {
                int code = mapData[row][col];
                int x = col * tileSize;
                int y = row * tileSize;
                
                if (code == 1) gp.platforms.add(new Platform(x, y, tileSize, tileSize, gp.imgBrick, Platform.Type.STATIC));
                else if (code == 2) gp.platforms.add(new Platform(x, y, tileSize, 30, gp.imgCloud, Platform.Type.MOVING));
                else if (code == 3) gp.platforms.add(new Platform(x, y, tileSize, 30, gp.imgCracked, Platform.Type.CRACKED));
                else if (code == 8) gp.spikes.add(new Spike(x, y, gp.imgSpike));
                else if (code == 9) gp.goal = new Goal(x, y, gp.imgGoal);
            }
        }
        
        // Posisi Start Player tiap level
        int startY = (level == 1) ? 450 : 350;
        gp.player = new Player(50, startY, gp, gp.imgPlayer);
    }

    private int[][] getMapData(int level) {
        // 0=Kosong, 1=Bata, 2=Awan, 3=Retak, 8=Duri, 9=Goal
        if (level == 1) {
            return new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0},
                {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,8,0,0,0,0,0,8,0,0,0,0,0},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
            };
        } else if (level == 2) {
            return new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0},
                {0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0},
                {0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0},
                {0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,3,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,8,8,8,0,0,0,8,8,0,0,0,0,0},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
            };
        } else { // Level 3
             return new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3},
                {0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,8,8,8,8,8,8,8,8,8,8,8,8,0,0},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
            };
        }
    }
}