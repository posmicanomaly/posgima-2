package posgima2.item;

/**
 * Created by Jesse Pospisil on 1/2/2015.
 */
public abstract class Item {
    public static final int BASE = 0;
    public static final int WEAPON = 1;
    public static final int ARMOR = 2;
    public static final int POTION = 3;
    public static final int SCROLL = 4;
    public static final int RING = 5;

    protected char glyph;
    protected int type;
    protected String name;

    public Item(char glyph) {
        this.glyph = glyph;
    }

    public char getGlyph() {
        return glyph;
    }
    public int getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
