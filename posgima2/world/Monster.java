package posgima2.world;

import posgima2.item.container.Corpse;
import posgima2.misc.Dice;
import posgima2.world.dungeonSystem.dungeon.FieldOfView;
import posgima2.misc.Vector2i;
import posgima2.world.dungeonSystem.dungeon.Dungeon;

import java.util.LinkedList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public class Monster extends Entity{
    private boolean[][] visibility;
    private boolean aggroPlayer;
    private int aggroTurnStart;

    private LinkedList<Vector2i> moveQueue;
    private int age;

    public Monster(char glyph, int level) {
        super(glyph);
        moveQueue = new LinkedList<Vector2i>();
        aggroPlayer = false;
        aggroTurnStart = 0;
        alive = true;
        this.level = level;
        armorClass = 15;
        baseHitDie = Dice.D4;
        attackDie = baseHitDie;
        damageBonus = 0;
        strength = level * 11;
        agility = 0;
        dexterity = level * 13;
        constitution = level * 12;

        maxHP = (level * Dice.roll(baseHitDie)) + (constitution * level);
        currentHP = maxHP;
        age = 0;
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

    public boolean die() {
        alive = false;
        this.tile.addItem(new Corpse('}', this));
        this.tile.remove(this);
        //posgima2.swing.WindowFrame.setupWindow.println(this + " died.");
        return true;
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
}
