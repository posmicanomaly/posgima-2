package posgima2.game;

import posgima2.game.requests.DungeonChange;
import posgima2.game.requests.ItemLootingWindow;
import posgima2.game.requests.ItemPickup;
import posgima2.game.requests.PlayerMove;
import posgima2.item.container.Corpse;
import posgima2.item.potion.Potion;
import posgima2.swing.WindowFrame;
import posgima2.world.Player;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public class RequestDispatcher {
    boolean moveRequest;
    boolean itemPickupRequest;
    boolean dungeonChangeRequest;
    boolean quaffRequest;
    boolean toggleDoorRequest;
    boolean eatRequest;
    boolean shootRequest;
    boolean lookRequest;
    private Game game;
    private int nextY;
    private int nextX;
    public boolean idleRequest;

    public RequestDispatcher(Game game) {
        this.game = game;
    }
    public void resetFlags() {
        moveRequest = false;
        itemPickupRequest = false;
        dungeonChangeRequest = false;
        quaffRequest = false;
        toggleDoorRequest = false;
        eatRequest = false;
        shootRequest = false;
        lookRequest = false;
        idleRequest = false;
    }
    public void dispatch() {
        boolean turnTickActionOccurred = false;
        Player player = game.player;
        if (moveRequest) switch (PlayerMove.processPlayerMoveRequest(game, nextY, nextX)) {
            case Game.PLAYER_MOVED:
                turnTickActionOccurred = true;
                player.modifySatiation(Game.HUNGER_HIT_MOVE);
                break;
            case Game.PLAYER_COMBAT:
                turnTickActionOccurred = true;
                player.modifySatiation(Game.HUNGER_HIT_MELEE);
                break;
            case Game.PLAYER_HIT_WALL:
                WindowFrame.writeConsole("You bump into the wall. OUCH!");
                break;
            case Game.PLAYER_HIT_CLOSED_DOOR:
                WindowFrame.writeConsole("Will you open the door? Y/n");
                break;
            case Game.ERROR_PLAYER_MOVE:
                WindowFrame.writeConsole("/warning/processMovePlayerRequest() error in <not passable><no " +
                        "monster> switch(check door/wall)");
                break;
            case Game.ERROR_OUT_OF_MAP_RANGE:
                WindowFrame.writeConsole("The void beckons, but you resist its call.");
                break;

        }
        else if(itemPickupRequest) {
            switch(ItemPickup.processPlayerItemPickupRequest(game)) {
                case HAS_ITEMS:
                    ItemLootingWindow.processItemLootingWithWindow(game);
                    break;
                case HAS_NO_ITEMS:
                    WindowFrame.writeConsole("There's nothing to pickup.");
                    break;
            }
        } else if(dungeonChangeRequest) {
            switch(DungeonChange.processDungeonChangeRequest(game)) {
                case HAS_DUNGEON_LINK:
                    WindowFrame.writeConsole("Changed dungeon");
                    turnTickActionOccurred = true;
                    break;
                case HAS_NO_DUNGEON_LINK:
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
            player.setState(Player.STATE.CLOSE_DOOR_ATTEMPT);
        } else if(eatRequest) {
            if(player.hasCorpseInInventory()) {
                Corpse food = player.getNextCorpseTest();
                switch(player.eat(food)) {
                    case Game.PLAYER_ATE_WELL:
                        WindowFrame.writeConsole("You ate " + food + ". You feel less hungry");
                        turnTickActionOccurred = true;
                        break;
                    case Game.PLAYER_ATE_POISON:
                        WindowFrame.writeConsole("Eating " + food + " made you sick!");
                        turnTickActionOccurred = true;
                        break;
                    case Game.PLAYER_HAS_NO_FOOD:
                        WindowFrame.writeConsole("You have nothing to eat!");
                        break;
                }
            }
        } else if(shootRequest) {
            switch(processPlayerShootRequest()) {
                case Game.PLAYER_SHOOTING:
                    player.setState(Player.STATE.SHOOTING);
                    WindowFrame.writeConsole("Use TAB to switch between targes, directional keys to manually target, " +
                            "T to shoot, Z to cancel");
                    game.targetCursor = new TargetCursor(player.getY(), player.getX());
                    if(game.monstersInView.size() > 0) {
                        game.setTargetCursorNextVisibleMonster();
                    }
                    break;
                case Game.PLAYER_NO_AMMO:
                    WindowFrame.writeConsole("No ammo");
            }
        } else if(lookRequest) {
            WindowFrame.writeConsole("Use directional keys to look around");
            game.lookCursor = new LookCursor(player.getY(), player.getX());
            player.setState(Player.STATE.LOOKING);
        } else if(idleRequest) {
            turnTickActionOccurred = true;
        }
        game.turnTickActionOccurred = turnTickActionOccurred;
    }

    private int processPlayerShootRequest() {
        boolean hasAmmo = true;
        if(hasAmmo) {
            return Game.PLAYER_SHOOTING;
        }
        return Game.PLAYER_NO_AMMO;
    }

    public void setNextLocation(int nextY, int nextX) {
        this.nextY = nextY;
        this.nextX = nextX;
    }
}
