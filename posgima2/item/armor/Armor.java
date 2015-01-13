package posgima2.item.armor;

import posgima2.item.Item;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public abstract class Armor extends Item{
    public static final int SLOT_CHEST = 0;
    public static final int SLOT_HEAD = 1;
    public static final int SLOT_HAND = 2;
    public static final int SLOT_LEG = 3;
    public static final int SLOT_ARM = 4;

    protected int armorClass;
    protected int slot;
    public Armor(char glyph) {
        super(glyph);
    }

    public int getArmorClass() {
        return armorClass;
    }

    public int getSlot() {
        return slot;
    }

    public String toString() {
        String result = name + ": ";
        result += armorClass + "ac";
        return result;
    }

}
