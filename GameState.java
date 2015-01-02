/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class GameState {
    private String message;
    private Player player;
    //private char[][] map;
    //private boolean[][] visibleMap;
    private Dungeon dungeon;

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
}
