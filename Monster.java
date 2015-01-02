import java.util.LinkedList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public class Monster {
    private int y;
    private int x;
    private char glyph;
    private boolean[][] visibility;
    private boolean aggroPlayer;

    private LinkedList<Vector2i> moveQueue;
    private boolean alive;

    public Monster(int y, int x) {
        this.y = y;
        this.x = x;
        glyph = 'r';
        moveQueue = new LinkedList<Vector2i>();
        aggroPlayer = false;
        alive = true;
    }

    public boolean move(int dir) {
        switch(dir) {
            case Game.UP:
                y--;
                break;
            case Game.DOWN:
                y++;
                break;
            case Game.LEFT:
                x--;
                break;
            case Game.RIGHT:
                x++;
                break;
        }
        return true;
    }

    public char getGlyph() {
        return glyph;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean[][] getVisibility() {
        return visibility;
    }

    public void calculateVisibility(Dungeon dungeon) {
        visibility = new boolean[dungeon.MAP_ROWS][dungeon.MAP_COLS];
        for(Vector2i v : FieldOfView.bresenhamFov(dungeon.getMap(), y, x, 0)) {
            visibility[v.getY()][v.getX()] = true;
        }
    }

    public LinkedList<Vector2i> getMoveQueue() {
        return moveQueue;
    }

    public void die() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }
}
