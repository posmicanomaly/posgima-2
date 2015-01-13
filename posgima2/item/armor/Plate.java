package posgima2.item.armor;

import posgima2.swing.RenderPanel;

/**
 * Created by Jesse Pospisil on 1/9/2015.
 */
public class Plate extends Armor {
    public Plate(int armorClass, int slot) {
        super(RenderPanel.ARMOR);
        this.armorClass = armorClass;
        this.slot = slot;
    }
}
