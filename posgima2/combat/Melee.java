package posgima2.combat;

import posgima2.misc.Dice;
import posgima2.swing.WindowFrame;
import posgima2.world.Entity;
import posgima2.world.Player;
import posgima2.world.SkillCaps;

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
        NEW:
        AC for damage reduction
        Agility and Dexterity for missing.
        Agile = harder to hit
        Dextrous = Easier to land hit
        % base, 0-100, so 10 agility = 10% chance to dodge, 10% dexterity = 10% chance to hit.
        This might result in a lot of misses early on.
         */

        /**
         * THE LOWER THE BETTER!!!!!!!
         */
        int attackDexterityRoll = (int)(Math.random() * 100) - attacker.getMeleeSkill();
        int defendAgilityRoll = (int)(Math.random() * 100);
//        WindowFrame.writeConsole(attacker + " dex roll: " + attackDexterityRoll + "(" + attacker.getDexterity() + ")");
//        WindowFrame.writeConsole(defender + " agi roll: " + defendAgilityRoll + "(" + defender.getAgility() + ")");
        boolean agilityCheck = false;
        if(defendAgilityRoll < defender.getAgility()) {
            /*
            e.g. roll: 10, agility 23, successful agility check
             */
            agilityCheck = true;
        }
        boolean dexterityCheck = false;
        if(attackDexterityRoll < attacker.getDexterity()) {
            dexterityCheck = true;
        }
        /*
        If defender was agile enough, and attacker failed dexterity check
        MISS
         */
        if((agilityCheck && !dexterityCheck) ||
                !agilityCheck && !dexterityCheck){
            if(ANNOUNCE) {
                WindowFrame.writeConsole("/combat/" + attacker + " missed " + defender + ".");
            }
            attacker.setAttackedThisTurn(true);
        }
        /*
        If attacker was dextrous, and defender not agile, or if they both passed, the more dextrous will land a hit
         */
        else if((dexterityCheck && !agilityCheck) ||
                dexterityCheck && agilityCheck) {
            int str = attacker.getStrength();
            int dmgbonus = attacker.getDamageBonus();
            int minHitDamage = attacker.getMinHitDamage();
            int maxHitDamage = attacker.getMaxHitDamage();
            int strComponent = (str / 2);
            // Test, remove str
            strComponent = 0;
            int damageRoll = (int) ((Math.random() * (maxHitDamage - minHitDamage)) + minHitDamage + strComponent + dmgbonus);
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

        meleeSkillUpTest(attacker);
    }

    private static void meleeSkillUpTest(Entity attacker) {
        if(attacker.getMeleeSkill() >= SkillCaps.MELEE) {
            return;
        }
        int chanceToSkillUp = (int)(Math.random() * 100);
        if(chanceToSkillUp < 10) {
            //Only players can skill up right now
            if(attacker instanceof Player) {
                attacker.modifyMeleeSkill(1);
                if(ANNOUNCE) {
                    WindowFrame.writeConsole("You become better at melee(" + attacker.getMeleeSkill() + ")!");
                }
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
