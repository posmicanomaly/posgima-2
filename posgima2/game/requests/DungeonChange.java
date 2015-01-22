package posgima2.game.requests;

import posgima2.game.Game;
import posgima2.swing.RenderPanel;
import posgima2.swing.WindowFrame;
import posgima2.world.Entity;
import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.dungeonSystem.dungeon.Tile;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public abstract class DungeonChange {
    public static Game.TileState processDungeonChangeRequest(Game game) {
        Tile pTile = game.dungeon.getTileMap()[game.player.getY()][game.player.getX()];
        if (pTile.hasDungeonLink()) {
            Dungeon nextDungeon = game.dungeonSystem.getDungeon(pTile.getDungeonLink());
            Tile nextTile = pTile.getTileLink();
            while(nextTile.hasEntity()) {
                WindowFrame.writeConsole("Stairs blocked on other side, clearing the way");
                Entity e = nextTile.getEntity();
                // Todo: entity should "wander" off the stairs so player can come down.
                Tile randomTile;
                do {
                    randomTile = nextDungeon.getRandomTileOf(RenderPanel.FLOOR);
                } while(randomTile.hasEntity());
                e.moveToTileImmediately(randomTile);
            }
            game.dungeon = nextDungeon;
            game.player.moveToTileImmediately(pTile.getTileLink());
            return Game.TileState.HAS_DUNGEON_LINK;
        }
        return Game.TileState.HAS_NO_DUNGEON_LINK;
    }
}
