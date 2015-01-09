package posgima2.item.weapon;

import posgima2.item.Item;
import posgima2.misc.Dice;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public abstract class Weapon extends Item {
    protected int strength;
    protected int agility;
    protected int dexterity;
    protected int hitDie;
    protected int damageBonus;

    public Weapon(char glyph) {
        super(glyph);
    }

    public String toString() {
        String result = "";
        switch(hitDie) {
            case Dice.D10:
                result += "1d10 ";
                break;
        }
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
