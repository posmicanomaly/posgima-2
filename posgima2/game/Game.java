package posgima2.game;

import posgima2.combat.Melee;
import posgima2.item.armor.Armor;
import posgima2.item.container.Corpse;
import posgima2.item.potion.Potion;
import posgima2.misc.Vector2i;
import posgima2.item.Item;
import posgima2.pathfinding.AStar;
import posgima2.swing.*;
import posgima2.swing.popup.CharacterPanel;
import posgima2.swing.popup.InventoryPanel;
import posgima2.swing.popup.PopupWindow;
import posgima2.world.*;
import posgima2.world.dungeonSystem.DungeonSystem;
import posgima2.world.dungeonSystem.dungeon.Dungeon;
import posgima2.world.dungeonSystem.dungeon.Room;
import posgima2.world.dungeonSystem.dungeon.Tile;
import posgima2.world.monster.Monster;

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
    public static final int TEST_MAP_HEIGHT = 20;
    public static final int TEST_MAP_WIDTH = 60;

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
    public static final int STATE_ITEM_PICKUP = 3;
    public static final int STATE_LOOTED = 5;
    private static final int STATE_GAME_OVER = 2;
    private static final int STATE_LOOTING = 4;
    private static final int STATE_CLOSE_DOOR_ATTEMPT = 6;
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
    private static final int TILE_HAS_DUNGEON_LINK = 1;
    private static final int TILE_HAS_NO_DUNGEON_LINK = 0;
    private static final int XP_RATE = 30;
    private static final int REGEN_RATE = 50;


    // Reassign this later, based on amount of rooms
   // private static int MAX_MONSTERS = 0;

    private Player player;
    private DungeonSystem dungeonSystem;
    private Dungeon dungeon;

    private LootWindow lootWindow;
    private CharacterPanel characterPanel;
    private InventoryPanel inventoryPanel;
    private PopupWindow inventoryPopup;
    private PopupWindow characterPopup;

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
        WindowFrame.gamePanel.consolePanel.setPlayerName(player.toString());

        addPlayerStartingGear(player);

        //dungeon.getTileMap()[center.getY()][center.getX()].addEntity(player);
        player.moveToTileImmediately(dungeon.getTileMap()[center.getY()][center.getX()]);
        dungeon.recalculateVisibility(new Vector2i(player.getY(), player.getX()));

        SetupWindow.println("player " + player.getY() + " " + player.getX());

        // TODO: Check this, as its important for proper EXP distribution per level
        //MAX_MONSTERS = dungeon.getMonsters().size() * 2;

        turns = 0;
        characterPanel = new CharacterPanel(player);
        inventoryPanel = new InventoryPanel(player);
        inventoryPopup = new PopupWindow(inventoryPanel);
        characterPopup = new PopupWindow(characterPanel);
    }

    private void addPlayerStartingGear(Player player) {
        player.addInventory(ItemGenerator.createWeapon(player.getLevel()), false);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_CHEST), false);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_ARM), false);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_HAND), false);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_LEG), false);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_HEAD), false);
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

            case STATE_CLOSE_DOOR_ATTEMPT:
                processStateToggleDoorKeys(e);
                break;
            /*
            STATE_LOOTED is for after we've looted items from the Loot window.
            It can provide a message stating such, and also sets the turn to be expended.
            Currently, looting one or more items only takes up a single turn, the player grabs items very quickly
            apparently.
             */
            case STATE_LOOTED:
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

        inventoryPopup.update();
        characterPopup.update();

        return getGameState();
    }

    private void processStateToggleDoorKeys(KeyEvent e) {
        int targetY = player.getY();
        int targetX = player.getX();
        switch (e.getKeyCode()) {
            case KEY_NORTH:
                targetY--;
                break;
            case KEY_SOUTH:
                targetY++;
                break;
            case KEY_EAST:
                targetX++;
                break;
            case KEY_WEST:
                targetX--;
                break;
        }
        if(dungeon.inRange(targetY, targetX)) {
            if(dungeon.isDoor(targetY, targetX)) {
                dungeon.toggleDoor(dungeon.getTileMap()[targetY][targetX]);
                turnTickActionOccurred = true;
                player.setState(STATE_READY);
            } else {
                WindowFrame.writeConsole("There's no door there.");
            }
        }
        player.setState(STATE_READY);
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
        int playerRegenRate = REGEN_RATE - player.getConstitution();
        if(playerRegenRate < 1) {
            playerRegenRate = 1;
        }
        if(turns > 1 && turns % playerRegenRate == 0) {
            player.regen();
        }
        for(Monster m : dungeon.getMonsters()) {
            m.addAge(1);
            if(m.getAge() % REGEN_RATE == 0) {
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
        boolean dungeonChangeRequest = false;
        boolean quaffRequest = false;
        boolean toggleDoorRequest = false;

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
            case KeyEvent.VK_BACK_SLASH:
                dungeonChangeRequest = true;
                break;

            /*
            Other
             */

            // DEBUG set current level fully explored
            case KeyEvent.VK_0:
                dungeon.setFullyExplored();
                WindowFrame.writeConsole("/success/map fully explored");
                break;

            // Idle, wait, stand still
            case KeyEvent.VK_PERIOD:
                //WindowFrame.writeConsole("idle");
                turnTickActionOccurred = true;
                break;

            // Quaff potion
            case KeyEvent.VK_Q:
                quaffRequest = true;
                break;

            // Close door
            case KEY_TOGGLE_DOOR:
                WindowFrame.writeConsole("Which direction to open/close door?");
                toggleDoorRequest = true;
                break;

            // Open character window
            case KEY_CHARACTER:
                if (characterPopup.isVisible()) {
                    characterPopup.hideWindow();
                } else {
                    characterPopup.showWindow();
                }
                break;

            // Open inventory window
            case KEY_INVENTORY:
                if(inventoryPopup.isVisible()) {
                    inventoryPopup.hideWindow();
                } else {
                    inventoryPopup.showWindow();
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
        } else if(dungeonChangeRequest) {
            switch(processDungeonChangeRequest()) {
                case TILE_HAS_DUNGEON_LINK:
                    WindowFrame.writeConsole("Changed dungeon");
                    turnTickActionOccurred = true;
                    break;
                case TILE_HAS_NO_DUNGEON_LINK:
                    WindowFrame.writeConsole("no dungeon link");
                    break;
            }
        } else if(quaffRequest) {
            Potion potion = player.getNextPotionTest();
            if(potion != null) {
                potion.applyEffects(player);
                player.getInventory().remove(potion);
                turnTickActionOccurred = true;
                WindowFrame.writeConsole("You quaff the potion");
            } else {
                WindowFrame.writeConsole("You have no potions");
            }
        } else if(toggleDoorRequest) {
            player.setState(STATE_CLOSE_DOOR_ATTEMPT);
        }
    }

    private int processDungeonChangeRequest() {
        Tile pTile = dungeon.getTileMap()[player.getY()][player.getX()];
        if(pTile.hasDungeonLink()) {
            dungeon = dungeonSystem.getDungeon(pTile.getDungeonLink());
            player.moveToTileImmediately(pTile.getTileLink());
            return TILE_HAS_DUNGEON_LINK;
        }
        return TILE_HAS_NO_DUNGEON_LINK;
    }

    private int processPlayerItemPickupRequest() {
        Tile tile = dungeon.getTileMap()[player.getY()][player.getX()];
        if(tile.hasItems()) {
            return TILE_HAS_ITEMS;
        } else {
            return TILE_HAS_NO_ITEMS;
        }
    }

    private int playerCombat(Monster monster) {
        /*
        perform melee combat round with player and monster, with defenderCanAttack set to true
         */
        Melee.meleeCombat(player, monster, true);

        /*
        Post combat check if monster is dead.
         */
        if (!monster.isAlive()) {
            // Announce
            WindowFrame.writeConsole("/combat//killed/You killed " + monster + ".");

            /*
            Compute proper experience reward based on dungeon level difficulty
             */
            int level = dungeon.getDifficulty();
            int exp = (int) (((level * (100 * (level * level))) / (dungeon.getMaxMonsterLimit())) * monster.getExpMod());

            // If player is lower level than the intended difficulty, increase the reward
            if(player.getLevel() < dungeon.getDifficulty()) {
                exp = (int)(exp * 1.5);
            }
            // Else reduce the reward
            else if(player.getLevel() > dungeon.getDifficulty()) {
                exp = exp / 4;
            }

            player.modifyExperience(exp);

            // Announce
            WindowFrame.writeConsole("/success/You gained " + exp + " points of experience.");

            // Remove the dead monster from dungeon
            dungeon.getMonsters().remove(monster);
        }

        /*
        Check if player died
         */
        if(!player.isAlive()) {
            WindowFrame.writeConsole("/warning/You died.");
            player.setState(STATE_GAME_OVER);
        }
        return PLAYER_COMBAT;
    }

    private int processPlayerMoveRequest(int nextY, int nextX) {
        /*
        In range check
         */
        if (dungeon.inRange(nextY, nextX)) {
            /*
            Check if next tile is passable, and move if it is
             */
            if (dungeon.isPassable(nextX, nextY)) {
                player.moveToTileImmediately(dungeon.getTileMap()[nextY][nextX]);
                // Set flag for player having just moved
                return PLAYER_MOVED;
            }

            /*
            Check if next tile has a monster that is preventing tile from passing isPassable
             */
            else if (dungeon.hasMonster(nextY, nextX)) {
                Monster monster = dungeon.getMonsterAt(nextY, nextX);
                // Fight the monster, return the result which will be PLAYER_COMBAT
                return playerCombat(monster);
            }

            /*
            If it failed isPassable, and there's no monster, check if it's a wall or door
             */
            else {
                switch(dungeon.getTileMap()[nextY][nextX].getGlyph()) {
                    // Walked into a wall
                    case RenderPanel.WALL:
                        return PLAYER_HIT_WALL;
                    // Walked into a closed door
                    case RenderPanel.DOOR_CLOSED:
                        // Set the "player" state as STATE_DOOR_CLOSED
                        player.setState(STATE_DOOR_CLOSED);
                        // Set the tile the door is on as the player's target
                        player.setTargetTile(dungeon.getTileMap()[nextY][nextX]);
                        // Set the "game" state as PLAYER_HIT_CLOSED_DOOR to fire off the proper menu and accept
                        // correct keys
                        return PLAYER_HIT_CLOSED_DOOR;
                    // Error case
                    default:
                        return ERROR_PLAYER_MOVE;
                }
            }
        }
        /*
        Trying to move beyond the map limits
         */
        else {
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

        if(shortestPath != null) {
            // i = size - 2 because we throw away the first move, because its the current location.
            for (int i = shortestPath.size() - 2; i >= 0; i--) {
                // System.out.print("[" + shortestPath.get(i).getY() + "," + shortestPath.get(i).getX() + "] ");
                m.getMoveQueue().add(shortestPath.get(i));
            }
            // System.out.println("process monsters took " + (System.currentTimeMillis() - time));
        }
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
        if(dungeon.getMonsters().size() < dungeon.getMaxMonsterLimit()) {
            dungeon.spawnRandomMonster(dungeon.getExploredMap());
        }
    }

    private void processMonsterMoveQueue(Monster m) {
        // Get the next location in monster's moveQueue
        Vector2i next = m.getMoveQueue().remove();

        /*
        If location has a player
         */
        if(hasPlayer(next.getY(), next.getX())) {
            // If player is alive
            if(player.isAlive()) {
                // Start melee combat round with player, with defenderCanAttack set to false so player cannot fight back
                Melee.meleeCombat(m, player, false);
            }

            // If player died during combat
            if(!player.isAlive()) {
                // Announce
                WindowFrame.writeConsole("/warning/You died.");
                // Set game state STATE_GAME_OVER
                player.setState(STATE_GAME_OVER);
            }
            /*
            Clear the move queue now, so we don't teleport to the next spot.
             */
            m.getMoveQueue().clear();
        }
        /*
        If no player, check if its a passable tile.
         */
        else if(dungeon.isPassable(next.getX(), next.getY())) {
            /*
            Hack:
            As long as its not a closed door
             */
            //if(dungeon.getTileMap()[next.getY()][next.getX()].getGlyph() != RenderPanel.DOOR_CLOSED)
            m.moveToTileImmediately(dungeon.getTileMap()[next.getY()][next.getX()]);
            if(m.getTile().hasItems()) {
                monsterLootItems(m, m.getTile());
            }
        }
        else {
            // tile not passable, calculate a new route for next time
            WindowFrame.writeConsole(m + " couldn't move");
            m.getMoveQueue().clear();
        }
    }

    private void monsterLootItems(Monster m, Tile t) {
        ArrayList<Item> itemsToLoot = new ArrayList<>();
        for(Item i : t.getItems()) {
            if(i instanceof Corpse) {
                continue;
            }
            itemsToLoot.add(i);
        }
        for(Item i : itemsToLoot) {
            m.addInventory(i, true);
            t.getItems().remove(i);
        }
    }

    private boolean hasPlayer(int y, int x) {
        return y == player.getY() && x == player.getX();
    }

    public void initDungeonSystem() {
        dungeonSystem = new DungeonSystem(10);
    }

    public GameState getGameState() {
        GameState gameState = new GameState();
        gameState.setPlayer(player);
        gameState.setDungeon(dungeon);
        gameState.setTurns(turns);
        return gameState;
    }
}
