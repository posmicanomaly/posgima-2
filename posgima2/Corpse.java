package posgima2;

import posgima2.item.Item;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/2/2015.
 */
public class Corpse extends Item {
    private ArrayList<Item> items;
    public Corpse(char c, Entity monster) {
        super(c);
        items = new ArrayList<Item>();
        // todo: monster inventory to corpse + bare corpse
    }
}
