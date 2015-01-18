package posgima2.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/16/2015.
 */
public abstract class Util {
    public static void centerWindow(JFrame window) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((dim.width / 2) - (window.getSize().width / 2), (dim.height / 2) - (window.getSize()
                .height /
                2));
    }
}
