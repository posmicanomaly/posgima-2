import java.util.LinkedList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public class Monster extends Entity{
    private boolean[][] visibility;
    private boolean aggroPlayer;

    private LinkedList<Vector2i> moveQueue;
    private boolean alive;

    public Monster(char glyph) {
        super(glyph);
        moveQueue = new LinkedList<Vector2i>();
        aggroPlayer = false;
        alive = true;
    }

    public boolean[][] getVisibility() {
        return visibility;
    }

    public void calculateVisibility(Dungeon dungeon) {
        visibility = new boolean[dungeon.MAP_ROWS][dungeon.MAP_COLS];
        for(Vector2i v : FieldOfView.bresenhamFov(dungeon.getTileMap(), y, x, 0)) {
            visibility[v.getY()][v.getX()] = true;
        }
    }

    public LinkedList<Vector2i> getMoveQueue() {
        return moveQueue;
    }

    public boolean die() {
        alive = false;
        this.tile.addItem(new Corpse('}', this));
        this.tile.remove(this);
        System.out.println(this + " died.");
        return true;
    }

    public boolean isAlive() {
        return alive;
    }
}
