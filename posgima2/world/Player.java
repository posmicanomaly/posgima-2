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

    public void setGameOverFlag(GameOver reason) {
        gameOverReason = reason;
    }

    public GameOver getGameOverFlag() {
        return gameOverReason;
    }

    /**
     * STATE_x
     * posgima2.world.Player states such as "ready", "door closed", "item pickup"
     * to determine which key options to accept, and control game flow.
     */
    public static enum STATE {
        CANCEL, READY, DOOR_CLOSED, ITEM_PICKUP, LOOTED, GAME_OVER, LOOTING, CLOSE_DOOR_ATTEMPT, SHOOTING, LOOKING
    }

    public static enum GameOver {
        monster, starvation, arrow
    }
    private STATE state;
    private boolean justLooted;
    private int satiation;
    private GameOver gameOverReason;

    public Player(char glyph) {
        super(glyph);
        alive = true;
        level = 1;
        expMod = 1;
        minHitDamage = 1;
        maxHitDamage = 1;
        armorClass = 1;
        rollAttributes();
        currentHP = maxHP;
        experience = 0;
        satiation = Game.MAX_SATIATION;

        //addInventory(new Sword(RenderPanel.WEAPON, Dice.D4, 0, 0, 0, 0), true);
        //addInventory(new Plate(RenderPanel.ITEM, 5), true);

        state = STATE.READY;
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
            } else if(satiation < 0) {
                satiation = 0;
            }

            inventory.remove(food);

            if (food.getSatiation() > 0) {
                return Game.PLAYER_ATE_WELL;
            } else if (food.getSatiation() < 0) {
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
        maxHP += Dice.roll(10) + constitution;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
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
