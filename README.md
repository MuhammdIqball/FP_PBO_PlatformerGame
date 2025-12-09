Map / LVL Making :
    Desain dari array String[] levelX di GamePanel.java

        J = Player Spawn

        G = Ground / Brick

        X = Goal / Next Level

        P = Platform (melayang / tambahan)

        S = Spike (Hazard)

        . = Empty Space

Nambah Object Baru :
    Buat class baru (mis: Coin, Enemy, dll)
    -   Tambah list di GamePanel (contoh: ArrayList<Objek> objekList)
    -   Tambah BufferedImage di GamePanel

    Load image di loadImages()
    -   Tambah case di loadLevelFromText() (mapping ke 1 karakter map)
    -   Tambah for di update() (panggil obj.update())
    -   Tambah for di paintComponent() (panggil obj.draw(g2))

Atur Collision Objek :
    Platform Collision (SOLID)
    -   Di Player → checkHorizontalCollisions() dan checkVerticalCollisions()
    -   Loop semua platform (dan platform lain yang solid)
    -   Kalau hitbox intersect → geser posisi player (supaya nggak nembus)

Event Collision (TRIGGER)
    Di GamePanel.update() setelah player.update()
    -   Cek player.hitbox.intersects(obj.hitbox) untuk:
    -   Spike → damage / respawn
    -   Goal → nextLevel()
    -   Pickup (Coin, dsb.) → tandai collected / tambah score
    -   Checkpoint → update spawnX, spawnY