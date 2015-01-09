package posgima2.swing;

import posgima2.game.Game;
import posgima2.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class WindowFrame extends JFrame implements KeyEventPostProcessor, WindowListener {
    static SetupWindow setupWindow;

    static RenderPanel renderPanel;
    public static ConsolePanel consolePanel;
    static StatisticsPanel statisticsPanel;

    static Game game;

    public WindowFrame(String title, Dimension size) throws HeadlessException {
        super(title);
        setupWindow = new SetupWindow();

        int consoleHeight = 160;
        int consoleWidth = (int) (size.getWidth() * .80);

        int statisticsWidth = 160;
        int statisticsHeight = (int) (size.getHeight() * .75);

        int renderHeight = (int) (size.getHeight() * .75);
        int renderWidth = (int) (size.getWidth() * .85);


        //pane = new JPanel(new GridBagLayout());

        //setExtendedState(MAXIMIZED_BOTH);


        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.black);
        //getContentPane().add(pane);
        renderPanel = new RenderPanel();
        //renderPanel.setPreferredSize(new Dimension(renderWidth, renderHeight));
        consolePanel = new ConsolePanel();
        consolePanel.setPreferredSize(new Dimension(consoleWidth, consoleHeight));
        statisticsPanel = new StatisticsPanel();
        statisticsPanel.setPreferredSize(new Dimension(statisticsWidth, statisticsHeight));

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

        //pack();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
        addWindowListener(this);
        setFocusable(true);



        //setPreferredSize(size);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();


        pack();
        setResizable(false);
        this.setLocation((dim.width / 2) - (getSize().width / 2), (dim.height / 2) - (this.getSize().height / 2));

        game = new Game();
        renderPanel.updateGameState(game.getGameState());
        statisticsPanel.update(game.getGameState());

        setVisible(true);

    }

    public static void Update() {
        //long time = System.currentTimeMillis();
        renderPanel.Update();

        //setupWindow.println("update took " + (System.currentTimeMillis() - time));
    }


    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public boolean postProcessKeyEvent(KeyEvent e) {
        if(e.getID() == KeyEvent.KEY_PRESSED) {
            //consolePanel.insertText("/warning/key_pressed: " + e.getKeyCode() + "\n");
            long time = System.currentTimeMillis();
            GameState nextState = game.Update(e);
            //setupWindow.println("game.Update() took " + (System.currentTimeMillis() - time));
            //consolePanel.insertText(nextState.getMessage() + "\n");
            //consolePanel.insertText("/info/x: " + nextState.getPlayer().getX() + "  y: " + nextState.getPlayer().getY
            //        () + "\n");
            statisticsPanel.update(nextState);
            renderPanel.updateGameState(nextState);
            Update();
            return true;
        } else if(e.getID() == KeyEvent.KEY_RELEASED) {
            //consolePanel.insertText("/success/key_released: " + e.getKeyCode() + "\n");
            return true;
        }
        return false;
    }

    /**
     * Only call this if the player's state is not STATE_READY
     */
    public static void forceGameUpdate() {
        GameState nextState = game.Update(null);
        statisticsPanel.update(nextState);
        renderPanel.updateGameState(nextState);
        Update();
    }

    public static void writeConsole(String string) {
        consolePanel.insertText(string + "\n");
    }
}
