package posgima2.item.potion;

import posgima2.item.Item;
import posgima2.world.Entity;

/**
 * Created by Jesse Pospisil on 1/12/2015.
 */
public class Potion extends Item {
    protected int damage;
    public Potion(char glyph) {
        super(glyph);
    }
    public void applyEffects(Entity target) {
        target.applyDamage(damage);
    }
}
