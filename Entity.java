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

    public Entity(char glyph) {
        this.glyph = glyph;
        tile = null;
        y = 0;
        x = 0;
        attackedThisTurn = false;
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

    public void meleeAttack(Entity entity, boolean defenderCanAttack) {
        if(canAttack()) {
            if(entity.canAttack() && defenderCanAttack) {
                int goesFirst = (int)(Math.random() * 2);
                switch (goesFirst) {
                    case 0:
                        entity.applyDamage(this.strength);
                        this.attackedThisTurn = true;
                        WindowFrame.writeConsole("/combat/" + this + " hit " + entity + " for " + this.strength);
                        if(entity.isAlive()) {
                            this.applyDamage(entity.strength);
                            entity.attackedThisTurn = true;
                            WindowFrame.writeConsole("/combat/" + entity + " hit " + this + " for " + entity.strength);
                        }
                        break;
                    case 1:
                        this.applyDamage(entity.strength);
                        entity.attackedThisTurn = true;
                        WindowFrame.writeConsole("/combat/" + entity + " hit " + this + " for " + entity.strength);
                        if(isAlive()) {
                            entity.applyDamage(this.strength);
                            this.attackedThisTurn = true;
                            WindowFrame.writeConsole("/combat/" + this + " hit " + entity + " for " + this.strength);
                        }
                        break;
                }
            } else {
                //WindowFrame.writeConsole("/info/" + entity + " could not defend.");
                entity.applyDamage(this.strength);
                attackedThisTurn = true;
                WindowFrame.writeConsole("/combat/" + this + " hit " + entity + " for " + this.strength);
            }
        } else {
            //WindowFrame.writeConsole("/info/" + this + " could not attack.");
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
}
