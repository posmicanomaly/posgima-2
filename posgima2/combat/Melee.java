package posgima2.combat;

import posgima2.misc.Dice;
import posgima2.swing.WindowFrame;
import posgima2.world.Entity;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public abstract class Melee {
    public static boolean ANNOUNCE = true;
    public static boolean SYSTEM_CONSOLE_OUTPUT = false;
    /*
    Attempt to attack the defender
     */
    public static void attemptMeleeAttack(Entity attacker, Entity defender) {
        /*
        roll 1d20 + strength for melee
         */
        int hitRoll = Dice.roll(Dice.D20) + (attacker.getStrength() / 2);

        /*
        If hitRoll less than defender's AC or hitroll equals 1, miss
         */

        if (hitRoll < defender.getTotalArmorClass() + defender.getAgility() || hitRoll == 1) {
            if(ANNOUNCE) {
                WindowFrame.writeConsole("/combat/" + attacker + " missed " + defender + ".");
            }
            if(SYSTEM_CONSOLE_OUTPUT) {
                System.out.println(attacker + " missed " + defender);
            }
            attacker.setAttackedThisTurn(true);
        }

        /*
        If hitroll greater than defender's AC or hitroll equals 20, hit
         */
        else if(hitRoll > defender.getTotalArmorClass() || hitRoll == 20) {

            int str = attacker.getStrength();
            int dmgbonus = attacker.getDamageBonus();
            int hitDie = attacker.getAttackDie();

            int damageRoll = Dice.roll(hitDie) + (str / 2) + dmgbonus;
            int defenseRoll = (int)(Math.random() * defender.getTotalArmorClass()) + 1;
            int actualDamage = damageRoll - defenseRoll;
            /*
            actual damage guaranteed to be 1
             */
            if(actualDamage < 1) {
                actualDamage = 1;
            }

            defender.applyDamage(actualDamage);
            /*
            Check death
             */
            if(!defender.isAlive()) {
                defender.die();
            }

            attacker.setAttackedThisTurn(true);
            String output = attacker + " hit " + defender + " for " + actualDamage + "(" +
                    (damageRoll - actualDamage) + " absorbed). (" + defender
                    .getCurrentHP() + "/" + defender.getMaxHP() + ") [L" + defender.getLevel() + "]";

            if(ANNOUNCE) {
                WindowFrame.writeConsole("/combat/" + output);
            }
            if(SYSTEM_CONSOLE_OUTPUT) {
                System.out.println(output);
            }
        }
    }

    public static void meleeCombat(Entity one, Entity two, boolean twoCanAttack) {
        Entity first = null;
        Entity second = null;

        if (twoCanAttack) {
            int speedRollOne = Dice.roll(Dice.D20) + one.getAgility() / 2;
            int speedRollTwo = Dice.roll(Dice.D20) + two.getAgility() / 2;
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
            if(one.canAttack()) {
                attemptMeleeAttack(one, two);
            }
        }
    }
}
