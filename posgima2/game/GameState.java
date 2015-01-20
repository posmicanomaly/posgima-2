package posgima2.game;

import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.Player;
import posgima2.world.monster.Monster;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class GameState {
    private String message;
    private Player player;
    //private char[][] map;
    //private boolean[][] visibleMap;
    private Dungeon dungeon;
    private ArrayList<Monster> monstersInView;
    private int playerState;
    private int turns;
    private LookCursor lookCursor;

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

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public ArrayList<Monster> getMonstersInView() {
        return monstersInView;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public int getTurns() {
        return turns;
    }

    public LookCursor getLookCursor() {
        return lookCursor;
    }

    public void setLookCursor(LookCursor l) {
        lookCursor = l;
    }

    public void setMonstersInView(ArrayList<Monster> monstersInView) {
        this.monstersInView = monstersInView;
    }
}
