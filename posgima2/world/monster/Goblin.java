package posgima2.world.monster;

import posgima2.misc.Dice;

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
        strength = 7;
        agility = 7;
        dexterity = 7;
        constitution = 7;
        expMod = 1.0;

        corpseSatiation = 70;

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
