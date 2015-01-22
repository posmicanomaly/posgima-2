package posgima2.item.container;

import posgima2.item.Item;
import posgima2.world.entity.monster.Monster;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/2/2015.
 */
public class Corpse extends Item {
    private ArrayList<Item> items;
    private int satiation;
    public Corpse(char c, Monster monster) {
        super(c);
        satiation = monster.getCorpseSatiation();
        /*
        10% chance to be poisonous
         */
        int poisonous = (int)(Math.random() * 100);
        if(poisonous < 10) {
            satiation = -satiation;
        }
        name = monster.toString() + "'s corpse";
        items = new ArrayList<Item>();
        // todo: monster inventory to corpse + bare corpse
    }

    public String toString() {
        return name;
    }

    public int getSatiation() {
        return satiation;
    }
}
