package posgima2.swing.popup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/13/2015.
 */
public class PopupWindow extends JFrame {
    private InfoWindow contentPanel;

    public PopupWindow(InfoWindow contentPanel) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        //setMaximumSize(getPreferredSize());
        this.contentPanel = contentPanel;
        setTitle(contentPanel.getPanelName());
        add(this.contentPanel);
        pack();
    }

    public void showWindow() {
        setVisible(true);
    }

    public void hideWindow() {
        setVisible(false);
    }

    public void setContentPanel(InfoWindow contentPanel) {
        this.contentPanel = contentPanel;
        pack();
    }

    public void update() {
        contentPanel.update();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setResizable(false);
        this.setLocation((dim.width / 2) - (getSize().width / 2), (dim.height / 2) - (this.getSize().height / 2));

    }
}
