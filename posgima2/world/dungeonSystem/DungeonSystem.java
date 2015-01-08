package posgima2.world.dungeonSystem;

import posgima2.game.Game;
import posgima2.world.dungeonSystem.dungeon.Dungeon;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/8/2015.
 */
public class DungeonSystem {
    private ArrayList<Dungeon> dungeons;

    public DungeonSystem(int levels) {
        dungeons = new ArrayList<>();
        for(int i = 0; i < levels; i++) {
            dungeons.add(new Dungeon(Game.TEST_MAP_HEIGHT, Game.TEST_MAP_WIDTH));
        }
    }

    public Dungeon getDungeon(int index) {
        if(index > dungeons.size()) {
            return null;
        }
        return dungeons.get(index);
    }
}
