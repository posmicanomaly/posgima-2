package posgima2;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/2/2015.
 */
public abstract class Entity {
    protected Tile tile;
    protected char glyph;
    protected int y;
    protected int x;
    protected int currentHP;
    protected int maxHP;
    protected int strength;
    private int agility;
    private int dexterity;
    private Tile targetTile;
    protected boolean alive;

    protected boolean attackedThisTurn;
    protected ArrayList<Item> inventory;
    protected String name;

    public Entity(char glyph) {
        this.glyph = glyph;
        tile = null;
        y = 0;
        x = 0;
        attackedThisTurn = false;
        inventory = new ArrayList<Item>();
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

    public Tile getTile() {
        return tile;
    }

    public char getGlyph() {
        return glyph;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveToTileImmediately(Tile tile) {
        tile.addEntity(this);
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setTargetTile(Tile targetTile) {
        this.targetTile = targetTile;
    }

    public Tile getTargetTile() {
        return targetTile;
    }

    private void applyDamage(int damage) {
        currentHP -= damage;
        if(currentHP < 1) {
            alive = false;
        }
    }

    private void sendCombatMessage(Entity attacker, Entity defender) {
        WindowFrame.writeConsole("/combat/" + attacker + " hit " + defender + " for " + attacker.strength + ".");
    }

    public void meleeAttack(Entity entity, boolean defenderCanAttack) {
        if(canAttack()) {
            if(entity.canAttack() && defenderCanAttack) {
                int goesFirst = (int)(Math.random() * 2);
                switch (goesFirst) {
                    case 0:
                        entity.applyDamage(this.strength);
                        this.attackedThisTurn = true;
                        sendCombatMessage(this, entity);
                        if(entity.isAlive()) {
                            this.applyDamage(entity.strength);
                            entity.attackedThisTurn = true;
                            sendCombatMessage(entity, this);
                        }
                        break;
                    case 1:
                        this.applyDamage(entity.strength);
                        entity.attackedThisTurn = true;
                        sendCombatMessage(entity, this);
                        if(isAlive()) {
                            entity.applyDamage(this.strength);
                            this.attackedThisTurn = true;
                            sendCombatMessage(this, entity);
                        }
                        break;
                }

            } else {
                //posgima2.WindowFrame.writeConsole("/info/" + entity + " could not defend.");
                entity.applyDamage(this.strength);
                attackedThisTurn = true;
                sendCombatMessage(this, entity);
            }
        } else {
            //posgima2.WindowFrame.writeConsole("/info/" + this + " could not attack.");
        }
    }

    private boolean canAttack() {
        return !attackedThisTurn;
    }


    public boolean isAlive() {
        return alive;
    }

    public void resetTurnTick() {
        attackedThisTurn = false;
    }

    public void regen() {
        if(currentHP < maxHP) {
            currentHP++;
        }
    }

    public void addInventory(Item i) {
        inventory.add(i);
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if(name != null)
            return name;
        return String.valueOf(hashCode());
    }
}
