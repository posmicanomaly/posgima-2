package posgima2.world.dungeonSystem;

import posgima2.game.Game;
import posgima2.swing.RenderPanel;
import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.dungeonSystem.dungeon.Room;
import posgima2.world.dungeonSystem.dungeon.Tile;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/8/2015.
 */
public class DungeonSystem {
    private ArrayList<Dungeon> dungeons;

    public DungeonSystem(int levels) {
        dungeons = new ArrayList<>();
        for (int i = 0; i < levels; i++) {
            int height = Game.TEST_MAP_HEIGHT + (i * 2);
            int width = Game.TEST_MAP_WIDTH +  (i * 2);
            /*
            13*13 is the biggest room we can make
             */
            int rooms = (height * width) / (13 * 13);

            /*
            Difficulty is i + 1 because we can't have level 0 monsters, and this is just the level of monsters that
            spawn currently.
             */
            dungeons.add(new Dungeon(height, width, rooms, i + 1));
        }

        connectLevels();
    }

    public Dungeon getDungeon(int index) {
        if (index > dungeons.size()) {
            return null;
        }
        return dungeons.get(index);
    }

    public Dungeon getDungeon(Dungeon dungeon) {
        for(Dungeon d : dungeons) {
            if(d.equals(dungeon)) {
                return d;
            }
        }
        return null;
    }

    private void connectLevels() {
        for (int i = 1; i < dungeons.size(); i++) {
            Dungeon prev = dungeons.get(i - 1);
            Dungeon cur = dungeons.get(i);

            Room prevRoom;
            Tile prevTile;
            /*
            Get a valid random room that doesn't already have stairs in the center. Otherwise they would get
            overwritten by STAIRS_DOWN and we wouldn't be able to get back
             */
            do{
                prevRoom = prev.getRandomRoom();
                prevTile = prev.getTileMap()[prevRoom.getCenter().getY()][prevRoom.getCenter().getX()];
            } while(prevTile.getGlyph() == RenderPanel.STAIRS_UP);

            Room curRoom = cur.getRandomRoom();
            Tile curTile = cur.getTileMap()[curRoom.getCenter().getY()][curRoom.getCenter().getX()];

            prevTile.setGlyph(RenderPanel.STAIRS_DOWN);
            curTile.setGlyph(RenderPanel.STAIRS_UP);

            prevTile.setDungeonAndTileLink(cur, curTile);
            curTile.setDungeonAndTileLink(prev, prevTile);
        }
    }
}
