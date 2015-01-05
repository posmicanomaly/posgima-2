import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class Game {


    private static final int TEST_MAP_HEIGHT = 64;
    private static final int TEST_MAP_WIDTH = 128;
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int STATE_READY = 0;
    public static final int STATE_DOOR_CLOSED = 1;
    private static final int PLAYER_MOVED = 0;
    private static final int PLAYER_COMBAT = 1;
    private static final int PLAYER_HIT_WALL = 2;
    private static final int PLAYER_HIT_CLOSED_DOOR = 3;
    private static final int ERROR_PLAYER_MOVE = 4;
    private static final int ERROR_OUT_OF_MAP_RANGE = 5;
    //reassign
    private static int MAX_MONSTERS = 0;
    private Player player;
    //private char[][] charMap;
    private boolean[][] visibleMap;
    private Tile[][] tileMap;
    private Dungeon dungeon;
    private boolean turnTickActionOccurred;

    public Game() {
        WindowFrame.setupWindow.println("Setting up game");
        initMap();
        Room startingRoom = dungeon.getRandomRoom();
        player = new Player('@');
        Vector2i center = startingRoom.getCenter();
        dungeon.getTileMap()[center.getY()][center.getX()].addEntity(player);
        dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));
        WindowFrame.setupWindow.println("player " + player.getY() + " " + player.getX());
        MAX_MONSTERS = dungeon.getMonsters().size() * 2;
    }

    public GameState Update(KeyEvent e) {

        turnTickActionOccurred = false;
        switch(player.getState()) {
            case STATE_READY:
                processStateReadyKeys(e);
                break;
            case STATE_DOOR_CLOSED:
                processStateDoorClosedKeys(e);
                break;
        }
        if(turnTickActionOccurred) {
            evaluatePlayer(player);

            processMonsters();


            //gameState.setMap(charMap);
            dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));
        }
        GameState gameState = new GameState();
        gameState.setPlayer(player);
        gameState.setDungeon(dungeon);
        return gameState;
    }

    private void processStateDoorClosedKeys(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_Y:
                WindowFrame.writeConsole("temp, you open door");
                dungeon.toggleDoor(player.getTargetTile());
                turnTickActionOccurred = true;
                player.setState(STATE_READY);
                break;
            case KeyEvent.VK_N:
                WindowFrame.writeConsole("temp, you choose no");
                player.setState(STATE_READY);
                break;
            case KeyEvent.VK_Z:
                WindowFrame.writeConsole("temp, you canceled menu");
                player.setState(STATE_READY);
                break;
        }
    }

    private void processStateReadyKeys(KeyEvent e) {
        int nextY = player.getY();
        int nextX = player.getX();
        boolean moveRequest = false;
        switch (e.getKeyCode()) {
            /*
            Movement
             */
            case KeyEvent.VK_W:
                moveRequest = true;
                nextY--;
                break;
            case KeyEvent.VK_S:
                moveRequest = true;
                nextY++;
                break;
            case KeyEvent.VK_A:
                moveRequest = true;
                nextX--;
                break;
            case KeyEvent.VK_D:
                moveRequest = true;
                nextX++;
                break;

            /*
            Other
             */
            case KeyEvent.VK_R:
                WindowFrame.writeConsole("/success/initMap()");
                initMap();
                break;
            case KeyEvent.VK_P:
                dungeon.setFullyExplored();
                WindowFrame.writeConsole("/success/map fully explored");
                break;
            case KeyEvent.VK_PERIOD:
                WindowFrame.writeConsole("idle");
                turnTickActionOccurred = true;
                break;
        }

        if (moveRequest) {
            switch(processPlayerMoveRequest(nextY, nextX)) {
                case PLAYER_MOVED:
                case PLAYER_COMBAT:
                    turnTickActionOccurred = true;
                    break;
                case PLAYER_HIT_WALL:
                    WindowFrame.writeConsole("You bump into the wall.");
                    break;
                case PLAYER_HIT_CLOSED_DOOR:
                    WindowFrame.writeConsole("temp, Open door? Y/n/z");
                    break;
                case ERROR_PLAYER_MOVE:
                    WindowFrame.writeConsole("/warning/processMovePlayerRequest() error in <not passable><no " +
                            "monster> switch(check door/wall)");
                    break;
                case ERROR_OUT_OF_MAP_RANGE:
                    WindowFrame.writeConsole("The void beckons, but you resist its call.");
                    break;

            }
        }
    }

    private int processPlayerMoveRequest(int nextY, int nextX) {
        if (dungeon.inRange(nextY, nextX)) {
            if (dungeon.isPassable(nextX, nextY)) {
                player.moveToTileImmediately(dungeon.getTileMap()[nextY][nextX]);
                return PLAYER_MOVED;
                //System.out.println("passable");
            } else if (dungeon.hasMonster(nextY, nextX)) {
                Monster monster = dungeon.getMonsterAt(nextY, nextX);
                if (monster.die()) {
                    WindowFrame.writeConsole("/combat//atk/You crush " + monster + ".");
                    dungeon.getMonsters().remove(monster);
                }
                return PLAYER_COMBAT;
                //System.out.println("has entity");
            } else {
                switch(dungeon.getTileMap()[nextY][nextX].getGlyph()) {
                    case RenderPanel.WALL:
                        return PLAYER_HIT_WALL;
                    case RenderPanel.DOOR_CLOSED:
                        player.setState(STATE_DOOR_CLOSED);
                        player.setTargetTile(dungeon.getTileMap()[nextY][nextX]);
                        return PLAYER_HIT_CLOSED_DOOR;
                    default:
                        return ERROR_PLAYER_MOVE;
                }
            }
        } else {
            return ERROR_OUT_OF_MAP_RANGE;
        }
    }

    private boolean monsterCanSeePlayer(Monster monster) {
        return monster.getVisibility()[player.getY()][player.getX()];
    }

    private void processMonsters() {
        for(Monster m : dungeon.getMonsters()) {
            if(!m.isAlive()) {
                continue;
            }
            m.calculateVisibility(dungeon);
            if(monsterCanSeePlayer(m)) {
                AStar astar = new AStar(dungeon);
                ArrayList<Vector2i> shortestPath = astar.getPath(new Vector2i(m.getY(), m.getX()), new Vector2i
                        (player.getY(), player.getX()), false, true);
                m.getMoveQueue().clear();

                // i = size - 2 because we throw away the first move, because its the current location.
                for(int i = shortestPath.size() - 2; i >= 0; i--) {
                   // System.out.print("[" + shortestPath.get(i).getY() + "," + shortestPath.get(i).getX() + "] ");
                    m.getMoveQueue().add(shortestPath.get(i));
                }
               // System.out.println("process monsters took " + (System.currentTimeMillis() - time));
            }

            if(m.getMoveQueue().size() == 0) {
                int direction = (int) (Math.random() * 4);
                int nextY = m.getY();
                int nextX = m.getX();
                switch (direction) {
                    case 0:
                        nextY--;
                        break;
                    case 1:
                        nextY++;
                        break;
                    case 2:
                        nextX--;
                        break;
                    case 3:
                        nextX++;
                        break;
                }
                if (dungeon.isPassable(nextX, nextY)) {
                    //m.move(direction);
                    // Monsters can't open doors (yet)
                    if(dungeon.getTileMap()[nextY][nextX].getGlyph() != RenderPanel.DOOR_CLOSED)
                        m.moveToTileImmediately(dungeon.getTileMap()[nextY][nextX]);
                }
            } else {
                processMonsterMoveQueue(m);
            }
        }
        if(dungeon.getMonsters().size() < MAX_MONSTERS) {
            dungeon.spawnMonster('r', dungeon.getVisibleMap());
        }
    }

    private void processMonsterMoveQueue(Monster m) {
        Vector2i next = m.getMoveQueue().remove();
        if(hasPlayer(next.getY(), next.getX())) {
            WindowFrame.writeConsole("/combat//def/" + m + " hits you");
        } else if(dungeon.isPassable(next.getX(), next.getY())) {
            //m.move(direction);
            if(dungeon.getTileMap()[next.getY()][next.getX()].getGlyph() != RenderPanel.DOOR_CLOSED)
                m.moveToTileImmediately(dungeon.getTileMap()[next.getY()][next.getX()]);
            else {
                // re add it for now
                m.getMoveQueue().addFirst(next);
            }
        }
        //System.out.println("move " + direction);
    }

    private boolean hasPlayer(int y, int x) {
        return y == player.getY() && x == player.getX();
    }

    private void evaluatePlayer(Player player) {
        Tile tile = player.getTile();
        if(tile.getGlyph() == RenderPanel.DOOR_CLOSED) {
            tile.setGlyph(RenderPanel.DOOR_OPEN);
            WindowFrame.writeConsole("You open the door.");
        }
    }

    public void initMap() {
        dungeon = new Dungeon(TEST_MAP_HEIGHT, TEST_MAP_WIDTH);
        //charMap = dungeon.getMap();
        visibleMap = dungeon.getVisibleMap();

    }

    public GameState getGameState() {
        GameState gameState = new GameState();
        gameState.setMessage("getGameState()");
        gameState.setPlayer(player);
        //gameState.setMap(charMap);
        gameState.setDungeon(dungeon);
        return gameState;
    }
}
