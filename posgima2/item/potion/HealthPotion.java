package posgima2.item.potion;

import posgima2.swing.RenderPanel;

/**
 * Created by Jesse Pospisil on 1/12/2015.
 */
public class HealthPotion extends Potion{

    public HealthPotion(int damage) {
        super(RenderPanel.SCROLL);
        this.damage = damage;
    }
}
