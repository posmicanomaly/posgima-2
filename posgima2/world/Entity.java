package posgima2.world;

import posgima2.combat.Melee;
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
    protected boolean alive;
    protected boolean attackedThisTurn;
    protected ArrayList<Item> inventory;
    protected String name;
    protected Armor armorSlot;
    private Tile targetTile;

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

    public int getBaseHitDie() {
        return baseHitDie;
    }

    public int getAttackDie() {
        return attackDie;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public Armor getArmorSlot() {
        return armorSlot;
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

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public char getGlyph() {
        return glyph;
    }

    public void moveToTileImmediately(Tile tile) {
        tile.addEntity(this);
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

    public Tile getTargetTile() {
        return targetTile;
    }

    public void setTargetTile(Tile targetTile) {
        this.targetTile = targetTile;
    }

    public void applyDamage(int damage) {
        currentHP -= damage;
        if(currentHP < 1) {
            alive = false;
        }
    }

//    public void meleeAttack(Entity attacker, Entity target, boolean defenderCanAttack) {
//        /*
//        First check if attacker can attack
//         */
//        if (attacker.canAttack()) {
//            if (target.canAttack() && defenderCanAttack) {
//                int goesFirst = (int) (Math.random() * 2);
//                switch (goesFirst) {
//                    case 0:
//                        Melee.attemptMeleeAttack(attacker, target);
//                        if (target.isAlive()) {
//                            Melee.attemptMeleeAttack(target, attacker);
//                        }
//                        break;
//                    case 1:
//                        Melee.attemptMeleeAttack(target, attacker);
//                        if (isAlive()) {
//                            Melee.attemptMeleeAttack(attacker, target);
//                        }
//                        break;
//                }
//
//            } else {
//                Melee.attemptMeleeAttack();
//            }
//        } else {
//            //posgima2.swing.WindowFrame.writeConsole("/info/" + this + " could not attack.");
//        }
//    }

    public boolean canAttack() {
        return !attackedThisTurn && isAlive();
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

    public void setAttackedThisTurn(boolean b) {
        attackedThisTurn = b;
    }

    public abstract void die();

}
