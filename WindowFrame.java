import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class WindowFrame extends JFrame implements KeyEventPostProcessor, WindowListener {
    RenderPanel renderPanel;
    ConsolePanel consolePanel;
    StatisticsPanel statisticsPanel;

    Game game;

    public WindowFrame(String title) throws HeadlessException {
        super(title);
        //pane = new JPanel(new GridBagLayout());

        setExtendedState(MAXIMIZED_BOTH);


        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.black);
        //getContentPane().add(pane);
        renderPanel = new RenderPanel();
        consolePanel = new ConsolePanel();
        statisticsPanel = new StatisticsPanel();

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        //gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = .95;
        gbc.weighty = .8;
        add(renderPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = .95;
        gbc.weighty = .2;
        add(consolePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = .05;
        gbc.weighty = .8;
        add(statisticsPanel, gbc);

        //pack();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
        addWindowListener(this);
        setFocusable(true);
        game = new Game();
        renderPanel.updateGameState(game.getGameState());
        setVisible(true);
    }

    public void Update() {
        renderPanel.Update();
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
            GameState nextState = game.Update(e);
            consolePanel.insertText(nextState.getMessage() + "\n");
            //consolePanel.insertText("/info/x: " + nextState.getPlayer().getX() + "  y: " + nextState.getPlayer().getY
            //        () + "\n");
            renderPanel.updateGameState(nextState);
            return true;
        } else if(e.getID() == KeyEvent.KEY_RELEASED) {
            //consolePanel.insertText("/success/key_released: " + e.getKeyCode() + "\n");
            return true;
        }
        return false;
    }
}
