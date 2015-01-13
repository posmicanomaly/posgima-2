package posgima2.world.monster;

import posgima2.misc.Dice;


import posgima2.item.armor.Plate;
import posgima2.misc.Dice;
import posgima2.swing.RenderPanel;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public class Kobold extends Monster {

    public Kobold(int level) {
        super('k', level);

        armorClass = 1;
        baseHitDie = Dice.D4;
        attackDie = baseHitDie;
        damageBonus = 0;
        strength = 1;
        agility = 1;
        dexterity = 1;
        constitution = 1;
        ttk = 0.7;

        maxHP = 10;
        for(int i = 1; i < level; i++) {
            maxHP += Dice.roll(baseHitDie) + constitution;
            strength += 1;
            agility += 1;
            dexterity += 1;
            constitution += 1;
        }
        currentHP = maxHP;

        name = "a kobold";
        //addInventory(new Plate(RenderPanel.ITEM, 3), true);
    }
}