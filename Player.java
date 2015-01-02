/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class Player {
    private int x;
    private int y;

    public Player(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public boolean move(int dir) {
        switch(dir) {
            case Game.UP:
                y--;
                break;
            case Game.DOWN:
                y++;
                break;
            case Game.LEFT:
                x--;
                break;
            case Game.RIGHT:
                x++;
                break;
        }
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
