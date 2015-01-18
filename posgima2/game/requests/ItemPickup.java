package posgima2.game.requests;

import posgima2.game.Game;
import posgima2.world.dungeonSystem.dungeon.Tile;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public class ItemPickup {
    public static int processPlayerItemPickupRequest(Game game) {
        Tile tile = game.dungeon.getTileMap()[game.player.getY()][game.player.getX()];
        if(tile.hasItems()) {
            return Game.TILE_HAS_ITEMS;
        } else {
            return Game.TILE_HAS_NO_ITEMS;
        }
    }
}
