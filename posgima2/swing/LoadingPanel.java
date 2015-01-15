package posgima2.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/15/2015.
 */
public class LoadingPanel extends JPanel {
    public LoadingPanel(Dimension size) {
        setLayout(new BorderLayout());
        setBackground(Color.cyan);
        setPreferredSize(size);
    }
}
