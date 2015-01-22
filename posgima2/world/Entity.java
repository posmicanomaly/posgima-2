package posgima2.world;

import posgima2.game.Game;
import posgima2.item.Item;
import posgima2.item.armor.Armor;
import posgima2.item.armor.NullArmor;
import posgima2.item.potion.Potion;
import posgima2.item.weapon.NullWeapon;
import posgima2.item.weapon.Weapon;
import posgima2.swing.RenderPanel;
import posgima2.swing.WindowFrame;
import posgima2.world.dungeonSystem.dungeon.Tile;
import posgima2.world.entity.trait.Invincible;
import posgima2.world.entity.trait.Trait;

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
    protected int minHitDamage;
    protected int maxHitDamage;
    /*
    expMod or time to kill, is a measure of base toughness in attack and hp
     */
    protected double expMod;
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

    protected int meleeSkill;

    protected boolean alive;
    protected boolean attackedThisTurn;
    protected ArrayList<Item> inventory;
    protected String name;
    /*
    Equipment slots
     */
    protected Armor chestSlot;
    protected Armor headSlot;
    protected Armor handSlot;
    protected Armor legSlot;
    protected Armor armSlot;

    protected Weapon mainHand;
    protected Weapon offHand;

    protected ArrayList<Trait> traits;

    private Tile targetTile;


    public Entity(char glyph) {
        this.glyph = glyph;
        tile = null;
        y = 0;
        x = 0;
        attackedThisTurn = false;
        inventory = new ArrayList<Item>();
        armorClass = 10;
        chestSlot = new NullArmor(RenderPanel.ITEM);
        headSlot = new NullArmor(RenderPanel.ITEM);
        handSlot = new NullArmor(RenderPanel.ITEM);
        legSlot = new NullArmor(RenderPanel.ITEM);
        armSlot = new NullArmor(RenderPanel.ITEM);

        mainHand = new NullWeapon(RenderPanel.WEAPON);
        offHand = new NullWeapon(RenderPanel.WEAPON);

        traits = new ArrayList<>();
    }





    public int getDamageBonus() {
        return damageBonus;
    }

    public Armor getChestSlot() {
        return chestSlot;
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
            if(isInvincible()) {
                currentHP = maxHP;
            } else {
                alive = false;
            }
        }
    }


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

    public void addInventory(Item i, boolean silent) {
        inventory.add(i);
        /*
        Auto equip best armor for now
         */
        if(i instanceof Armor) {
            if(!silent) {
                WindowFrame.writeConsole("picked up armor");
            }
            Armor a = (Armor)i;
            boolean equipped = false;
            switch(a.getSlot()) {
                case Armor.SLOT_CHEST:
                    if(a.getArmorClass() > chestSlot.getArmorClass()) {
                        chestSlot = a;
                        equipped = true;
                    }
                    break;
                case Armor.SLOT_ARM:
                    if(a.getArmorClass() > armSlot.getArmorClass()) {
                        armSlot = a;
                        equipped = true;
                    }
                    break;
                case Armor.SLOT_HAND:
                    if(a.getArmorClass() > handSlot.getArmorClass()) {
                        handSlot = a;
                        equipped = true;
                    }
                    break;
                case Armor.SLOT_HEAD:
                    if(a.getArmorClass() > headSlot.getArmorClass()) {
                        headSlot = a;
                        equipped = true;
                    }
                    break;
                case Armor.SLOT_LEG:
                    if(a.getArmorClass() > legSlot.getArmorClass()) {
                        legSlot = a;
                        equipped = true;
                    }
                    break;
            }
            if(equipped) {
                if(!silent) {
                    WindowFrame.writeConsole("You equipped the " + a);
                }
            }
        } else if(i instanceof Weapon) {
            if(!silent) {
                WindowFrame.writeConsole("picked up weapon");
            }
            Weapon w = (Weapon)i;
            if(w.getMaxHitDamage() > maxHitDamage) {
                minHitDamage = w.getMinHitDamage();
                maxHitDamage = w.getMaxHitDamage();
                mainHand = w;
                damageBonus = w.getDamageBonus();
                if(!silent) {
                    WindowFrame.writeConsole("You equipped the " + w);
                }
            } else if(w.getMaxHitDamage() == maxHitDamage) {
                if(damageBonus < w.getDamageBonus()) {
                    minHitDamage = w.getMinHitDamage();
                    maxHitDamage = w.getMaxHitDamage();
                    mainHand = w;
                    damageBonus = w.getDamageBonus();
                    if(!silent) {
                        WindowFrame.writeConsole("You equipped the " + w);
                    }
                }
            }
        } else {
            if(!silent) {
                WindowFrame.writeConsole("You picked up the " + i);
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
        int totalAC = armorClass;
        totalAC += chestSlot.getArmorClass();
        totalAC += armSlot.getArmorClass();
        totalAC += handSlot.getArmorClass();
        totalAC += legSlot.getArmorClass();
        totalAC += headSlot.getArmorClass();

        return totalAC;
    }

    public void setAttackedThisTurn(boolean b) {
        attackedThisTurn = b;
    }

    public abstract void die();

    public double getExpMod() {
        return expMod;
    }

    public Potion getNextPotionTest() {
        for(Item i : inventory) {
            if(i instanceof Potion) {
                return (Potion) i;
            }
        }
        return null;
    }

    public Armor getHeadSlot() {
        return headSlot;
    }

    public Armor getArmSlot() {
        return armSlot;
    }

    public Armor getLegSlot() {
        return legSlot;
    }

    public Armor getHandSlot() {
        return handSlot;
    }

    public Weapon getMainHand() {
        return mainHand;
    }

    public boolean hasItemEquipped(Item i) {
        if(i instanceof Armor) {
            if(armSlot == i || headSlot == i || chestSlot == i || legSlot == i || handSlot == i)
                return true;
        } else if(i instanceof Weapon) {
            if(mainHand == i || offHand == i)
                return true;
        }
        return false;
    }


    public int getMinHitDamage() {
        return minHitDamage;
    }

    public int getMaxHitDamage() {
        return maxHitDamage;
    }

    public int getMeleeSkill() {
        return meleeSkill;
    }

    public void modifyMeleeSkill(int i) {
        meleeSkill += i;
    }

    public boolean isInvincible() {
        for(Trait t : traits) {
            if(t instanceof Invincible)
                return true;
        }
        return false;
    }

    public void addTrait(Trait trait) {
        if(!traits.contains(trait)) {
            traits.add(trait);
        }
    }
}
