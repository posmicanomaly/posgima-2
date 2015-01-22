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
    protected int damageBonus;
    protected int slot;
    protected int minHitDamage;
    protected int maxHitDamage;


    public Weapon(char glyph) {
        super(glyph);
    }

    public String toString() {
        String result = name + ": ";
        result += minHitDamage + "-" + maxHitDamage;
        if(damageBonus > 0) {
            result += "+" + damageBonus;
        }
        result += " weapon";
        return result;
    }


    public int getDamageBonus() {
        return damageBonus;
    }

    public int getAverageDamage() {
        return (minHitDamage + maxHitDamage) / 2;
    }

    public int getMinHitDamage() {
        return minHitDamage;
    }

    public int getMaxHitDamage() {
        return maxHitDamage;
    }
}
