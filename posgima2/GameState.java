package posgima2;

import posgima2.world.dungeon.Dungeon;
import posgima2.world.Player;

/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class GameState {
    private String message;
    private Player player;
    //private char[][] map;
    //private boolean[][] visibleMap;
    private Dungeon dungeon;
    private int monstersInView;
    private int playerState;
    private int turns;

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public char[][] getMap() {
        return dungeon.getMap();
    }

    //public void setMap(char[][] map) {
    //    this.map = map;
    //}

    public boolean[][] getVisibleMap() {
        return dungeon.getVisibleMap();
    }



    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public int monstersInView() {
        return monstersInView;
    }

    public void setMonstersInView(int monstersInView) {
        this.monstersInView = monstersInView;
    }

    public int getPlayerState() {
        return playerState;
    }

    public void setPlayerState(int newState) {
        playerState = newState;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public int getTurns() {
        return turns;
    }
}
