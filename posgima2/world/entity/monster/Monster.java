package posgima2.world.entity.monster;

import posgima2.item.Item;
import posgima2.item.container.Corpse;
import posgima2.swing.RenderPanel;
import posgima2.world.Entity;
import posgima2.world.dungeonSystem.dungeon.FieldOfView;
import posgima2.misc.Vector2i;
import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.entity.BaseStats;

import java.util.LinkedList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public abstract class Monster extends Entity {
    private boolean[][] visibility;
    private boolean aggroPlayer;
    private int aggroTurnStart;
    protected int corpseSatiation;

    public State chooseRandomState() {
        int choice = (int)(Math.random() * State.values().length);
        return State.values()[choice];
    }

    public enum State {
        idle, wander, patrol
    }

    private State currentState;
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

        /*
        Set base stats
         */
        strength = BaseStats.STRENGTH;
        agility = BaseStats.AGILITY;
        dexterity = BaseStats.DEXTERITY;
        constitution = BaseStats.CONSTITUTION;

        currentState = State.wander;
    }

    protected abstract void initStats();
    protected abstract void setStatsBasedOnLevel();

    public State getCurrentState() {
        return currentState;
    }

    public void setState(State state) {
        currentState = state;
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
