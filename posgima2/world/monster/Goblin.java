package posgima2.world.monster;

import posgima2.item.armor.Plate;
import posgima2.misc.Dice;
import posgima2.swing.RenderPanel;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public class Goblin extends Monster {

    public Goblin(int level) {
        super('g', level);

        armorClass = 2;
        baseHitDie = Dice.D4;
        attackDie = baseHitDie;
        damageBonus = 0;
        strength = 8;
        agility = 8;
        dexterity = 8;
        constitution = 8;
        ttk = 0.7;

        maxHP = 8;
        for(int i = 1; i < level; i++) {
            armorClass += 2;
            strength += 2;
            agility += 2;
            dexterity += 2;
            constitution += 2;
            maxHP += Dice.roll(baseHitDie) + constitution;
        }
        currentHP = maxHP;

        name = "a goblin";
        //addInventory(new Plate(RenderPanel.ITEM, 3), true);
    }
}
