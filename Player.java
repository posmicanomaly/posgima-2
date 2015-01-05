/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class Player extends Entity{

    private int state;

    public Player(char glyph) {
        super(glyph);
        state = Game.STATE_READY;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
