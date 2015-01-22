package posgima2.game;

import posgima2.misc.Vector2i;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/21/2015.
 */
public class TargetCursor extends LookCursor {
    private ArrayList<Vector2i> lineOfSight;
    private int maxRange;

    public TargetCursor(int y, int x, int maxRange) {
        super(y, x);
        this.maxRange = maxRange;
        lineOfSight = new ArrayList<>();
    }

    public void setLineOfSight(ArrayList<Vector2i> line) {
        this.lineOfSight = line;
    }

    public ArrayList<Vector2i> getLineOfSight() {
        return lineOfSight;
    }

    public int getMaxRange() {
        return maxRange;
    }
}
