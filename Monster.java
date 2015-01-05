import java.util.LinkedList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public class Monster extends Entity{
    private boolean[][] visibility;
    private boolean aggroPlayer;

    private LinkedList<Vector2i> moveQueue;
    private int age;

    public Monster(char glyph) {
        super(glyph);
        moveQueue = new LinkedList<Vector2i>();
        aggroPlayer = false;
        alive = true;
        strength = 1;
        maxHP = 7;
        currentHP = maxHP;
        age = 0;
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

    public boolean die() {
        alive = false;
        this.tile.addItem(new Corpse('}', this));
        this.tile.remove(this);
        //WindowFrame.setupWindow.println(this + " died.");
        return true;
    }

    public LinkedList<Vector2i> getMoveQueue() {
        return moveQueue;
    }

    public int getAge() {
        return age;
    }

    public void addAge(int i) {
        age += i;
    }
}
