package posgima2.world.entity.monster;

import posgima2.misc.Dice;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public class Goblin extends Monster{

    public Goblin(int level) {
        super('g', level);
        initStats();
        setStatsBasedOnLevel();
        name = "a goblin";
    }

    @Override
    public void setStatsBasedOnLevel() {
        maxHP = 8;
        for(int i = 1; i < level; i++) {
            armorClass += 2;
            strength += 2;
            agility += 2;
            dexterity += 2;
            constitution += 2;
            maxHP += Dice.roll(4) + constitution;

            minHitDamage += i * 2;
            maxHitDamage = minHitDamage * 2;
        }
        currentHP = maxHP;
    }

    @Override
    protected void initStats() {
        armorClass = 2;
        minHitDamage = 2;
        maxHitDamage = 6;
        damageBonus = 0;

        strength -= 3;
        agility -= 3;
        dexterity -= 3;
        constitution -= 3;

        expMod = 1.0;
        corpseSatiation = 70;
    }
}
