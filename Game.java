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
    private Player player;
    private char[][] charMap;
    private boolean[][] visibleMap;
    private Dungeon dungeon;

    public Game() {
        initMap();
        Room startingRoom = dungeon.getRandomRoom();
        player = new Player(startingRoom.getCenter().getY(), startingRoom.getCenter().getX());
        dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));
    }

    public GameState Update(KeyEvent e) {
        GameState gameState = new GameState();
        gameState.setMessage("/info/");
        int nextY = player.getY();
        int nextX = player.getX();
        boolean moveRequest = false;
        switch(e.getKeyCode()) {
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
                initMap();
                break;
            case KeyEvent.VK_P:
                dungeon.setFullyExplored();
                break;
            case KeyEvent.VK_PERIOD:
                gameState.setMessage(gameState.getMessage() + "idle");
        }

        if(moveRequest) {
            if(dungeon.isPassable(nextX, nextY)) {
                int direction = 0;
                if(nextY < player.getY()) {
                    direction = Game.UP;
                } else if(nextY > player.getY()) {
                    direction = Game.DOWN;
                }

                if(nextX < player.getX()) {
                    direction = Game.LEFT;
                } else if(nextX > player.getX()) {
                    direction = Game.RIGHT;
                }
                player.move(direction);
            } else if(dungeon.hasMonster(nextY, nextX)) {
                dungeon.getMonsterAt(nextY, nextX).die();
            }
        }
        String playerEvaluation = evaluatePlayer(player);
        if(playerEvaluation != null) {
            gameState.setMessage(gameState.getMessage() + "\n" + playerEvaluation);
        }

        processMonsters();

        gameState.setPlayer(player);
        gameState.setDungeon(dungeon);
        //gameState.setMap(charMap);
        dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));
        return gameState;
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
                System.out.println(m + " sees you");
                System.out.println("monster: " + m.getY() + ", " + m.getX());
                System.out.println("player: " + player.getY() + ", " + player.getX());
                AStar astar = new AStar(dungeon.getMap());
                ArrayList<Vector2i> shortestPath = astar.getPath(new Vector2i(m.getY(), m.getX()), new Vector2i
                        (player.getY(), player.getX()));
                m.getMoveQueue().clear();
                // i = size - 2 because we throw away the first move, because its the current location.
                for(int i = shortestPath.size() - 2; i >= 0; i--) {
                    System.out.print("[" + shortestPath.get(i).getY() + "," + shortestPath.get(i).getX() + "] ");
                    m.getMoveQueue().add(shortestPath.get(i));
                }
                System.out.println();
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
                if (dungeon.isPassable(nextX, nextY))
                    m.move(direction);
            } else {
                Vector2i next = m.getMoveQueue().remove();
                int direction = 0;
                int yDiff = next.getY() - m.getY();
                int xDiff = next.getX() - m.getX();

                if(yDiff < 0) {
                    direction = Game.UP;
                } else if(yDiff > 0) {
                    direction = Game.DOWN;
                }

                if(xDiff < 0) {
                    direction = Game.LEFT;
                } else if(xDiff > 0) {
                    direction = Game.RIGHT;
                }

                if(hasPlayer(next.getY(), next.getX())) {
                    System.out.println(m + " hits you");
                } else if(dungeon.isPassable(next.getX(), next.getY())) {
                    m.move(direction);
                }
                System.out.println("move " + direction);
            }
        }
    }

    private boolean hasPlayer(int y, int x) {
        return y == player.getY() && x == player.getX();
    }

    private String evaluatePlayer(Player player) {
        if(charMap[player.getY()][player.getX()] == RenderPanel.DOOR_CLOSED) {
            charMap[player.getY()][player.getX()] = RenderPanel.DOOR_OPEN;
            return "opened door";
        }
        return null;
    }

    public void initMap() {
        dungeon = new Dungeon(TEST_MAP_HEIGHT, TEST_MAP_WIDTH);
        charMap = dungeon.getMap();
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
