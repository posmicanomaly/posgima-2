package posgima2.item.armor;

import posgima2.item.Item;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public abstract class Armor extends Item{
    protected int armorClass;
    public Armor(char glyph) {
        super(glyph);
    }

    public int getArmorClass() {
        return armorClass;
    }
}
