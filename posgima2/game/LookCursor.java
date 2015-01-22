package posgima2.game;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public class LookCursor {
    protected int x;
    protected int y;

    public LookCursor(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public void setLocation(int targetY, int targetX) {
        y = targetY;
        x = targetX;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
