package posgima2.world.monster;

import posgima2.misc.Dice;

/**
 * Created by Jesse Pospisil on 1/14/2015.
 */
public class Rat extends Monster {
    public Rat(int level) {
        super('r', level);

        initStats();
        setStatsBasedOnLevel();
        name = "a rat";
        //addInventory(new Plate(RenderPanel.ITEM, 3), true);
    }

    @Override
    public void setStatsBasedOnLevel() {
        maxHP = 6;
        for(int i = 1; i < level; i++) {
            armorClass += 2;
            strength += 2;
            agility += 2;
            dexterity += 2;
            constitution += 2;
            maxHP += Dice.roll(4) + constitution;
        }
        currentHP = maxHP;
    }

    @Override
    protected void initStats() {
        armorClass = 1;
        minHitDamage = 1;
        maxHitDamage = 4;
        damageBonus = 0;

        strength -= 6;
        agility -= 6;
        dexterity -= 6;
        constitution -= 6;

        expMod = 0.6;
        corpseSatiation = 40;
    }
}
