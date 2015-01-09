package posgima2.combat;

import posgima2.misc.Dice;
import posgima2.swing.WindowFrame;
import posgima2.world.Entity;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public abstract class Melee {
    /*
    Attempt to attack the defender
     */
    public static void attemptMeleeAttack(Entity attacker, Entity defender) {
        /*
        roll 1d20 + strength for melee
         */
        int hitRoll = Dice.roll(Dice.D20) + attacker.getStrength();

        /*
        If hitRoll less than defender's AC or hitroll equals 1, miss
         */
        if (hitRoll < defender.getTotalArmorClass() || hitRoll == 1) {
            WindowFrame.writeConsole("/combat/" + attacker + " missed " + defender + ".");
        }
        /*
        If hitroll greater than defender's AC or hitroll equals 20, hit
         */
        else if(hitRoll > defender.getTotalArmorClass() || hitRoll == 20) {
            int damageRoll = Dice.roll(attacker.getBaseHitDie()) + (attacker.getLevel() / attacker.getStrength()) +
                    attacker.getDamageBonus();
            defender.applyDamage(damageRoll);
            /*
            Check death
             */
            if(!defender.isAlive()) {
                defender.die();
            }

            attacker.setAttackedThisTurn(true);
            WindowFrame.writeConsole("/combat/" + attacker + " hit " + defender + " for " + damageRoll + ".");
        }
    }

    public static void meleeCombat(Entity one, Entity two, boolean twoCanAttack) {
        Entity first = null;
        Entity second = null;

        if (twoCanAttack) {
            int speedRollOne = Dice.roll(Dice.D20) + one.getAgility();
            int speedRollTwo = Dice.roll(Dice.D20) + two.getAgility();
            if (speedRollOne > speedRollTwo) {
                first = one;
                second = two;
            } else {
                first = two;
                second = one;
            }
            if (first.canAttack()) {
                attemptMeleeAttack(first, second);
            }

            if (second.canAttack()) {
                attemptMeleeAttack(second, first);
            }

        } else {
            attemptMeleeAttack(one, two);
        }
    }
}
