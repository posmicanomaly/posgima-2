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

    public static GamePanel gamePanel;
    public static TitlePanel titlePanel;
    public static LoadingPanel loadingPanel;
    static Game game;
    public static boolean GAME_IS_LOADING;
    public static boolean GAME_IS_RUNNING;
    private Dimension size;


    public WindowFrame(String title, Dimension size) throws HeadlessException {
        super(title);
        this.size = size;
        GAME_IS_LOADING = true;

        initMenu();

        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.black);
        loadingPanel = new LoadingPanel(size);
        titlePanel = new TitlePanel(size);

        setContentPane(titlePanel);
        initKeyboardFocusManager();
        pack();
        setResizable(false);
        centerWindow();
        setVisible(true);
    }

    private void initKeyboardFocusManager() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
        addWindowListener(this);
        setFocusable(true);
    }

    private void centerWindow() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width / 2) - (getSize().width / 2), (dim.height / 2) - (this.getSize().height / 2));
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameMenuItem = new JMenuItem("New");
        newGameMenuItem.setToolTipText("Start a new game");
        newGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        gameMenu.add(newGameMenuItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    public void setContentPane(JPanel panel) {
        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
    }

    private void startGame() {
        gamePanel = new GamePanel(size);
        GAME_IS_LOADING = true;
        setupWindow = new SetupWindow();

        game = new Game();
        gamePanel.renderPanel.updateGameState(game.getGameState());
        gamePanel.statisticsPanel.update(game.getGameState());
        setupWindow.hideWindow();
        GAME_IS_LOADING = false;
        GAME_IS_RUNNING = true;
        setContentPane(gamePanel);
        pack();
        update();
    }

    public static void update() {
        gamePanel.renderPanel.update();
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
        if(GAME_IS_LOADING) {
            return false;
        }
        if(e.getID() == KeyEvent.KEY_PRESSED) {
            //WindowFrame.writeConsole("/warning/key_pressed: " + e.getKeyCode() + "\n");
            long time = System.currentTimeMillis();
            GameState nextState = game.Update(e);
            gamePanel.statisticsPanel.update(nextState);
            gamePanel.renderPanel.updateGameState(nextState);
            update();
            return true;
        } else if(e.getID() == KeyEvent.KEY_RELEASED) {
            return true;
        }
        return false;
    }

    /**
     * Only call this if the player's state is not STATE_READY
     */
    public static void forceGameUpdate() {
        GameState nextState = game.Update(null);
        gamePanel.statisticsPanel.update(nextState);
        gamePanel.renderPanel.updateGameState(nextState);
        update();
    }

    public static void writeConsole(String string) {
        gamePanel.consolePanel.insertText(string + "\n");
    }
}
