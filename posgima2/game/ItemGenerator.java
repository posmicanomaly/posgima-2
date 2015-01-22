package posgima2.game;

import posgima2.item.armor.Armor;
import posgima2.item.armor.Plate;
import posgima2.item.potion.HealthPotion;
import posgima2.item.potion.Potion;
import posgima2.item.weapon.Sword;
import posgima2.item.weapon.Weapon;
import posgima2.misc.Dice;

/**
 * Created by Jesse Pospisil on 1/12/2015.
 */
public abstract class ItemGenerator {
    public static Weapon createWeapon(int level) {
        int minDamage = 1;
        int maxDamage = 3;
        int damageBonus = 0;
        for(int i = 1; i <= level; i++) {
            minDamage += i * 2;
            maxDamage = minDamage * 2;
            if(i > 0 && i % 2 == 0) {
                damageBonus++;
            }
        }
        String name;
        switch (level) {
            case 1:
                name = "rusty sword";
                break;
            case 2:
                name = "polished sword";
                break;
            case 3:
                name = "fine steel sword";
                break;
            case 4:
                name = "exquisite sword";
                break;
            default:
                name = "default sword";
        }
        Weapon sword = new Sword(minDamage, maxDamage, damageBonus, 0, 0, 0);
        sword.setName(name);
        return sword;
    }

    public static Potion createPotion(int level) {
        Potion potion = new HealthPotion(-(Math.abs(level * 5)));
        potion.setName("Health Potion L" + level);
        return potion;
    }

    public static Armor createArmor(int level, int slot) {
        int ac = level;
        Armor armor = new Plate(ac, slot);
        String name;
        switch (level) {
            case 1:
                name = "rusty";
                break;
            case 2:
                name = "polished";
                break;
            case 3:
                name = "fine steel";
                break;
            case 4:
                name = "exquisite";
                break;
            default:
                name = "default";
                break;
        }
        switch (slot) {
            case Armor.SLOT_ARM:
                name += " bracers";
                break;
            case Armor.SLOT_CHEST:
                name += " chestguard";
                break;
            case Armor.SLOT_HEAD:
                name += " helmet";
                break;
            case Armor.SLOT_LEG:
                name += " legplates";
                break;
            case Armor.SLOT_HAND:
                name += " gauntlets";
                break;
            default:
                name += " default";
        }
        armor.setName(name);
        return armor;
    }

    public static int randomArmorSlot() {
        //0 - 4 inclusive
        return (int) (Math.random() * 5);
    }
}
