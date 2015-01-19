package posgima2.world;

import posgima2.game.Game;
import posgima2.item.Item;
import posgima2.item.container.Corpse;
import posgima2.misc.Dice;
import posgima2.swing.WindowFrame;

/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class Player extends Entity{

    /**
     * STATE_x
     * posgima2.world.Player states such as "ready", "door closed", "item pickup"
     * to determine which key options to accept, and control game flow.
     */
    public static final int STATE_CANCEL = -1;
    public static final int STATE_READY = 0;
    public static final int STATE_DOOR_CLOSED = 1;
    public static final int STATE_ITEM_PICKUP = 3;
    public static final int STATE_LOOTED = 5;
    public static final int STATE_GAME_OVER = 2;
    public static final int STATE_LOOTING = 4;
    public static final int STATE_CLOSE_DOOR_ATTEMPT = 6;
    public static final int STATE_SHOOTING = 7;
    public static final int STATE_LOOKING = 8;
    private final int unarmedDie;
    private int state;
    private boolean justLooted;
    private int satiation;

    public Player(char glyph) {
        super(glyph);
        alive = true;
        level = 1;
        expMod = 1;
        baseHitDie = Dice.D10;
        unarmedDie = Dice.D1;
        attackDie = unarmedDie;
        armorClass = 1;
        rollAttributes();
        currentHP = maxHP;
        experience = 0;
        satiation = 100;

        //addInventory(new Sword(RenderPanel.WEAPON, Dice.D4, 0, 0, 0, 0), true);
        //addInventory(new Plate(RenderPanel.ITEM, 5), true);

        state = STATE_READY;
    }

    @Override
    public void die() {
        //WindowFrame.writeConsole("Such a sad thing that your journey has ended here.");
    }

    public int eat(Corpse food) {
        if(inInventory(food)) {
            satiation += food.getSatiation();
            if (satiation > Game.MAX_SATIATION) {
                satiation = Game.MAX_SATIATION;
            }
            inventory.remove(food);
            if (satiation > 0) {
                return Game.PLAYER_ATE_WELL;
            } else if (satiation < 0) {
                return Game.PLAYER_ATE_POISON;
            }
        }
        return Game.PLAYER_HAS_NO_FOOD;
    }

    private boolean inInventory(Item item) {
        if(inventory.contains(item)) {
            return true;
        }
        return false;
    }


    public Corpse getNextCorpseTest() {
        for(Item i : inventory) {
            if(i instanceof Corpse) {
                return (Corpse) i;
            }
        }
        return null;
    }

    public boolean hasCorpseInInventory() {
        for(Item i : inventory) {
            if(i instanceof Corpse) {
                return true;
            }
        }
        return false;
    }



    private void rollAttributes() {
        strength = 10;
        agility = 10;
        dexterity = 10;
        constitution = 10;
        maxHP = 16;
    }

    public void modifyExperience(int amount) {
        experience += amount;
        if(amount < 0) {
            WindowFrame.writeConsole("You LOSE experience!");
        }
        while(checkLevelUp()) {
            levelUp();
            WindowFrame.writeConsole("/info/You level up! You are now level " + level);
            WindowFrame.writeConsole("/success/Strength increased to " + strength);
            WindowFrame.writeConsole("/success/Maximum HP increased to " + maxHP);
        }
    }

    private boolean checkLevelUp() {
        if(experience >= (level * (100 * (level * level))))
            return true;
        return false;
    }

    public void levelUp() {
        level++;
        strength += 2;
        constitution += 2;
        agility += 2;
        dexterity += 2;
        maxHP += Dice.roll(baseHitDie) + constitution;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setJustLooted(boolean justLooted) {
        this.justLooted = justLooted;
    }

    public boolean getJustLooted() {
        return justLooted;
    }

    public int getSatiation() {
        return satiation;
    }

    public void modifySatiation(int hungerHitMelee) {
        satiation -= hungerHitMelee;
        if(satiation < 0) {
            satiation = 0;
        }
    }
}
