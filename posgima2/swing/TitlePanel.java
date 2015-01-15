package posgima2.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/15/2015.
 */
public class TitlePanel extends JPanel {
    public TitlePanel(Dimension size) {
        setLayout(new BorderLayout());
        setBackground(Color.blue);
        setPreferredSize(size);
    }

    public void paintComponent(Graphics g) {
        //g.clearRect(0, 0, getWidth(), getHeight());
        int fontSize = 48;
        g.setFont(new Font("Courier New", Font.BOLD, fontSize));
        g.setColor(Color.BLACK);
        char[] title = "[ Posgima - 2 ] by Jesse Pospisil".toCharArray();
        g.drawChars(title, 0, title.length, 0, fontSize);
    }
}
