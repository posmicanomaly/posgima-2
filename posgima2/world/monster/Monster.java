package posgima2.world.monster;

import posgima2.item.Item;
import posgima2.item.container.Corpse;
import posgima2.swing.RenderPanel;
import posgima2.world.Entity;
import posgima2.world.dungeonSystem.dungeon.FieldOfView;
import posgima2.misc.Vector2i;
import posgima2.world.dungeonSystem.dungeon.Dungeon;

import java.util.LinkedList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public abstract class Monster extends Entity {
    private boolean[][] visibility;
    private boolean aggroPlayer;
    private int aggroTurnStart;
    protected int corpseSatiation;

    private LinkedList<Vector2i> moveQueue;
    protected int age;
    protected boolean ranged;
    protected int range;

    public Monster(char glyph, int level) {
        super(glyph);
        moveQueue = new LinkedList<Vector2i>();
        aggroPlayer = false;
        aggroTurnStart = 0;
        alive = true;
        this.level = level;
        age = 0;
        expMod = 1;
        // default
        corpseSatiation = 10;
    }

    public boolean[][] getVisibility() {
        return visibility;
    }

    public void calculateVisibility(Dungeon dungeon) {
        visibility = new boolean[dungeon.getMAP_ROWS()][dungeon.getMAP_COLS()];
        for(Vector2i v : FieldOfView.bresenhamFov(dungeon.getTileMap(), y, x, 0)) {
            visibility[v.getY()][v.getX()] = true;
        }
    }

    @Override
    public void die() {
        alive = false;
        if(tile != null) {
            Item corpse = new Corpse(RenderPanel.CORPSE, this);
            this.tile.addItem(corpse);
            for(Item i : inventory) {
                this.tile.addItem(i);
            }
            this.tile.remove(this);
        } else {
            //System.out.println("no tile");
        }
        //posgima2.swing.WindowFrame.setupWindow.println(this + " died.");
    }

    public void setAggro(boolean b, int turn) {
        aggroPlayer = b;
        if(b) {
            aggroTurnStart = turn;
        }
    }

    public boolean isAggroPlayer() {
        return aggroPlayer;
    }

    public int getAggroTurnStart() {
        return aggroTurnStart;
    }


    public LinkedList<Vector2i> getMoveQueue() {
        return moveQueue;
    }

    public int getAge() {
        return age;
    }

    public void addAge(int i) {
        age += i;
    }

    public int getCorpseSatiation() {
        return corpseSatiation;
    }

    public boolean isRanged() {
        return ranged;
    }

    public int getRange() {
        return range;
    }
}
