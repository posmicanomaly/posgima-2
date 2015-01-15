package posgima2.world.monster;

import posgima2.misc.Dice;

/**
 * Created by Jesse Pospisil on 1/14/2015.
 */
public class Rat extends Monster {
    public Rat(int level) {
        super('r', level);

        armorClass = 1;
        baseHitDie = Dice.D4;
        attackDie = baseHitDie;
        damageBonus = 0;
        strength = 4;
        agility = 4;
        dexterity = 4;
        constitution = 4;
        expMod = 0.6;

        maxHP = 6;
        for(int i = 1; i < level; i++) {
            armorClass += 2;
            strength += 2;
            agility += 2;
            dexterity += 2;
            constitution += 2;
            maxHP += Dice.roll(baseHitDie) + constitution;
        }
        currentHP = maxHP;

        name = "a rat";
        //addInventory(new Plate(RenderPanel.ITEM, 3), true);
    }
}
