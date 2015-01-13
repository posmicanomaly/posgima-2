package posgima2.game;

import posgima2.item.Item;
import posgima2.item.armor.Armor;
import posgima2.item.armor.Plate;
import posgima2.item.potion.HealthPotion;
import posgima2.item.potion.Potion;
import posgima2.item.weapon.Sword;
import posgima2.item.weapon.Weapon;
import posgima2.misc.Dice;
import posgima2.swing.RenderPanel;

/**
 * Created by Jesse Pospisil on 1/12/2015.
 */
public abstract class ItemGenerator {
    public static Weapon createWeapon(int level) {
        int hit;
        String name;
        switch(level) {
            case 1: hit = Dice.D4; name = "rusty sword"; break;
            case 2: hit = Dice.D6; name = "polished sword"; break;
            case 3: hit = Dice.D8; name = "fine steel sword"; break;
            case 4: hit = Dice.D10; name = "exquisite sword"; break;
            default: hit = Dice.D4; name = "default sword";
        }
        Weapon sword = new Sword(hit, 0, 0, 0, 0);
        sword.setName(name);
        return sword;
    }

    public static Potion createPotion(int level) {
        return new HealthPotion(-(Math.abs(level * 5)));
    }

    public static Armor createArmor(int level, int slot) {
        int ac = level;
        Armor armor = new Plate(ac, slot);
        String name;
        switch(level) {
            case 1: name = "rusty"; break;
            case 2: name = "polished"; break;
            case 3: name = "fine steel"; break;
            case 4: name = "exquisite"; break;
            default: name = "default"; break;
        }
        switch(slot) {
            case Armor.SLOT_ARM: name += " bracers"; break;
            case Armor.SLOT_CHEST: name += " chestguard"; break;
            case Armor.SLOT_HEAD: name += " helmet"; break;
            case Armor.SLOT_LEG: name += " legplates"; break;
            case Armor.SLOT_HAND: name += " gauntlets"; break;
            default: name += " default";
        }
        armor.setName(name);
        return armor;
    }

    public static int randomArmorSlot() {
        //0 - 4 inclusive
        return (int)(Math.random() * 5);
    }
}
