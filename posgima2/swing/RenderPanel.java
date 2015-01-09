package posgima2.swing;

import posgima2.game.GameState;
import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.Monster;
import posgima2.world.Player;
import posgima2.world.dungeonSystem.dungeon.Tile;

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
    public static final char STAIRS_DOWN = '>';
    public static final char STAIRS_UP = '<';
    public static final char DOOR_OPEN = '/';
    public static final char DOOR_CLOSED = '+';

    // Used to dim colors "explored" but not "visible"
    private static final int DIM_DIVISOR = 2;

    private static final int visibleY = 30;//43;
    private static final int visibleX = 87;//128;
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
        fontSize = getHeight() / 24;
        yIncrement = fontSize - (fontSize / 4);
        xIncrement = fontSize - (fontSize / 2);
        g.setColor(tileGray);
        g.setFont(new Font("courier new", Font.PLAIN, fontSize));



        /**
         * Select which row to start drawing, to keep player centered on the Y axis.
         */
        int rowStart;
        if(currentState.getDungeon().getMAP_ROWS() < visibleY) {
            rowStart = 0;
        } else {
            rowStart = currentState.getPlayer().getY() - (visibleY / 2);
            if (rowStart < 0) {
                rowStart = 0;
            } else if (currentState.getMap().length - rowStart < visibleY) {
                rowStart = currentState.getMap().length - (visibleY);
            }
        }

        /**
         *  Select which column to start drawing, to keep player centered on the X axis
         */
        int colStart;
        if(currentState.getDungeon().getMAP_COLS() < visibleX) {
            colStart = 0;
        } else {
            colStart = currentState.getPlayer().getX() - (visibleX / 2);
            if (colStart < 0) {
                colStart = 0;
            } else if (currentState.getMap()[0].length - colStart < visibleX) {
                colStart = currentState.getMap()[0].length - (visibleX);
            }
        }

        drawMap(rowStart, currentState.getMap().length, colStart, currentState.getMap()[0].length, g);
    }

//    private void drawMonsters(int rowStart, int rowEnd, int colStart, int colEnd, Graphics g) {
//        for(posgima2.world.Monster m : currentState.getDungeon().getMonsters()) {
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
        Player p = currentState.getPlayer();
        Dungeon d = currentState.getDungeon();
        Tile[][] tMap = currentState.getDungeon().getTileMap();
        boolean[][] visMap = currentState.getDungeon().getVisibleMap();
        boolean[][] expMap = currentState.getDungeon().getExploredMap();

        int y = fontSize;
        for(int i = rowStart; i < tMap.length; i++) {
            int x = fontSize;
            for(int j = colStart; j < tMap[i].length; j++) {
                /*
                Player gets first priority
                 */
                if(hasPlayer(i, j)) {
                    drawPlayer(y, x, g, p);
                }
                /*

                 */
                else {
                    // set color based on a lookup switch for the glyph
                    g.setColor(getGlyphColor(tMap[i][j].getGlyph()));
                    // if this tile is visible to the player
                    if (visMap[i][j]) {
                        // apply a torch light effect
                        g.setColor(addTorchLight(g.getColor(), getLargestDistanceDifference(p.getY(), p.getX(), i, j)));
                        /*
                        Priority: Monster -> Items -> Empty Tile
                         */
                        if(hasMonster(i, j)) {
                            drawMonster(d.getMonsterAt(i, j), y, x, g);
                        } else if(d.hasItems(i, j)) {
                            drawItem(tMap[i][j], y, x, g);
                        } else {
                            g.drawChars(new char[]{tMap[i][j].getGlyph()}, 0, 1, x, y);
                        }
                    }
                    // if it's not visible, but has been explored before
                    else if (expMap[i][j]) {
                        /*
                        Draw the tile in a dark blue, without monsters or items.
                         */
                        int R = 0;
                        int G = 0;
                        int B = 64;

                        g.setColor(new Color(R, G, B));
                        g.drawChars(new char[]{tMap[i][j].getGlyph()}, 0, 1, x, y);
                    }
                }
                // Next draw column
                x += xIncrement;
            }
            // Next draw row
            y += yIncrement;
        }
    }

    /**
     * Gives the absolute highest difference of distance between y0,x0 and y1,x1
     * @param y0 start Y
     * @param x0 start X
     * @param y1 end Y
     * @param x1 end X
     * @return largest difference, either y0-y1 or x0-x1
     */
    private int getLargestDistanceDifference(int y0, int x0, int y1, int x1) {
        int yd = Math.abs(y0-y1);
        int xd = Math.abs(x0-x1);

        if(yd > xd) {
            return yd;
        }
        return xd;
    }

    /**
     * Calculate a fading torchlight color based on the distance from its source
     * @param color Original color of tile
     * @param distanceFrom absolute distance from the source
     * @return Color
     */
    private Color addTorchLight(Color color, int distanceFrom) {
        /*
        Extract the RGB values from color
         */
        int b = color.getBlue();
        /*
        Apply a reddish "torch" color, and subtract te distanceFrom * 16 value
        Higher values will give less of a "bright" radius, and also decrease the initial brightness
        Lower values will give a higher initial value, and a larger radius that fades slower
         */
        int r = color.getRed() + (150 - (distanceFrom * 16));
        int g = color.getRed() + (120 - (distanceFrom * 16));

        /*
        Bounds checking for R and G
         */
        if(r < 0)
            r = 0;
        else if(r > 255)
            r = 255;
        if(g < 0)
            g = 0;
        else if(g > 255)
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
        if(monster == null) {
            g.setColor(Color.white);
            g.drawChars(new char[]{'N'}, 0, 1, renderX, renderY);
        } else {
            g.setColor(getGlyphColor(monster.getGlyph()));
            if (monster.isAlive())
                g.drawChars(new char[]{monster.getGlyph()}, 0, 1, renderX, renderY);
            else {
                g.drawChars(new char[]{'}'}, 0, 1, renderX, renderY);
            }
        }
    }

    private Color getGlyphColor(char c) {
        switch(c) {
            case '@' : return Color.cyan;
            case WALL : return Color.darkGray;
            case ITEM :
            case SCROLL :
            case WEAPON : return Color.pink;
            case STAIRS_DOWN: return Color.WHITE;
            case STAIRS_UP: return Color.white;
            case WATER : return Color.blue;
            case 'r': return Color.red;
            case 'g': return Color.green;
            case 'D': return Color.BLUE;
            case 'T': return Color.green;
            case 'b': return Color.pink;
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
