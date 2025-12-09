/**
 * Write a description of class Main here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Platformer Final Project");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        gp.startGameThread();
    }
}