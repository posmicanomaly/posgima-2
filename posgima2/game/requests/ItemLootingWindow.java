package posgima2.game.requests;

import posgima2.game.Game;
import posgima2.swing.LootWindow;
import posgima2.world.entity.player.Player;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public class ItemLootingWindow {
    public static void processItemLootingWithWindow(Game game) {
        game.lootWindow = new LootWindow(game.dungeon.getTileMap()[game.player.getY()][game.player.getX()], game
                .player);
        game.player.setState(Player.STATE.LOOTING);
    }
}
