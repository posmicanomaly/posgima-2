package posgima2;

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

        state = Game.STATE_READY;
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
