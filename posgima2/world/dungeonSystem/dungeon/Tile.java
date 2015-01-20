package posgima2.world.dungeonSystem.dungeon;

import posgima2.item.Item;
import posgima2.misc.Vector2i;
import posgima2.world.Entity;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/2/2015.
 */
public class Tile {
    private char glyph;
    private Entity entity;
    private ArrayList<Item> items;
    private int y;
    private int x;

    private Dungeon dungeonLink;
    private Tile tileLink;

    public Tile(char glyph, int y, int x) {
        this.glyph = glyph;
        entity = null;
        items = new ArrayList<Item>();
        this.y = y;
        this.x = x;
    }

    public boolean hasItems() {
        return items.size() > 0;
    }

    public void setDungeonAndTileLink(Dungeon targetDungeon, Tile targetTile) {
        this.dungeonLink = targetDungeon;
        this.tileLink = targetTile;
    }

    public boolean hasDungeonLink() {
        if(dungeonLink != null && tileLink != null) {
            return true;
        }
        return false;
    }

    public boolean hasEntity() {
        if(entity == null) {
            return false;
        }
        return true;
    }

    public boolean addEntity(Entity entity) {
        if(!hasEntity()) {
            if(entity.getTile() != null) {
                entity.getTile().remove(entity);
            }
            this.entity = entity;
            entity.setY(this.y);
            entity.setX(this.x);
            entity.setTile(this);
            return true;
        }
        return false;
    }

    // todo: this is not removing the entity
    public boolean remove(Entity entity) {
        if(this.entity == entity) {
            this.entity = null;
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Entity getEntity() {
        return entity;
    }

    public char getGlyph() {
        return glyph;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public Dungeon getDungeonLink() {
        return dungeonLink;
    }

    public Tile getTileLink() {
        return tileLink;
    }

    public Vector2i getLocation() {
        return new Vector2i(y, x);
    }
}
