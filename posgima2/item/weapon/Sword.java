package posgima2.item.weapon;

import posgima2.item.Item;
import posgima2.swing.RenderPanel;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class Sword extends Weapon {

    public Sword(int hitDie, int damageBonus, int strength, int dexterity, int agility) {
        super(RenderPanel.WEAPON);
        type = Item.WEAPON;
        this.hitDie = hitDie;
        this.damageBonus= damageBonus;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
    }
}
