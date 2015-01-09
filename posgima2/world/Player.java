package posgima2.world;

import posgima2.game.Game;
import posgima2.misc.Dice;
import posgima2.swing.WindowFrame;

/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class Player extends Entity{

    private int state;
    private boolean justLooted;

    public Player(char glyph) {
        super(glyph);
        alive = true;
        level = 1;
        baseHitDie = Dice.D10;
        attackDie = baseHitDie;
        rollAttributes();
        currentHP = maxHP;
        experience = 0;

        state = Game.STATE_READY;
    }

    @Override
    public void die() {
        WindowFrame.writeConsole("Such a sad thing that your journey has ended here.");
    }

    private void rollAttributes() {
        strength = Dice.roll(3, Dice.D6);
        agility = Dice.roll(3, Dice.D6);
        dexterity = Dice.roll(3, Dice.D6);
        constitution = Dice.roll(3, Dice.D6);
        switch(baseHitDie) {
            case Dice.D10:
                maxHP = 10 + constitution + 99999;
        }
    }

    public void modifyExperience(int amount) {
        experience += amount;
        if(amount < 0) {
            WindowFrame.writeConsole("You LOSE experience!");
        }
        while(checkLevelUp()) {
            levelUp();
        }
    }

    private boolean checkLevelUp() {
        if(experience >= (level * (100 * (level * level))))
            return true;
        return false;
    }

    public void levelUp() {
        level++;
        maxHP += Dice.roll(baseHitDie) + constitution;
        WindowFrame.writeConsole("/info/You level up! You are now level " + level);
        //WindowFrame.writeConsole("/success/Strength increased to " + strength);
        WindowFrame.writeConsole("/success/Maximum HP increased to " + maxHP);
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

}
