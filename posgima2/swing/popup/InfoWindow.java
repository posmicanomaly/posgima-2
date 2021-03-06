package posgima2.swing.popup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/13/2015.
 */
public abstract class InfoWindow extends JPanel{

    private String panelName;

    public InfoWindow(LayoutManager layout) {
        super(layout);
    }

    public abstract void update();

    public String getPanelName() {
        return panelName;
    }
}
