import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/28/2014.
 */
public class Dungeon {
    public static final char TEMP_CORRIDOR = 'C';
    public static final char VOID = ' ';
    public static final int ANCHOR_CENTER = 4;
    public static final int ANCHOR_UPPER_LEFT = 0;
    public static final int ANCHOR_LOWER_LEFT = 1;
    public static final int ANCHOR_LOWER_RIGHT = 2;
    public static final int ANCHOR_UPPER_RIGHT = 3;

    private ArrayList<Room> rooms;


    char[][] charMap;
    int MAP_ROWS;
    int MAP_COLS;
    private boolean[][] visibleMap;
    private boolean[][] exploredMap;
    private ArrayList<Monster> monsters;

    public Dungeon(int rows, int cols) {
        this.MAP_ROWS = rows;
        this.MAP_COLS = cols;
        charMap = new char[rows][cols];
        visibleMap = new boolean[rows][cols];
        exploredMap = new boolean[rows][cols];
        monsters = new ArrayList<Monster>();
        fillMap(VOID);

        rooms = new ArrayList<Room>();

        for(int i = 0; i < 100; i++) {
            addRandomRoom();
        }
        int[][] roomConnections = new int[rooms.size()][rooms.size()];
        System.out.println(rooms.size() + " rooms to connect");
        int connections = 0;
        for(int room = 0; room < rooms.size(); room++) {
            for(int otherRoom = 0; otherRoom < rooms.size(); otherRoom++) {
                if(roomConnections[room][otherRoom] == 0 && roomConnections[otherRoom][room] == 0) {
                    if(rooms.get(room) != rooms.get(otherRoom)) {
                        carvePath(rooms.get(room), rooms.get(otherRoom));
                        roomConnections[room][otherRoom] = 1;
                        connections++;
                    }
                }
            }
        }
        System.out.println("connections made: " + connections);
        finishMap();
        sprinkleItems();
        sprinkleMonsters();
    }

    public boolean isPassable(int x, int y) {
        if(y < 0 || y >= MAP_ROWS || x < 0 || x >= MAP_COLS)
            return false;
        if(charMap[y][x] == RenderPanel.WALL)
            return false;
        if(hasMonster(y, x))
            return false;
        return true;
    }

    public boolean hasMonster(int y, int x) {
        for(Monster m : monsters) {
            if(m.getY() == y && m.getX() == x)
                return true;
        }
        return false;
    }

    private Vector2i getRandomSpaceOf(char glyph) {
        int y = 0;
        int x = 0;
        do {
            y = (int) (Math.random() * MAP_ROWS);
            x = (int) (Math.random() * MAP_COLS);
        } while(charMap[y][x] != glyph);
        return new Vector2i(y, x);
    }

    private void sprinkleMonsters() {
        for(int i = 0; i < 50; i++) {
            Vector2i loc = getRandomSpaceOf(RenderPanel.FLOOR);
            monsters.add(new Monster(loc.getY(), loc.getX()));
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
            getRandomRoom().addItem(charMap, RenderPanel.SCROLL);
        }

        for(int i = 0; i < 25; i++) {
            getRandomRoom().addItem(charMap, RenderPanel.ITEM);
        }

        for(int i = 0; i < 10; i++) {
            getRandomRoom().addItem(charMap, RenderPanel.WEAPON);
        }
    }

    public Room getRandomRoom() {
        return rooms.get((int)(Math.random() * rooms.size()));
    }

    public void carvePath(Vector2i start, Vector2i end) {
        AStar aStar = new AStar(charMap);
        ArrayList<Vector2i> path = aStar.getPath(start, end);
        for(int i = 0; i < path.size(); i++) {
            Vector2i v = path.get(i);
            if(i == 0 || i == path.size()) {
                charMap[v.getY()][v.getX()] = RenderPanel.DOOR_OPEN;
            } else {
                charMap[v.getY()][v.getX()] = TEMP_CORRIDOR;
            }
        }
    }

    public void carvePath(Room start, Room end) {
        AStar aStar = new AStar(charMap);
        ArrayList<Vector2i> path = aStar.getPath(start.getEntrance(), end.getEntrance());
        for(int i = 1; i < path.size() - 1; i++) {
            Vector2i v = path.get(i);
            if(i == 0 || i == path.size() - 1) {
                charMap[v.getY()][v.getX()] = RenderPanel.DOOR_OPEN;
            } else {
                charMap[v.getY()][v.getX()] = TEMP_CORRIDOR;
            }
        }
    }






    public void fillMap(char glyph) {
        for(int y = 0; y < MAP_ROWS; y++) {
            for(int x = 0; x < MAP_COLS; x++) {
                charMap[y][x] = glyph;
            }
        }
    }






    public boolean newCanCarveRoom(int height, int width, Vector2i entrance, Direction direction, int offset) {
        // only accept odd dimensions
        if(height % 2 == 0 || width % 2 == 0)
            return false;
        // unsignedOffset is to check for an offset too large in relation to the dimension
        int unsignedOffset = offset;
        if(unsignedOffset < 0)
            unsignedOffset = unsignedOffset * 1;

        if(direction.getyMod() != 0) {
            if(unsignedOffset >= width / 2)
                return false;
        }

        if(direction.getxMod() != 0) {
            if(unsignedOffset >= height / 2)
                return false;
        }


        int entranceY = entrance.getY();
        int entranceX = entrance.getX();

        // Default with no direction means a room centered at the entrance, it will not have any way to get in or out
        int centerY = entranceY;
        int centerX = entranceX;

        // y < 0 room is growing "up",
        // y > 0 room is growing "down"
        if(direction.getyMod() < 0) {
            centerY = entranceY - (height / 2);
            centerX = entranceX - offset;
        } else if(direction.getyMod() > 0) {
            centerY = entranceY + (height / 2);
            centerX = entranceX - offset;
        }

        // x < 0 room is growing "left"
        // x > 0 room is growing "right"
        if(direction.getxMod() < 0) {
            centerY = entranceY - offset;
            centerX = entranceX - (height / 2);
        } else if(direction.getxMod() > 0) {
            centerY = entranceY - offset;
            centerX = entranceX + (height / 2);
        }

        int startRow = centerY - (height / 2);
        int startCol = centerX - (width / 2);

        int endRow = centerY + (height / 2);
        int endCol = centerX + (width / 2);

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

    /**
     * Carve a room with the locY and locX as the center, with amount of MAP_ROWS and columns to cover.
     * @param rows
     * @param cols
     * @param locY
     * @param locX
     */
    public void carveRoom(int rows, int cols, int locY, int locX, int anchor) {
        switch(anchor) {
            case ANCHOR_UPPER_LEFT:
                for(int y = locY; y < rows + locY; y++) {
                    for(int x = locX; x < cols + locX; x++) {
                        if(y == locY || y == locY + rows) {
                            charMap[y][x] = RenderPanel.WALL;
                        } else if(x == locX || x == locX + rows) {
                            charMap[y][x] = RenderPanel.WALL;
                        }
                        charMap[y][x] = RenderPanel.FLOOR;
                    }
                }
                break;
            case ANCHOR_LOWER_LEFT:
                for(int y = locY; y > locY - rows; y--) {
                    for(int x = locX; x < cols + locX; x++) {
                        charMap[y][x] = RenderPanel.FLOOR;
                    }
                }
                break;
            case ANCHOR_LOWER_RIGHT:
                for(int y = locY; y > locY - rows; y--) {
                    for(int x = locX; x > locX - rows; x--) {
                        charMap[y][x] = RenderPanel.FLOOR;
                    }
                }
                break;
            case ANCHOR_UPPER_RIGHT:
                for(int y = locY; y < locY + rows; y++) {
                    for(int x = locX; x > locX - rows; x--) {
                        charMap[y][x] = RenderPanel.FLOOR;
                    }
                }
                break;
            case ANCHOR_CENTER:
                for(int y = locY - rows / 2; y < locY + rows / 2; y++) {
                    for(int x = locX - cols / 2; x < locX + cols / 2; x++) {
                        charMap[y][x] = RenderPanel.FLOOR;
                    }
                }
                break;
        }
    }

    /**
     * Carve a corridor starting at locY locX, in a direction, with a length
     * dir: 0 up, 1 down, 2 left, 3 right
     * @param locY
     * @param locX
     * @param dir
     * @param length
     */
    public void carveCorridor(int locY, int locX, int dir, int length) {
        if(dir == 0) {
            for(int y = locY; y > locY - length; y--) {
                charMap[y][locX] = RenderPanel.FLOOR;
            }
        }
        else if(dir == 1) {
            for(int y = locY; y < locY + length; y++) {
                charMap[y][locX] = RenderPanel.FLOOR;
            }
        }
        else if(dir == 2)
            for(int x = locX; x > locX - length; x--) {
                charMap[locY][x] = RenderPanel.FLOOR;
            }
        else if(dir == 3)
            for(int x = locX; x < locX + length; x++) {
                charMap[locY][x] = RenderPanel.FLOOR;
            }

    }

    /**
     * Picks the first random wall encountered near coordinates
     * @param y
     * @param x
     * @return
     */
    public Vector2i getRandomWallNear(int y, int x) {
        int currentX = x;
        int currentY = y;
        boolean wallFound = false;
        while(!wallFound) {
            int randomDirection = (int) (Math.random() * 4);
            switch (randomDirection) {
                case 0: currentY--; break;
                case 1: currentY++; break;
                case 2: currentX--; break;
                case 3: currentX++; break;
            }
            if(charMap[currentY][currentX] == RenderPanel.WALL) {
                wallFound = true;
            }
        }
        return new Vector2i(currentY, currentX);
    }

    /**
     * Checks the adjacent locations to determine where the floor is in relation to this wall
     * @param v
     * @return direction, or -1 if there was a problem
     */
    public int sideOfRoom(Vector2i v) {
        // return if this isn't a wall, this method is only used to find which side of a wall we're dealing with.
        if(charMap[v.getY()][v.getX()] != RenderPanel.WALL) {
            return -1;
        }
        if(charMap[v.getY() - 1][v.getX()] != RenderPanel.WALL)
            return 1;
        else if(charMap[v.getY() + 1][v.getX()] != RenderPanel.WALL)
            return 0;
        else if(charMap[v.getY()][v.getX() - 1] != RenderPanel.WALL)
            return 3;
        else if(charMap[v.getY()][v.getX() + 1] != RenderPanel.WALL)
            return 2;
        //return -1 because no floor was found
        else
            return -1;
    }

    private Vector2i endOfCorridor(int y, int x, int dir, int length) {
        int endY = y;
        int endX = x;
        switch(dir) {
            case 0: endY -= length; break;
            case 1: endY += length; break;
            case 2: endX -= length; break;
            case 3: endX += length; break;
        }
        return new Vector2i(endY, endX);
    }

    public char[][] getMap() {
        return charMap;
    }

    public void addRandomRoom() {
        int height;
        int width;
        Vector2i entrance;
        Direction dir;
        int maxTimeout = 50;
        int timeout = 0;
        int offset = 0;
        do {
            if(timeout == maxTimeout)
                return;
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
            entrance = new Vector2i(y, x);
            int direction = (int) (Math.random() * 4);
            dir = new Direction(-1, 0);
            int offsetEdgeSize = 0;
            switch (direction) {
                case 0:
                    dir = new Direction(-1, 0);
                    offsetEdgeSize = width;
                    break;
                case 1:
                    dir = new Direction(1, 0);
                    offsetEdgeSize = width;
                    break;
                case 2:
                    dir = new Direction(0, -1);
                    offsetEdgeSize = height;
                    break;
                case 3:
                    dir = new Direction(0, 1);
                    offsetEdgeSize = height;
                    break;
            }
            offset = (int)(Math.random() * (offsetEdgeSize / 2));
            if((int)(Math.random() * 2) != 0) {
                offset = -offset;
            }
            timeout++;
        } while(!newCanCarveRoom(height, width, entrance, dir, offset));
        if (newCanCarveRoom(height, width, entrance, dir, offset)) {
            rooms.add(new Room(height, width, entrance, dir, offset, charMap));
        }
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
        for(Vector2i v : FieldOfView.bresenhamFov(charMap, center.getY(), center.getX(), 0)) {
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
        for(Monster m : monsters) {
            if(m.getY() == row && m.getX() == col)
                return m;
        }
        return null;
    }
}
