package posgima2.game.requests;

import posgima2.game.Game;
import posgima2.swing.RenderPanel;
import posgima2.world.entity.player.Player;
import posgima2.world.entity.monster.Monster;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public class PlayerMove {
    public static int processPlayerMoveRequest(Game game, int nextY, int nextX) {
        /*
        In range check
         */
        if (game.dungeon.inRange(nextY, nextX)) {
            /*
            Check if next tile is passable, and move if it is
             */
            if (game.dungeon.isPassable(nextX, nextY)) {
                game.player.moveToTileImmediately(game.dungeon.getTileMap()[nextY][nextX]);
                // Set flag for player having just moved
                return Game.PLAYER_MOVED;
            }

            /*
            Check if next tile has a monster that is preventing tile from passing isPassable
             */
            else if (game.dungeon.hasMonster(nextY, nextX)) {
                Monster monster = game.dungeon.getMonsterAt(nextY, nextX);
                // Fight the monster, return the result which will be PLAYER_COMBAT
                return game.playerCombat(monster);
            }

            /*
            If it failed isPassable, and there's no monster, check if it's a wall or door
             */
            else {
                switch (game.dungeon.getTileMap()[nextY][nextX].getGlyph()) {
                    // Walked into a wall
                    case RenderPanel.WALL:
                        return Game.PLAYER_HIT_WALL;
                    // Walked into a closed door
                    case RenderPanel.DOOR_CLOSED:
                        // Set the "player" state as STATE_DOOR_CLOSED
                        game.player.setState(Player.STATE.DOOR_CLOSED);
                        // Set the tile the door is on as the player's target
                        game.player.setTargetTile(game.dungeon.getTileMap()[nextY][nextX]);
                        // Set the "game" state as PLAYER_HIT_CLOSED_DOOR to fire off the proper menu and accept
                        // correct keys
                        return Game.PLAYER_HIT_CLOSED_DOOR;
                    // Error case
                    default:
                        return Game.ERROR_PLAYER_MOVE;
                }
            }
        }
        /*
        Trying to move beyond the map limits
         */
        else {
            return Game.ERROR_OUT_OF_MAP_RANGE;
        }
    }
}
