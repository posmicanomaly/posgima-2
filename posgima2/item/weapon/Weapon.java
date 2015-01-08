package posgima2.item.weapon;

import posgima2.item.Item;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public abstract class Weapon extends Item {
    protected int strength;
    protected int agility;
    protected int dexterity;

    public Weapon(char glyph) {
        super(glyph);
    }

}
