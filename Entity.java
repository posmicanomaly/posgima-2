/**
 * Created by Jesse Pospisil on 1/2/2015.
 */
public abstract class Entity {
    protected Tile tile;
    protected char glyph;
    protected int y;
    protected int x;
    private int curentHP;
    private int maxHP;
    private int strength;
    private int agility;
    private int dexterity;
    private Tile targetTile;

    public Entity(char glyph) {
        this.glyph = glyph;
        tile = null;
        y = 0;
        x = 0;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Tile getTile() {
        return tile;
    }

    public char getGlyph() {
        return glyph;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveToTileImmediately(Tile tile) {
        tile.addEntity(this);
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getCurentHP() {
        return curentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setTargetTile(Tile targetTile) {
        this.targetTile = targetTile;
    }

    public Tile getTargetTile() {
        return targetTile;
    }
}
