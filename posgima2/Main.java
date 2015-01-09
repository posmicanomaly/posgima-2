package posgima2;

import posgima2.game.Game;
import posgima2.swing.WindowFrame;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class Main {
    public static void main(String[] args) {
        WindowFrame windowFrame = new WindowFrame("Posgima-2 - " + Game.STAGE + " " + Game.VERSION, new Dimension
                (1366, 768));
        while(true) {
            windowFrame.Update();
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
