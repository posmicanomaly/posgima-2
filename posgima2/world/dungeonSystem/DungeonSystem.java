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

            dungeons.add(new Dungeon(height, width, rooms));
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
            Room prevRoom = prev.getRandomRoom();
            Room curRoom = cur.getRandomRoom();
            Tile prevTile = prev.getTileMap()[prevRoom.getCenter().getY()][prevRoom.getCenter().getX()];
            Tile curTile = cur.getTileMap()[curRoom.getCenter().getY()][curRoom.getCenter().getX()];

            prevTile.setGlyph(RenderPanel.STAIRS_DOWN);
            curTile.setGlyph(RenderPanel.STAIRS_UP);

            prevTile.setDungeonAndTileLink(cur, curTile);
            curTile.setDungeonAndTileLink(prev, prevTile);
        }
    }
}
