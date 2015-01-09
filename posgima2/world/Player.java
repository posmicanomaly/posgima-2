package posgima2.world;

import posgima2.game.Game;
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
        strength = 4;
        maxHP = 16;
        currentHP = maxHP;
        level = 1;
        experience = 0;
        state = Game.STATE_READY;
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
        maxHP += level * 2;
        strength += level * 1;
        WindowFrame.writeConsole("/warning/You level up! You are now level " + level);
        WindowFrame.writeConsole("/success/Strength increased to " + strength);
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
