package posgima2.game.requests;

import posgima2.game.Game;
import posgima2.world.dungeonSystem.dungeon.Tile;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public abstract class DungeonChange {
    public static int processDungeonChangeRequest(Game game) {
        Tile pTile = game.dungeon.getTileMap()[game.player.getY()][game.player.getX()];
        if(pTile.hasDungeonLink()) {
            game.dungeon = game.dungeonSystem.getDungeon(pTile.getDungeonLink());
            game.player.moveToTileImmediately(pTile.getTileLink());
            return Game.TILE_HAS_DUNGEON_LINK;
        }
        return Game.TILE_HAS_NO_DUNGEON_LINK;
    }
}
