package posgima2.game;

import posgima2.misc.Vector2i;
import posgima2.item.Item;
import posgima2.pathfinding.AStar;
import posgima2.swing.*;
import posgima2.world.*;
import posgima2.world.dungeonSystem.DungeonSystem;
import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.dungeonSystem.dungeon.Room;
import posgima2.world.dungeonSystem.dungeon.Tile;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static posgima2.misc.Controls.*;

/**
 * Created by Jesse Pospisil on 12/27/2014.
 */
public class Game {
    //major minor release build
    public static final String VERSION = "0.0.2.0";
    public static final String STAGE = "Pre Alpha";

    /**
     * posgima2.world.dungeonSystem.dungeon.Dungeon dimensions
     */
    public static final int TEST_MAP_HEIGHT = 64;
    public static final int TEST_MAP_WIDTH = 128;

    /**
     * Cardinal directions
     */
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    /**
     * STATE_x
     * posgima2.world.Player states such as "ready", "door closed", "item pickup"
     * to determine which key options to accept, and control game flow.
     */
    public static final int STATE_CANCEL = -1;
    public static final int STATE_READY = 0;
    public static final int STATE_DOOR_CLOSED = 1;
    private static final int STATE_GAME_OVER = 2;
    public static final int STATE_ITEM_PICKUP = 3;
    private static final int STATE_LOOTING = 4;
    public static final int STATE_LOOTED = 5;

    /**
     * PLAYER_x
     * Flags set in movement
     */
    private static final int PLAYER_MOVED = 0;
    private static final int PLAYER_COMBAT = 1;
    private static final int PLAYER_HIT_WALL = 2;
    private static final int PLAYER_HIT_CLOSED_DOOR = 3;
    //Errors set in movement
    private static final int ERROR_PLAYER_MOVE = 4;
    private static final int ERROR_OUT_OF_MAP_RANGE = 5;



    /**
     * TILE_x
     * Flags set in tile pickup
     */
    private static final int TILE_HAS_ITEMS = 1;
    private static final int TILE_HAS_NO_ITEMS = 0;

    // Reassign this later, based on amount of rooms
    private static int MAX_MONSTERS = 0;

    private Player player;
    private DungeonSystem dungeonSystem;
    private Dungeon dungeon;

    private LootWindow lootWindow;
    private CharacterWindow characterWindow;
    private InventoryWindow inventoryWindow;

    /**
     * Set true if a major move was performed, like moving, or quaffing, opening a door
     * Do not set if a move was cancelled, didn't open door, didn't pick up items, etc.
     */
    private boolean turnTickActionOccurred;
    private int turns;

    public Game() {
        SetupWindow.println("Setting up game");

        //initMap();
        initDungeonSystem();
        dungeon = dungeonSystem.getDungeon(0);
        Room startingRoom = dungeon.getRandomRoom();
        Vector2i center = startingRoom.getCenter();

        player = new Player('@');
        player.setName("AlphaTester");
        WindowFrame.consolePanel.setPlayerName(player.toString());

        dungeon.getTileMap()[center.getY()][center.getX()].addEntity(player);
        dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));

        SetupWindow.println("player " + player.getY() + " " + player.getX());

        MAX_MONSTERS = dungeon.getMonsters().size() * 2;

        turns = 0;
        characterWindow = new CharacterWindow(player);
        inventoryWindow = new InventoryWindow(player);
    }

    /**
     * process regen, ailments, etc for all entities
     * get and process desired action based on key input
     * if action expended the turn
     *          - process monsters
     *          - recalc player visibility
     *          - reset attack states
     * @param e
     * @return
     */
    public GameState Update(KeyEvent e) {

        turnTickActionOccurred = false;
        switch (player.getState()) {
            /*
            The main player STATE. The player is ready for anything(mostly), so process the most keys
             */
            case STATE_READY:
                processStateReadyKeys(e);
                break;
            /*
            Use STATE_CANCEL when returning from another window, because forceGameUpdate passes a null KeyEvent, and
            so STATE_READY would try and read that key event and crash.
             */
            case STATE_CANCEL:
                player.setState(STATE_READY);
                break;
            /*
            STATE_LOOTED is for after we've looted items from the Loot window.
            It can provide a message stating such, and also sets the turn to be expended.
            Currently, looting one or more items only takes up a single turn, the player grabs items very quickly
            apparently.
             */
            case STATE_LOOTED:
                StringBuilder inventoryMessage = new StringBuilder("inventory: ");
                for (Item i : player.getInventory()) {
                    inventoryMessage.append("[").append(i).append("] ");
                }
                WindowFrame.writeConsole(inventoryMessage.toString());
                turnTickActionOccurred = true;
                player.setState(STATE_READY);
                break;
            /*
            STATE_DOOR_CLOSED is set when the player bumps into a door. A should already have been sent at this point
             before setting this state, and so the next key pressed(Y/n), will be passed into
             processStateDoorClosedKeys().
             */
            case STATE_DOOR_CLOSED:
                processStateDoorClosedKeys(e);
                break;

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /*
            STATES below here are "blocking"
            Usually set when we've opened another window like when looting
            This is important because the WindowFrame is always listening, and we don't want to allow movement or
            other actions while looting. Similar to how we use specific processKey methods for various other states
            to prevent illegal actions.
             */
            case STATE_LOOTING:
                break;
            case STATE_GAME_OVER:
                break;
        }
        /*
        If the turn has been expended by the player, or otherwise set due to an effect
         */
        if (turnTickActionOccurred) {
            processMonsters();

            /*
            todo: have player calculate their own vis, remove it from dungeon.
             */
            dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));
            /*
            Reset the attack states so next turn everyone can attack again.
             */
            resetAttackStates();

            endTurn();
            turns++;
        }

        inventoryWindow.update();
        characterWindow.update();

        return getGameState();
    }

    private void processItemLootingWithWindow() {
        lootWindow = new LootWindow(dungeon.getTileMap()[player.getY()][player.getX()], player);
        player.setState(STATE_LOOTING);
    }


    private void resetAttackStates() {
        player.resetTurnTick();
        for(Monster m : dungeon.getMonsters()) {
            m.resetTurnTick();
        }
    }

    /**
     * Process regen, etc
     */
    private void endTurn() {
        if(turns > 1 && turns % 30 == 0) {
            player.regen();
        }
        for(Monster m : dungeon.getMonsters()) {
            m.addAge(1);
            if(m.getAge() % 30 == 0) {
                m.regen();
            }
        }
    }


    private void processStateDoorClosedKeys(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KEY_YES:
                WindowFrame.writeConsole("You open the door.");
                dungeon.toggleDoor(player.getTargetTile());
                turnTickActionOccurred = true;
                player.setState(STATE_READY);
                break;
            case KEY_NO:
                // Chose not to open door
                player.setState(STATE_READY);
                break;
        }
    }

    private void processStateReadyKeys(KeyEvent e) {
        int nextY = player.getY();
        int nextX = player.getX();
        boolean moveRequest = false;
        boolean itemPickupRequest = false;
        switch (e.getKeyCode()) {
            /*
            Movement
             */
            case KEY_NORTH:
                moveRequest = true;
                nextY--;
                break;
            case KEY_SOUTH:
                moveRequest = true;
                nextY++;
                break;
            case KEY_WEST:
                moveRequest = true;
                nextX--;
                break;
            case KEY_EAST:
                moveRequest = true;
                nextX++;
                break;

            /*
            Interaction
             */
            case KEY_PICKUP:
                itemPickupRequest = true;
                break;

            /*
            Other
             */
            case KeyEvent.VK_P:
                dungeon.setFullyExplored();
                WindowFrame.writeConsole("/success/map fully explored");
                break;
            case KeyEvent.VK_PERIOD:
                WindowFrame.writeConsole("idle");
                turnTickActionOccurred = true;
                break;
            case KEY_CHARACTER:
                if (characterWindow.isVisible()) {
                    characterWindow.hideWindow();
                } else {
                    characterWindow.showWindow();
                }
                break;
            case KEY_INVENTORY:
                if(inventoryWindow.isVisible()) {
                    inventoryWindow.hideWindow();
                } else {
                    inventoryWindow.showWindow();
                }
        }

        if (moveRequest) {
            switch(processPlayerMoveRequest(nextY, nextX)) {
                case PLAYER_MOVED:
                case PLAYER_COMBAT:
                    turnTickActionOccurred = true;
                    break;
                case PLAYER_HIT_WALL:
                    WindowFrame.writeConsole("You bump into the wall. OUCH!");
                    break;
                case PLAYER_HIT_CLOSED_DOOR:
                    WindowFrame.writeConsole("Will you open the door? Y/n");
                    break;
                case ERROR_PLAYER_MOVE:
                    WindowFrame.writeConsole("/warning/processMovePlayerRequest() error in <not passable><no " +
                            "monster> switch(check door/wall)");
                    break;
                case ERROR_OUT_OF_MAP_RANGE:
                    WindowFrame.writeConsole("The void beckons, but you resist its call.");
                    break;

            }
        } else if(itemPickupRequest) {
            switch(processPlayerItemPickupRequest()) {
                case TILE_HAS_ITEMS:
                    processItemLootingWithWindow();
                    break;
                case TILE_HAS_NO_ITEMS:
                    WindowFrame.writeConsole("There's nothing to pickup.");
                    break;
            }
        }
    }

    private int processPlayerItemPickupRequest() {
        Tile tile = dungeon.getTileMap()[player.getY()][player.getX()];
        if(tile.hasItems()) {
            return TILE_HAS_ITEMS;
        } else {
            return TILE_HAS_NO_ITEMS;
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
                player.meleeAttack(monster, true);

                if (!monster.isAlive()) {
                    WindowFrame.writeConsole("/combat//killed/You killed " + monster + ".");
                    monster.die();
                    dungeon.getMonsters().remove(monster);
                }

                if(!player.isAlive()) {
                    WindowFrame.writeConsole("/warning/You died.");
                    player.setState(STATE_GAME_OVER);
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

    private void setMonsterPathToPlayer(Monster m) {
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

    private void processMonsters() {
        for(Monster m : dungeon.getMonsters()) {
            if(!m.isAlive()) {
                continue;
            }
            m.calculateVisibility(dungeon);
            /*
            If monster can see player
             */
            if(monsterCanSeePlayer(m)) {
                /*
                If monster is already aggro on player
                 */
                if(m.isAggroPlayer()) {
                    /*
                    Every 3rd turn that this Monster has been aggro on the player
                    perform a fresh path calculation.
                    This is to prevent lockstep movement, at "2" you can run circles with a Monster.
                    at "3" its a little bit more spaced out.
                     */

                    /*
                    If the turn is multiple of 3
                     */
                    if((turns - m.getAggroTurnStart()) % 3 == 0) {
                        setMonsterPathToPlayer(m);
                        //WindowFrame.writeConsole(m + " fresh calc");
                    }
                    /*
                    If the move queue is empty
                     */
                    if(m.getMoveQueue().size() == 0) {
                        setMonsterPathToPlayer(m);
                        //WindowFrame.writeConsole(m + " moveQueue empty, sees you, aggro on you already. Recalc");
                    }
                }
                /*
                If monster is not aggro on player
                 */
                else if(!m.isAggroPlayer()){
                    m.setAggro(true, turns);
                    setMonsterPathToPlayer(m);
                    WindowFrame.writeConsole(m + " aggro on you");
                }
            }

            /*
            If monster cannot see the player
             */
            else {
                /*
                But they are aggro, and they have no more moves left in their queue
                 */
                if(m.isAggroPlayer() && m.getMoveQueue().size() == 0) {
                    // Drop aggro
                    m.setAggro(false, turns);
                    //WindowFrame.writeConsole(m + " dropped aggro");
                }
            }

            /*
            If there are no moves in the monster's queue
            Pick a random direction and go
             */
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
                    // Monsters can't open doors (yet)
                    if(dungeon.getTileMap()[nextY][nextX].getGlyph() != RenderPanel.DOOR_CLOSED)
                        m.getMoveQueue().add(new Vector2i(nextY, nextX));
                       // m.moveToTileImmediately(dungeon.getTileMap()[nextY][nextX]);
                }
            }
            /*
            Otherwise, process next move in queue
             */
            if(m.getMoveQueue().size() > 0) {
                processMonsterMoveQueue(m);
            }
            //}
        }
        /*
        Spawn a new monster if we're below the max limit.
         */
        if(dungeon.getMonsters().size() < MAX_MONSTERS) {
            dungeon.spawnRandomMonster(dungeon.getVisibleMap());
        }
    }

    private void processMonsterMoveQueue(Monster m) {
        Vector2i next = m.getMoveQueue().remove();
        if(hasPlayer(next.getY(), next.getX())) {
            //posgima2.swing.WindowFrame.writeConsole("/combat//def/" + m + " hits you");
            m.meleeAttack(player, false);
            if(!player.isAlive()) {
                WindowFrame.writeConsole("/warning/You died.");
                player.setState(STATE_GAME_OVER);
            }
            /*
            Clear the move queue now, so we don't teleport to the next spot.
             */
            m.getMoveQueue().clear();
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

//    private void evaluatePlayer(posgima2.world.Player player) {
//        posgima2.world.dungeonSystem.dungeon.Tile tile = player.getTile();
//        if(tile.getGlyph() == posgima2.swing.RenderPanel.DOOR_CLOSED) {
//            tile.setGlyph(posgima2.swing.RenderPanel.DOOR_OPEN);
//            posgima2.swing.WindowFrame.writeConsole("You open the door.");
//        }
//    }

    public void initDungeonSystem() {
        dungeonSystem = new DungeonSystem(3);
    }

    public void initMap() {
        dungeon = new Dungeon(TEST_MAP_HEIGHT, TEST_MAP_WIDTH);
        //charMap = dungeon.getMap();
        //visibleMap = dungeon.getVisibleMap();

    }

    public GameState getGameState() {
        GameState gameState = new GameState();
        gameState.setPlayer(player);
        gameState.setDungeon(dungeon);
        gameState.setTurns(turns);
        return gameState;
    }
}
