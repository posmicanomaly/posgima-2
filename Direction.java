/**
 * Created by Jesse Pospisil on 12/31/2014.
 */
public class Direction {
    private int yMod;
    private int xMod;

    public Direction(int yMod, int xMod) {
        this.yMod = yMod;
        this.xMod = xMod;
    }

    public int getyMod() {
        return yMod;
    }

    public int getxMod() {
        return xMod;
    }
}
