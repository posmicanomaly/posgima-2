import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class RenderPanel extends JPanel {
    public static final char WALL = '#';
    public static final char FLOOR = '.';//'\u00b7'; // middle dot
    public static final char WATER = '~';
    public static final char WEAPON = ')';
    public static final char SCROLL = '?';
    public static final char ITEM = '!';
    public static final char STAIRS = '%';
    public static final char DOOR_OPEN = '/';
    public static final char DOOR_CLOSED = '+';

    // Used to dim colors "explored" but not "visible"
    private static final int DIM_DIVISOR = 2;

    private static final int visibleY = 31;//43;
    private static final int visibleX = 99;//128;
    private static final Color COLOR_EXPLORED = new Color(52, 52, 72);
    Color tileGray = new Color(54, 54, 54);
    private GameState currentState;
    private int fontSize;
    private int yIncrement;
    private int xIncrement;

    public RenderPanel() {
        setDoubleBuffered(true);
        setBackground(Color.black);
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/square.ttf")));
        } catch (IOException |FontFormatException e) {
            //Handle exception
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Changed from getWidth() / 64 because it was causing the characters to overlap during certain resizes.
        fontSize = getHeight() / 24;
        yIncrement = fontSize - (fontSize / 4);
        xIncrement = fontSize - (fontSize / 2);
        g.setColor(tileGray);
        g.setFont(new Font("courier new", Font.PLAIN, fontSize));



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

//    private void drawMonsters(int rowStart, int rowEnd, int colStart, int colEnd, Graphics g) {
//        for(Monster m : currentState.getDungeon().getMonsters()) {
//            g.setColor(getGlyphColor(m.getGlyph()));
//            g.drawChars(new char[]{m.getGlyph()}, 0, 1, m.getX() * fontSize, m.getY() * fontSize);
//        }
//    }

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
        for(int i = rowStart; i < currentState.getDungeon().getTileMap().length; i++) {
            int x = fontSize;
            for(int j = colStart; j < currentState.getDungeon().getTileMap()[i].length; j++) {
                if(hasPlayer(i, j)) {
                    drawPlayer(y, x, g, currentState.getPlayer());
                } else {
                    g.setColor(getGlyphColor(currentState.getDungeon().getTileMap()[i][j].getGlyph()));
                    if (currentState.getVisibleMap()[i][j]) {
                        g.setColor(addYellow(g.getColor()));
                        if(hasMonster(i, j)) {
                            drawMonster(currentState.getDungeon().getMonsterAt(i, j), y, x, g);
                        } else if(currentState.getDungeon().hasItems(i, j)) {
                            drawItem(currentState.getDungeon().getTileMap()[i][j], y, x, g);
                        } else {
                            g.drawChars(new char[]{currentState.getDungeon().getTileMap()[i][j].getGlyph()}, 0, 1, x,
                                    y);
                        }
                    }
                    else if (currentState.getDungeon().getExploredMap()[i][j]) {
                        int R = g.getColor().getRed() / DIM_DIVISOR;
                        int G = g.getColor().getGreen() / DIM_DIVISOR;
                        int B = g.getColor().getGreen() / DIM_DIVISOR;

                        g.setColor(new Color(R, G, B));
                        g.drawChars(new char[]{currentState.getDungeon().getTileMap()[i][j].getGlyph()}, 0, 1, x, y);
                    }
                }

                x += xIncrement;
            }
            y += yIncrement;
        }
    }

    private Color addYellow(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        r += r / 2;
        g += g / 2;
        if(r > 255)
            r = 255;
        if(g > 255)
            g = 255;
        color = new Color(r,g,b);
        return color;
    }

    private void drawItem(Tile tile, int y, int x, Graphics g) {
        char glyph = tile.getItems().get(tile.getItems().size() - 1).getGlyph();
        g.setColor(getGlyphColor(glyph));
        g.drawChars(new char[]{glyph}, 0, 1, x, y);
    }

    private void drawPlayer(int y, int x, Graphics g, Player player) {
        char glyph = player.getGlyph();
        g.setColor(getGlyphColor(glyph));
        g.drawChars(new char[]{glyph}, 0, 1, x, y);
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
            case WALL : return Color.darkGray;
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
