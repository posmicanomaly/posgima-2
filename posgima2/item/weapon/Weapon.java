package posgima2.item.weapon;

import posgima2.item.Item;
import posgima2.misc.Dice;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public abstract class Weapon extends Item {
    public static final int SLOT_MAINHAND = 0;
    public static final int SLOT_OFFHAND = 1;
    protected int strength;
    protected int agility;
    protected int dexterity;
    protected int hitDie;
    protected int damageBonus;
    protected int slot;

    public Weapon(char glyph) {
        super(glyph);
    }

    public String toString() {
        String result = name + ": ";
        result += "1d" + hitDie +  " ";
        if(damageBonus > 0) {
            result += "+" + damageBonus;
        }
        result += " weapon";
        return result;
    }

    public int getHitDie() {
        return hitDie;
    }

    public int getDamageBonus() {
        return damageBonus;
    }
}
