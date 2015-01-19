package posgima2.game;

import posgima2.combat.Melee;
import posgima2.item.armor.Armor;
import posgima2.item.container.Corpse;
import posgima2.item.weapon.Arrow;
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
import posgima2.world.dungeonSystem.dungeon.FieldOfView;
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
    public static enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * PLAYER_x
     * Flags set in movement
     */
    public static final int PLAYER_MOVED = 0;
    public static final int PLAYER_COMBAT = 1;
    public static final int PLAYER_HIT_WALL = 2;
    public static final int PLAYER_HIT_CLOSED_DOOR = 3;
    public static final int PLAYER_ATE_WELL = 4;
    public static final int PLAYER_ATE_POISON = 5;
    public static final int PLAYER_HAS_NO_FOOD = 6;
    public static final int PLAYER_SHOOTING = 7;
    public static final int PLAYER_NO_AMMO = 8;
    //Errors set in movement
    public static final int ERROR_PLAYER_MOVE = -1;
    public static final int ERROR_OUT_OF_MAP_RANGE = -2;



    /**
     * TILE_x
     * Flags set in tile pickup
     */
    public static enum TileState {
        HAS_ITEMS, HAS_NO_ITEMS, HAS_DUNGEON_LINK, HAS_NO_DUNGEON_LINK
    }
//    public static final int TILE_HAS_ITEMS = 1;
//    public static final int TILE_HAS_NO_ITEMS = 0;
//    public static final int TILE_HAS_DUNGEON_LINK = 1;
//    public static final int TILE_HAS_NO_DUNGEON_LINK = 0;
    private static final int XP_RATE = 30;
    private static final int REGEN_RATE = 30;

    public static final int MAX_SATIATION = 150;
    public static final int HUNGER_HIT_MELEE = 2;
    public static final int HUNGER_HIT_MOVE = 1;



    // Reassign this later, based on amount of rooms
   // private static int MAX_MONSTERS = 0;

    public Player player;
    public LookCursor lookCursor;
    public DungeonSystem dungeonSystem;
    public Dungeon dungeon;

    public LootWindow lootWindow;
    private CharacterPanel characterPanel;
    private InventoryPanel inventoryPanel;
    private PopupWindow inventoryPopup;
    private PopupWindow characterPopup;

    /**
     * Set true if a major move was performed, like moving, or quaffing, opening a door
     * Do not set if a move was cancelled, didn't open door, didn't pick up items, etc.
     */
    public boolean turnTickActionOccurred;
    private int turns;

    public Game(String name) {
        SetupWindow.println("Setting up game");

        //initMap();
        initDungeonSystem();
        dungeon = dungeonSystem.getDungeon(0);
        Room startingRoom = dungeon.getRandomRoom();
        Vector2i center = startingRoom.getCenter();

        player = new Player('@');
        player.setName(name);
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

        sendGreetingText();
    }

    private void sendGreetingText() {
        WindowFrame.writeConsole("/success/Welcome to Posgima-2. An ancient dungeon lay before you, can you descend " +
                "it's" +
                " maddening levels without perishing?");
        WindowFrame.writeConsole("/info/Press F1 for help.");
    }

    private void addPlayerStartingGear(Player player) {
        player.addInventory(ItemGenerator.createWeapon(player.getLevel()), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_CHEST), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_ARM), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_HAND), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_LEG), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_HEAD), true);
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
            case READY:
                processStateReadyKeys(e);
                break;
            /*
            Use STATE_CANCEL when returning from another window, because forceGameUpdate passes a null KeyEvent, and
            so STATE_READY would try and read that key event and crash.
             */
            case CANCEL:
                player.setState(Player.STATE.READY);
                break;

            case CLOSE_DOOR_ATTEMPT:
                processStateToggleDoorKeys(e);
                break;
            /*
            STATE_LOOTED is for after we've looted items from the Loot window.
            It can provide a message stating such, and also sets the turn to be expended.
            Currently, looting one or more items only takes up a single turn, the player grabs items very quickly
            apparently.
             */
            case LOOTED:
                turnTickActionOccurred = true;
                player.setState(Player.STATE.READY);
                break;
            /*
            STATE_DOOR_CLOSED is set when the player bumps into a door. A should already have been sent at this point
             before setting this state, and so the next key pressed(Y/n), will be passed into
             processStateDoorClosedKeys().
             */
            case DOOR_CLOSED:
                processStateDoorClosedKeys(e);
                break;

            case SHOOTING:
                processStateShootingKeys(e);
                break;

            case LOOKING:
                processStateLookingKeys(e);
                break;

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /*
            STATES below here are "blocking"
            Usually set when we've opened another window like when looting
            This is important because the WindowFrame is always listening, and we don't want to allow movement or
            other actions while looting. Similar to how we use specific processKey methods for various other states
            to prevent illegal actions.
             */
            case LOOTING:
                break;
            case GAME_OVER:
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
        int playerRegenRate = REGEN_RATE - player.getConstitution() - (player.getSatiation() / 10);
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

        if(player.getSatiation() == 0) {
            player.applyDamage(1);
            WindowFrame.writeConsole("You are starving to death!");
        }

        if(!player.isAlive()) {
            player.die();
            player.setState(Player.STATE.GAME_OVER);
            WindowFrame.writeConsole("You died!");
        }
    }

    private void sendHelpMessage() {
        String helpMessage = "Key\tDescription\n";
        helpMessage += "----------------------------\n";
        helpMessage += "Arrows\tMove/attack\n";
        helpMessage += "C\tOpen/close doors\n";
        helpMessage += "Q\tQuaff health potion\n";
        helpMessage += ",\tLoot items\n";
        helpMessage += "P\tToggle Player window\n";
        helpMessage += "I\tToggle Inventory window\n";
        helpMessage += "\\\tUse stairs\n";
        helpMessage += ".\tWait\n";
        helpMessage += "E\tEat\n";
        WindowFrame.writeConsole(helpMessage);
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
                player.setState(Player.STATE.GAME_OVER);
            }
            /*
            Clear the move queue now, so we don't teleport to the next spot.
             */
            m.getMoveQueue().clear();
        }
        else if(monsterCanSeePlayer(m) && m.isRanged() && playerInRangeOfMonster(m)) {

                boolean shouldShoot = true;
                /*
                Pre screen the path
                 */
                ArrayList<Vector2i> line = FieldOfView.findLine(dungeon.getTileMap(), m.getY(), m.getX(), player.getY
                        (), player.getX());
                for(Vector2i v : line) {
                    if(dungeon.hasMonster(v.getY(), v.getX())) {
                        shouldShoot = false;
                    } else if(line.size() > m.getRange()) {
                        shouldShoot = false;
                    }
                }
                if(shouldShoot) {
                    shootTest(line);
                    // If player died during combat
                    if(!player.isAlive()) {
                        // Announce
                        WindowFrame.writeConsole("/warning/You died.");
                        // Set game state STATE_GAME_OVER
                        player.setState(Player.STATE.GAME_OVER);
                    }
                }

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

    private boolean playerInRangeOfMonster(Monster m) {
        ArrayList<Vector2i> line = FieldOfView.findLine(dungeon.getTileMap(), m.getY(), m.getX(), player.getY
                (), player.getX());
        if(line.size() > m.getRange()) {
            return false;
        }
        return true;
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
        gameState.setLookCursor(lookCursor);
        return gameState;
    }

    //-----------------------------------------------------------------------------------------------------------------
    /**
     * Combat related
     */

    public int playerCombat(Monster monster) {
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

            addExperience(monster);

            // Remove the dead monster from dungeon
            dungeon.getMonsters().remove(monster);
        }

        /*
        Check if player died
         */
        if(!player.isAlive()) {
            WindowFrame.writeConsole("/warning/You died.");
            player.setState(Player.STATE.GAME_OVER);
        }
        return PLAYER_COMBAT;
    }

    /**
     * Adds experience to player based on monster source
     * @param monster
     */
    private void addExperience(Monster monster) {
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
    }

    private void rangedAttack(Entity attacker, Entity defender) {
        defender.applyDamage(3);
        WindowFrame.writeConsole("/combat/" + attacker + " arrow struck " + defender + " for 3 damage!");
        if(!defender.isAlive()) {
            defender.die();
            addExperience((Monster) defender);
            dungeon.getMonsters().remove(defender);
        }
    }
    private void shootTest(ArrayList<Vector2i> line) {
        int range = 5;
        if(line.size() < range) {
            range = line.size();
        }

        for(int i = 0; i < range; i++) {

            Tile t = dungeon.getTileMap()[line.get(i).getY()][line.get(i).getX()];
            if(t.hasEntity()) {
                Entity e = t.getEntity();
                rangedAttack(player, e);

                t.addItem(new Arrow());
                return;
            }
        }
        /*
        No entity was hit, so figure out where to drop the arrow. This makes sure it doesn't replace a wall or door
         */
        for(int i = range - 1; i >= 0; i--) {
            Tile cur = dungeon.getTileMap()[line.get(i).getY()][line.get(i).getX()];
            if(cur.getGlyph() == RenderPanel.WALL || cur.getGlyph() == RenderPanel.DOOR_CLOSED) {
                continue;
            }
            cur.addItem(new Arrow());
            WindowFrame.writeConsole("/combat/The arrow falls to the ground");
            break;
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    /**
     * Key processes
     */

    private void processStateDoorClosedKeys(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KEY_YES:
                WindowFrame.writeConsole("You open the door.");
                dungeon.toggleDoor(player.getTargetTile());
                turnTickActionOccurred = true;
                player.setState(Player.STATE.READY);
                break;
            case KEY_NO:
                // Chose not to open door
                player.setState(Player.STATE.READY);
                break;
        }
    }

    private void processStateReadyKeys(KeyEvent e) {
        int nextY = player.getY();
        int nextX = player.getX();

        RequestDispatcher rd = new RequestDispatcher(this);
        rd.resetFlags();
        switch (e.getKeyCode()) {
            /*
            Movement
             */
            case KEY_NORTH:
                rd.moveRequest = true;
                nextY--;
                break;
            case KEY_SOUTH:
                rd.moveRequest = true;
                nextY++;
                break;
            case KEY_WEST:
                rd.moveRequest = true;
                nextX--;
                break;
            case KEY_EAST:
                rd.moveRequest = true;
                nextX++;
                break;

            /*
            Interaction
             */
            case KEY_PICKUP:
                rd.itemPickupRequest = true;
                break;
            case KeyEvent.VK_BACK_SLASH:
                rd.dungeonChangeRequest = true;
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
                rd.idleRequest = true;
                break;

            // Quaff potion
            case KeyEvent.VK_Q:
                rd.quaffRequest = true;
                break;

            // Close door
            case KEY_TOGGLE_DOOR:
                WindowFrame.writeConsole("Which direction to open/close door?");
                rd.toggleDoorRequest = true;
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
                break;

            // Display help
            case KEY_HELP:
                sendHelpMessage();
                break;

            // Eat
            case KEY_EAT:
                rd.eatRequest = true;
                break;

            // Shoot
            case KEY_SHOOT:
                rd.shootRequest = true;
                break;

            case KeyEvent.VK_L:
                rd.lookRequest = true;
                break;
        }
        rd.setNextLocation(nextY, nextX);
        rd.dispatch();
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
                player.setState(Player.STATE.READY);
            } else {
                WindowFrame.writeConsole("There's no door there.");
            }
        }
        player.setState(Player.STATE.READY);
    }

    private void processStateShootingKeys(KeyEvent e) {
        int targetY = lookCursor.getY();
        int targetX = lookCursor.getX();
        switch(e.getKeyCode()) {
            case KEY_NORTH:
                targetY --;
                break;
            case KEY_SOUTH:
                targetY ++;
                break;
            case KEY_EAST:
                targetX ++;
                break;
            case KEY_WEST:
                targetX --;
                break;
            case KEY_CANCEL:
                player.setState(Player.STATE.READY);
                lookCursor = null;
                return;
            case KEY_SHOOT:
                WindowFrame.writeConsole("/combat/You loose an arrow");
                shootTest(FieldOfView.findLine(dungeon.getTileMap(), player.getY(), player.getX(), targetY, targetX));
                player.setState(Player.STATE.READY);
                lookCursor = null;
                turnTickActionOccurred = true;
                return;
        }
        if(dungeon.inRange(targetY, targetX)) {
            lookCursor.setLocation(targetY, targetX);
            Tile t = dungeon.getTileMap()[targetY][targetX];
        }
    }

    private void processStateLookingKeys(KeyEvent e) {
        int targetY = lookCursor.getY();
        int targetX = lookCursor.getX();
        switch(e.getKeyCode()) {
            case KEY_NORTH:
                targetY --;
                break;
            case KEY_SOUTH:
                targetY ++;
                break;
            case KEY_EAST:
                targetX ++;
                break;
            case KEY_WEST:
                targetX --;
                break;
            case KEY_CANCEL:
                player.setState(Player.STATE.READY);
                lookCursor = null;
                return;
        }
        if(dungeon.inRange(targetY, targetX)) {
            lookCursor.setLocation(targetY, targetX);
            Tile t = dungeon.getTileMap()[targetY][targetX];
            if(dungeon.getVisibleMap()[targetY][targetX]) {
                if(t.hasEntity()) {
                    WindowFrame.writeConsole(t.getEntity().toString());
                } else if(t.hasItems()) {
                    WindowFrame.writeConsole("Items");
                } else {
                    WindowFrame.writeConsole(String.valueOf(t.getGlyph()));
                }
            } else if(dungeon.getExploredMap()[targetY][targetX]) {
                WindowFrame.writeConsole(String.valueOf(t.getGlyph()));
            } else {
                WindowFrame.writeConsole("Unexplored");
            }
        }
    }
}
