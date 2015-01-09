package posgima2.item.armor;

/**
 * Use this for empty armor slots for now, so we don't have to null check everywhere.
 * Created by Jesse Pospisil on 1/9/2015.
 */
public class NullArmor extends Armor {
    public NullArmor(char glyph) {
        super(glyph);
        this.armorClass = 0;
    }
}
