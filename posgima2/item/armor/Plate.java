package posgima2.item.armor;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public class Plate extends Armor {
    public Plate(char glyph, int armorClass, int slot) {
        super(glyph);
        this.armorClass = armorClass;
        this.slot = slot;
    }
}
