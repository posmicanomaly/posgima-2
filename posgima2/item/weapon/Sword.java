package posgima2.item.weapon;

import posgima2.item.Item;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class Sword extends Weapon {

    public Sword(char glyph, int strength, int dexterity, int agility) {
        super(glyph);
        type = Item.WEAPON;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
    }
}
