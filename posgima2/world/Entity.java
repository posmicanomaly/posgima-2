package posgima2.world;

import posgima2.game.Game;
import posgima2.item.Item;
import posgima2.item.armor.Armor;
import posgima2.item.armor.NullArmor;
import posgima2.item.weapon.Weapon;
import posgima2.misc.Dice;
import posgima2.swing.RenderPanel;
import posgima2.swing.WindowFrame;
import posgima2.world.dungeonSystem.dungeon.Tile;

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

    protected int baseHitDie;
    protected int attackDie;

    /*
    Strength is primary melee attribute.
     */
    protected int strength;

    protected int armorClass;

    protected int level;
    protected int experience;

    protected int agility;
    protected int dexterity;
    protected int constitution;

    protected int damageBonus;
    private Tile targetTile;
    protected boolean alive;

    protected boolean attackedThisTurn;
    protected ArrayList<Item> inventory;
    protected String name;

    protected Armor armorSlot;

    public Entity(char glyph) {
        this.glyph = glyph;
        tile = null;
        y = 0;
        x = 0;
        attackedThisTurn = false;
        inventory = new ArrayList<Item>();
        armorClass = 10;
        armorSlot = new NullArmor(RenderPanel.ITEM);
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



    /*
    Attempt to attack the defender
     */
    private void attemptMeleeAttack(Entity defender) {
        /*
        roll 1d20 + strength for melee
         */
        int hitRoll = Dice.roll(Dice.D20) + this.strength;

        /*
        If hitRoll less than defender's AC or hitroll equals 1, miss
         */
        if (hitRoll < defender.getTotalArmorClass() || hitRoll == 1) {
            WindowFrame.writeConsole("/combat/" + this + " misses " + defender + ".");
        }
        /*
        If hitroll greater than defender's AC or hitroll equals 20, hit
         */
        else if(hitRoll > defender.getTotalArmorClass() || hitRoll == 20) {
            int damageRoll = Dice.roll(this.baseHitDie) + (level / strength) + damageBonus;
            defender.applyDamage(damageRoll);
            this.attackedThisTurn = true;
            WindowFrame.writeConsole("/combat/" + this + " hit " + defender + " for " + damageRoll + ".");
        }
    }

    public void meleeAttack(Entity target, boolean defenderCanAttack) {
        if (canAttack()) {
            if (target.canAttack() && defenderCanAttack) {
                int goesFirst = (int) (Math.random() * 2);
                switch (goesFirst) {
                    case 0:
                        attemptMeleeAttack(target);
                        if (target.isAlive()) {
                            target.attemptMeleeAttack(this);
                        }
                        break;
                    case 1:
                        target.attemptMeleeAttack(this);
                        if (isAlive()) {
                            attemptMeleeAttack(target);
                        }
                        break;
                }

            } else {
                attemptMeleeAttack(target);
            }
        } else {
            //posgima2.swing.WindowFrame.writeConsole("/info/" + this + " could not attack.");
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
        /*
        Auto equip best armor for now
         */
        if(i instanceof Armor) {
            WindowFrame.writeConsole("picked up armor");
            Armor a = (Armor)i;
            if(a.getArmorClass() > armorSlot.getArmorClass()) {
                WindowFrame.writeConsole("You equipped the armor");
                armorSlot = a;
            }
        } else if(i instanceof Weapon) {
            WindowFrame.writeConsole("picked up weapon");
            Weapon w = (Weapon)i;
            if(w.getHitDie() > attackDie) {
                attackDie = w.getHitDie();
                damageBonus = w.getDamageBonus();
                WindowFrame.writeConsole("You equipped the " + w);
            } else if(w.getHitDie() == attackDie) {
                if(damageBonus < w.getDamageBonus()) {
                    attackDie = w.getHitDie();
                    damageBonus = w.getDamageBonus();
                    WindowFrame.writeConsole("You equipped the " + w);
                }
            }
        }
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

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public int getConstitution() {
        return constitution;
    }

    public int getTotalArmorClass() {
        return armorClass + armorSlot.getArmorClass();
    }
}
