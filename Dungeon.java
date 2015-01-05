import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by Jesse Pospisil on 12/28/2014.
 */
public class Dungeon {
    public static final char TEMP_CORRIDOR = 'C';
    public static final char VOID = ' ';

    private ArrayList<Room> rooms;


    char[][] charMap;
    Tile[][] tileMap;
    int MAP_ROWS;
    int MAP_COLS;
    private boolean[][] visibleMap;
    private boolean[][] exploredMap;
    private ArrayList<Monster> monsters;

    public Dungeon(int rows, int cols) {
        this.MAP_ROWS = rows;
        this.MAP_COLS = cols;
        charMap = new char[rows][cols];
        tileMap = new Tile[rows][cols];
        visibleMap = new boolean[rows][cols];
        exploredMap = new boolean[rows][cols];
        monsters = new ArrayList<Monster>();
        fillMap(VOID);

        rooms = new ArrayList<Room>();

        WindowFrame.setupWindow.println("Creating and connecting rooms");
        createAndConnectRooms(40);
        WindowFrame.setupWindow.println("total rooms: " + rooms.size());

        WindowFrame.setupWindow.println("blasting rooms");
        blastExtraEntrances(rooms.size() / 4);

        WindowFrame.setupWindow.println("finishing map");
        finishMap();
        WindowFrame.setupWindow.println("creating tileMap");
        for(int y = 0; y < MAP_ROWS; y++) {
            for(int x = 0; x < MAP_COLS; x++) {
                Tile tile = new Tile(charMap[y][x], y, x);
                tileMap[y][x] = tile;
            }
        }

        WindowFrame.setupWindow.println("sprinkling items");
        sprinkleItems();
        WindowFrame.setupWindow.println("sprinkling monsters");
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
//        if(tileMap[y][x].getGlyph() == RenderPanel.WALL)
//            return false;
        if(hasMonster(y, x) && getMonsterAt(y, x).isAlive())
            return false;
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
        for(int i = 0; i < rooms.size(); i++) {
            Tile tile = null;
            do {
                tile = getRandomTileOf(RenderPanel.FLOOR);
            } while(tile.hasEntity());
            Monster monster = new Monster('r');
            monsters.add(monster);
            tile.addEntity(monster);
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
        for(int i = 0; i < 25; i++) {
            Item item = new Item(RenderPanel.SCROLL);
            getRandomTileOf(RenderPanel.FLOOR).addItem(item);
        }

        for(int i = 0; i < 25; i++) {
            Item item = new Item(RenderPanel.ITEM);
            getRandomRoom().addItem(tileMap, item);
        }

        for(int i = 0; i < 10; i++) {
            Item item = new Item(RenderPanel.WEAPON);
            getRandomRoom().addItem(tileMap, item);
        }
    }

    public void spawnMonster(char glyph, boolean[][] invalidList) {
        Tile tile = null;
        do{
            tile = getRandomTileOf(RenderPanel.FLOOR);
        } while(invalidList[tile.getY()][tile.getX()] == true && tile.hasEntity());
        Monster monster = new Monster(glyph);
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
            WindowFrame.setupWindow.println(start + " no unconnected rooms, cannot add new entrance, using existing one");
            startEntrance = start.getEntrances().get((int)(Math.random() * start.getEntrances().size()));
        }

        if(end.hasUnconnectedEntrance()) {
            endEntrance = end.getNextUnconnectedEntrance();
        } else if(end.canAddEntrance()) {
            endEntrance = end.getViableEntranceLocation();
            end.addEntrance(endEntrance, charMap);
        } else {
            WindowFrame.setupWindow.println(end + " no unconnected rooms, cannot add new entrance, using existing one");
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
        //Direction dir;
        int maxTimeout = 50;
        int timeout = 0;
        //int offset = 0;
        do {
            if(timeout == maxTimeout)
                return null;
            int roomSize = (int)(Math.random() * 4);
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
}
