package posgima2;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class CharacterWindow extends JFrame {
    private JPanel statPanel;
    private Player player;
    public CharacterWindow(Player player) {
        statPanel = new JPanel(new BorderLayout());
        JLabel playerLabel = new JLabel(player.toString());
        statPanel.add(playerLabel);
        add(statPanel);
        pack();
    }

    public void update() {
        pack();
    }

    public void showWindow() {
        setVisible(true);
    }

    public void hideWindow() {
        setVisible(false);
    }
}
