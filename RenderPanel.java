import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class RenderPanel extends JPanel {
    public static final char WALL = '#';
    public static final char FLOOR = '\u00b7'; // middle dot
    public static final char WATER = '~';
    public static final char WEAPON = ')';
    public static final char SCROLL = '?';
    public static final char ITEM = '!';
    public static final char STAIRS = '%';
    public static final char DOOR_OPEN = '/';
    public static final char DOOR_CLOSED = '+';

    // Used to dim colors "explored" but not "visible"
    private static final int DIM_DIVISOR = 8;

    private static final int visibleY = 43;
    private static final int visibleX = 128;
    private static final Color COLOR_EXPLORED = new Color(52, 52, 72);
    Color tileGray = new Color(54, 54, 54);
    private GameState currentState;
    private int fontSize;
    private int yIncrement;
    private int xIncrement;

    public RenderPanel() {
        setDoubleBuffered(true);
        setBackground(Color.black);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Changed from getWidth() / 64 because it was causing the characters to overlap during certain resizes.
        fontSize = getHeight() / 32;
        yIncrement = fontSize - (fontSize / 4);
        xIncrement = fontSize - (fontSize / 2);
        g.setColor(tileGray);
        g.setFont(new Font("Courier New", Font.BOLD, fontSize));



        /**
         * Select which row to start drawing, to keep player centered on the Y axis.
         */
        int rowStart = currentState.getPlayer().getY() - (visibleY / 2);
        if(rowStart < 0) {
            rowStart = 0;
        } else if(currentState.getMap().length - rowStart < visibleY) {
            rowStart = currentState.getMap().length - (visibleY);
        }

        /**
         *  Select which column to start drawing, to keep player centered on the X axis
         */
        int colStart = currentState.getPlayer().getX() - (visibleX / 2);
        if(colStart < 0) {
            colStart = 0;
        } else if(currentState.getMap()[0].length - colStart < visibleX) {
            colStart = currentState.getMap()[0].length - (visibleX);
        }

        drawMap(rowStart, currentState.getMap().length, colStart, currentState.getMap()[0].length, g);
    }

    private void drawMonsters(int rowStart, int rowEnd, int colStart, int colEnd, Graphics g) {
        for(Monster m : currentState.getDungeon().getMonsters()) {
            g.setColor(getGlyphColor(m.getGlyph()));
            g.drawChars(new char[]{m.getGlyph()}, 0, 1, m.getX() * fontSize, m.getY() * fontSize);
        }
    }

    private boolean hasPlayer(int y, int x) {
        return y == currentState.getPlayer().getY() && x == currentState.getPlayer().getX();
    }

    private boolean hasMonster(int y, int x) {
        for(Monster m : currentState.getDungeon().getMonsters()) {
            if(m.getY() == y && m.getX() == x)
                return true;
        }
        return false;
    }



    private void drawMap(int rowStart, int rowEnd, int colStart, int colEnd, Graphics g) {
        int y = fontSize;
        for(int i = rowStart; i < currentState.getMap().length; i++) {
            int x = fontSize;
            for(int j = colStart; j < currentState.getMap()[i].length; j++) {
                if(hasPlayer(i, j)) {
                    drawPlayer(y, x, g);
                } else {
                    g.setColor(getGlyphColor(currentState.getMap()[i][j]));
                    if (currentState.getVisibleMap()[i][j]) {
                        if(hasMonster(i, j)) {
                            drawMonster(currentState.getDungeon().getMonsterAt(i, j), y, x, g);
                        } else {
                            g.drawChars(currentState.getMap()[i], j, 1, x, y);
                        }
                    }
                    else if (currentState.getDungeon().getExploredMap()[i][j]) {
                        int R = g.getColor().getRed() / DIM_DIVISOR;
                        int G = g.getColor().getGreen() / DIM_DIVISOR;
                        int B = g.getColor().getGreen() / DIM_DIVISOR;

                        g.setColor(new Color(R, G, B));
                        g.drawChars(currentState.getMap()[i], j, 1, x, y);
                    }
                }

                x += xIncrement;
            }
            y += yIncrement;
        }
    }

    private void drawPlayer(int y, int x, Graphics g) {
        g.setColor(getGlyphColor('@'));
        g.drawChars(new char[]{'@'}, 0, 1, x, y);
    }

    private void drawMonster(Monster monster, int renderY, int renderX, Graphics g) {
        g.setColor(getGlyphColor(monster.getGlyph()));
        if(monster.isAlive())
            g.drawChars(new char[]{monster.getGlyph()}, 0, 1, renderX, renderY);
        else {
            g.drawChars(new char[]{'}'}, 0, 1, renderX, renderY);
        }
    }

    private Color getGlyphColor(char c) {
        switch(c) {
            case '@' : return Color.cyan;
            case 'M' : return Color.red;
            case WALL : return Color.lightGray;
            case ITEM :
            case SCROLL :
            case WEAPON : return Color.pink;
            case STAIRS : return Color.green;
            case WATER : return Color.blue;
            default : return Color.gray;
        }
    }

    public void updateGameState(GameState state) {
        currentState = state;
    }

    public void Update() {
        repaint();
    }
}
