package posgima2.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/15/2015.
 */
public class GamePanel extends JPanel {
    public RenderPanel renderPanel;
    public ConsolePanel consolePanel;
    public StatisticsPanel statisticsPanel;
    public GamePanel(Dimension size) {
        //getContentPane().add(pane);
        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(size);



        int consoleHeight = 160;
        int consoleWidth = (int) (size.getWidth() * .80);

        int statisticsWidth = 160;
        int statisticsHeight = (int) (size.getHeight() * .75);

        int renderHeight = (int) (size.getHeight() * .75);
        int renderWidth = (int) (size.getWidth() * .85);

        consolePanel = new ConsolePanel();
        consolePanel.setPreferredSize(new Dimension(consoleWidth, consoleHeight));
        statisticsPanel = new StatisticsPanel();
        statisticsPanel.setPreferredSize(new Dimension(statisticsWidth, statisticsHeight));
        renderPanel = new RenderPanel();
        //renderPanel.setPreferredSize(new Dimension(renderWidth, renderHeight));
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(statisticsPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        //gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(renderPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        add(consolePanel, gbc);
    }
}
