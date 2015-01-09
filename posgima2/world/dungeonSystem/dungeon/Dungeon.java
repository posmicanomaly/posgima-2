package posgima2.world.dungeonSystem.dungeon;

import posgima2.misc.Vector2i;
import posgima2.pathfinding.AStar;
import posgima2.swing.ConsolePanel;
import posgima2.swing.RenderPanel;
import posgima2.swing.SetupWindow;
import posgima2.swing.WindowFrame;
import posgima2.item.weapon.Sword;
import posgima2.world.Entity;
import posgima2.world.Monster;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/28/2014.
 */
public class Dungeon {
    public static final char TEMP_CORRIDOR = 'C';
    public static final char VOID = ' ';

    private ArrayList<Room> rooms;


    char[][] charMap;
    Tile[][] tileMap;
    private final int MAP_ROWS;
    private final int MAP_COLS;
    private boolean[][] visibleMap;
    private boolean[][] exploredMap;
    private ArrayList<Monster> monsters;
    private int maxMonsterLimit;
    private int difficulty;

    public Dungeon(int rows, int cols, int roomAmount, int difficulty) {
        this.MAP_ROWS = rows;
        this.MAP_COLS = cols;
        charMap = new char[rows][cols];
        tileMap = new Tile[rows][cols];
        visibleMap = new boolean[rows][cols];
        exploredMap = new boolean[rows][cols];
        monsters = new ArrayList<Monster>();
        fillMap(VOID);

        rooms = new ArrayList<Room>();

        SetupWindow.println("Creating and connecting rooms");
        createAndConnectRooms(roomAmount);
        SetupWindow.println("total rooms: " + rooms.size());

        SetupWindow.println("blasting rooms");
        blastExtraEntrances(rooms.size() / 4);

        SetupWindow.println("finishing map");
        finishMap();

        SetupWindow.println("creating tileMap");
        for (int y = 0; y < MAP_ROWS; y++) {
            for (int x = 0; x < MAP_COLS; x++) {
                Tile tile = new Tile(charMap[y][x], y, x);
                tileMap[y][x] = tile;
            }
        }

        SetupWindow.println("sprinkling items");
        sprinkleItems();
        SetupWindow.println("sprinkling monsters");
        setMaxMonsterLimit(rooms.size());
        this.difficulty = difficulty;
        sprinkleMonsters();
    }

    private void createAndConnectRooms(int amount) {
        Room room;
            do {
                room = createRandomRoom();
                if(room != null)
                    rooms.add(room);
            } while(rooms.size() < amount);

        for(int i = 1; i < rooms.size(); i++) {
            Room prev = rooms.get(i - 1);
            Room cur = rooms.get(i);

            carvePath(prev, cur, TEMP_CORRIDOR);
        }
    }

    public boolean isPassable(int x, int y) {
        if(y < 0 || y >= MAP_ROWS || x < 0 || x >= MAP_COLS)
            return false;
        switch(tileMap[y][x].getGlyph()) {
            case RenderPanel.WALL: return false;
            case RenderPanel.DOOR_CLOSED: return false;
        }
//        if(tileMap[y][x].getGlyph() == posgima2.swing.RenderPanel.WALL)
//            return false;
        if(hasEntity(y, x)) {
            if(!getEntityAt(y, x).isAlive())
                return true;
            return false;
        }
        return true;
    }

    public boolean hasMonster(int y, int x) {
        // todo: refactor to only check for monster, not any entity
        if(tileMap[y][x].getEntity() != null)
        {
            return true;
        }

        return false;
    }

    public boolean hasEntity(int y, int x) {
        if(tileMap[y][x].getEntity() != null)
            return true;
        return false;
    }

    private Tile getRandomTileOf(char glyph) {
        int y = 0;
        int x = 0;
        do {
            y = (int) (Math.random() * MAP_ROWS);
            x = (int) (Math.random() * MAP_COLS);
        } while(tileMap[y][x].getGlyph() != glyph);
        return tileMap[y][x];
    }

    private void blastExtraEntrances(int amount) {

        for(int i = 0; i < amount; i++) {
            Room room1 = null;
            Room room2 = null;
            do {
                room1 = getRandomRoom();
                room2 = getRandomRoom();
            } while(room1 == room2);

            carvePath(room1, room2, TEMP_CORRIDOR);

        }
    }


    public boolean inRange(int y, int x) {
        return y > 0 && y < MAP_ROWS && x > 0 && x < MAP_COLS;
    }

    private boolean inRange(Vector2i v) {
        return inRange(v.getY(), v.getX());
    }

    private void sprinkleMonsters() {
        for(int i = 0; i < getMaxMonsterLimit(); i++) {
            Tile tile = null;
            do {
                tile = getRandomTileOf(RenderPanel.FLOOR);
            } while(tile.hasEntity());
            spawnRandomMonster();
        }
    }

    private void finishMap() {
        for(int y = 0; y < MAP_ROWS; y++) {
            for(int x = 0; x < MAP_COLS; x++) {
                if(charMap[y][x] == TEMP_CORRIDOR) {
                    charMap[y][x] = RenderPanel.FLOOR;
                } else if(charMap[y][x] == VOID) {
                    charMap[y][x] = RenderPanel.WALL;
                }
            }
        }
    }

    private void sprinkleItems() {
        for(int i = 0; i < 50; i++) {
            Sword sword = new Sword(RenderPanel.WEAPON, 5, 0, 0);
            getRandomTileOf(RenderPanel.FLOOR).addItem(sword);
        }
    }

    public void spawnRandomMonster(boolean[][] invalidList) {
        int times = 0;
        int timeout = 15;
        Tile tile = null;
        while (times < timeout) {
            tile = getRandomTileOf(RenderPanel.FLOOR);
            if (!invalidList[tile.getY()][tile.getX()]) {
                if (!tile.hasEntity()) {
                    break;
                }
            }
            times++;
            tile = null;
        }
        if (tile != null) {
            int m = (int) (Math.random() * 5);
            char glyph = 'x';
            String name = "x";
            switch (m) {
                case 0:
                    glyph = 'r';
                    name = "large rat";
                    break;
                case 1:
                    glyph = 'g';
                    name = "goblin";
                    break;
                case 2:
                    glyph = 'D';
                    name = "dragon";
                    break;
                case 3:
                    glyph = 'T';
                    name = "troll";
                    break;
                case 4:
                    glyph = 'b';
                    name = "bat";
                    break;
            }
            Monster monster = new Monster(glyph, getDifficulty());
            monster.setName(name);
            monsters.add(monster);
            tile.addEntity(monster);
        }
    }

    private Monster createRandomMonster() {
        int m = (int)(Math.random() * 5);
        char glyph = 'x';
        String name = "x";
        switch(m) {
            case 0: glyph = 'r'; name = "large rat"; break;
            case 1: glyph = 'g'; name = "goblin"; break;
            case 2: glyph = 'D'; name = "dragon"; break;
            case 3: glyph = 'T'; name = "troll"; break;
            case 4: glyph = 'b'; name = "bat"; break;
        }
        Monster monster = new Monster(glyph, getDifficulty());
        monster.setName(name);
        return monster;
    }

    public void spawnRandomMonster() {
        Tile tile = null;
        do{
            tile = getRandomTileOf(RenderPanel.FLOOR);
        } while(tile.hasEntity());

        Monster monster = createRandomMonster();
        monsters.add(monster);
        tile.addEntity(monster);
        //System.out.println("spawned mob");
    }

    public Room getRandomRoom() {
        return rooms.get((int)(Math.random() * rooms.size()));
    }

    private void carvePath(Vector2i start, Vector2i end, char fillglyph) {
        AStar aStar = new AStar(this);
        ArrayList<Vector2i> path = aStar.getPath(start, end, true, false);
        for(int i = 0; i < path.size(); i++) {
            Vector2i v = path.get(i);
            if(i == 0 || i == path.size() - 1) {
                charMap[v.getY()][v.getX()] = RenderPanel.DOOR_CLOSED;
            } else {
                charMap[v.getY()][v.getX()] = fillglyph;
            }
        }

    }

    public void carvePath(Room start, Room end, char fillglyph) {
        Vector2i startEntrance;
        Vector2i endEntrance;
        if(start.hasUnconnectedEntrance()) {
            startEntrance = start.getNextUnconnectedEntrance();
        } else if(start.canAddEntrance()) {
            startEntrance = start.getViableEntranceLocation();
            start.addEntrance(startEntrance, charMap);
        } else {
            SetupWindow.println(start + " no unconnected rooms, cannot add new entrance, using existing one");
            startEntrance = start.getEntrances().get((int)(Math.random() * start.getEntrances().size()));
        }

        if(end.hasUnconnectedEntrance()) {
            endEntrance = end.getNextUnconnectedEntrance();
        } else if(end.canAddEntrance()) {
            endEntrance = end.getViableEntranceLocation();
            end.addEntrance(endEntrance, charMap);
        } else {
            SetupWindow.println(end + " no unconnected rooms, cannot add new entrance, using existing one");
            endEntrance = end.getEntrances().get((int)(Math.random() * end.getEntrances().size()));
        }
        carvePath(startEntrance, endEntrance, fillglyph);
        start.connectEntrance(startEntrance);
        end.connectEntrance(endEntrance);
    }






    public void fillMap(char glyph) {
        for(int y = 0; y < MAP_ROWS; y++) {
            for(int x = 0; x < MAP_COLS; x++) {
                charMap[y][x] = glyph;
            }
        }
    }





    public boolean canPlaceRoom(int height, int width, Vector2i center) {
        if(height % 2 == 0 || width % 2 == 0)
            return false;

        int startRow = center.getY() - (height / 2);
        int startCol = center.getX() - (width / 2);

        int endRow = center.getY() + (height / 2);
        int endCol = center.getX() + (width / 2);

        if(startRow < 2 || startRow > MAP_ROWS - 2)
            return false;
        if(startCol < 2 || startCol > MAP_COLS - 2)
            return false;
        if(endRow < 2 || endRow > MAP_ROWS - 2)
            return false;
        if(endCol < 2 || endCol > MAP_COLS - 2)
            return false;

        ArrayList<Character> invalidChars = new ArrayList<Character>();
        invalidChars.add(RenderPanel.FLOOR);
        invalidChars.add(RenderPanel.WALL);
        invalidChars.add(RenderPanel.DOOR_OPEN);
        invalidChars.add(RenderPanel.DOOR_CLOSED);
        // Extend row and col one space to make sure there is nothing touching this room
        for(int y = startRow - 1; y <= endRow + 1; y++) {
            for(int x = startCol - 1; x <= endCol + 1; x++) {
                for (Character c : invalidChars) {
                    if (charMap[y][x] == c)
                        return false;
                }
            }
        }
        return true;
    }

    public char[][] getMap() {
        return charMap;
    }

    public Room createRandomRoom() {
        int height;
        int width;
        Vector2i center;
        //posgima2.misc.Direction dir;
        int maxTimeout = 50;
        int timeout = 0;
        //int offset = 0;
        do {
            if(timeout == maxTimeout)
                return null;
            int roomSize = (int)(Math.random() * 5);
            height = 5;
            width = 5;
            switch (roomSize) {
                case 0:
                    height = 7;
                    width = 7;
                    break;
                case 1:
                    height = 9;
                    width = 9;
                    break;
                case 2:
                    height = 11;
                    width = 11;
                    break;
                case 3:
                    height = 13;
                    width = 13;
                    break;
                case 4:
                    height = 5;
                    width = 5;
            }
            int x = (int) (Math.random() * MAP_COLS);
            int y = (int) (Math.random() * MAP_ROWS);
            center = new Vector2i(y, x);
            timeout++;
        } while(!canPlaceRoom(height, width, center));
        if (canPlaceRoom(height, width, center)) {
            return new Room(height, width, center, charMap);
        }
        return null;
    }

    public void resetVisibility() {
        for(int y = 0; y < visibleMap.length; y++) {
            for(int x = 0; x < visibleMap[0].length; x++) {
                visibleMap[y][x] = false;
            }
        }
    }

    public void recalculateVisibility(Vector2i center) {
        resetVisibility();
        for(Vector2i v : FieldOfView.bresenhamFov(tileMap, center.getY(), center.getX(), 0)) {
            visibleMap[v.getY()][v.getX()] = true;
            exploredMap[v.getY()][v.getX()] = true;
        }
    }

    public void setFullyExplored() {
        for(int y = 0; y < exploredMap.length; y++) {
            for(int x = 0; x < exploredMap[0].length; x++) {
                exploredMap[y][x] = true;
            }
        }
    }

    public boolean[][] getVisibleMap() {
        return visibleMap;
    }

    public boolean[][] getExploredMap() {
        return exploredMap;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public Monster getMonsterAt(int row, int col) {
        return (Monster) tileMap[row][col].getEntity();
    }

    public Entity getEntityAt(int row, int col) {
        return tileMap[row][col].getEntity();
    }

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public boolean hasItems(int i, int j) {
        return tileMap[i][j].hasItems();
    }

    public void toggleDoor(Tile targetTile) {
        if(targetTile.getGlyph() == RenderPanel.DOOR_CLOSED) {
            targetTile.setGlyph(RenderPanel.DOOR_OPEN);
        } else if(targetTile.getGlyph() == RenderPanel.DOOR_OPEN) {
            targetTile.setGlyph(RenderPanel.DOOR_CLOSED);
        } else {
            WindowFrame.writeConsole("/warning/toggleDoor() error");
        }
    }

    public int getMAP_ROWS() {
        return MAP_ROWS;
    }

    public int getMAP_COLS() {
        return MAP_COLS;
    }

    public int getMaxMonsterLimit() {
        return maxMonsterLimit;
    }

    public void setMaxMonsterLimit(int maxMonsterLimit) {
        this.maxMonsterLimit = maxMonsterLimit;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
