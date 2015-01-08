package weapon;

import posgima2.Item;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public abstract class Weapon extends Item{
    protected int strength;
    protected int agility;
    protected int dexterity;

    public Weapon(char glyph) {
        super(glyph);
    }

}
