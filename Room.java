/**
 * Created by Jesse Pospisil on 12/31/2014.
 */
public class Room {
    private Vector2i entrance;
    private Vector2i center;
    private int height;
    private int width;

    public Room(int height, int width, Vector2i entrance, Direction direction, int offset, char[][] charMap) {
        this.entrance = entrance;
        this.height = height;
        this.width = width;

        // only accept odd dimensions
        if(height % 2 == 0 || width % 2 == 0)
            return;

        // unsignedOffset is to check for an offset too large in relation to the dimension
        int unsignedOffset = Math.abs(offset);

        // Direction is only meant to go one way, so if Y is set, then offset will be based on width
        if(direction.getyMod() != 0) {
            if(unsignedOffset >= width / 2)
                return;
        }
        // If X is set, then offset will be based on height
        if(direction.getxMod() != 0) {
            if(unsignedOffset >= height / 2)
                return;
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

        center = new Vector2i(centerY, centerX);

        int startRow = centerY - (height / 2);
        int startCol = centerX - (width / 2);

        int endRow = centerY + (height / 2);
        int endCol = centerX + (width / 2);

        for(int y = startRow; y <= endRow; y++) {
            for(int x = startCol; x <= endCol; x++) {
                // Place a door
                if(y == entranceY && x == entranceX) {
                    charMap[y][x] = RenderPanel.DOOR_CLOSED;
                }
                // Top or bottom row, place a wall
                else if(y == startRow || y == endRow) {
                    charMap[y][x] = RenderPanel.WALL;
                }
                // Left or right column, place a wall
                else if(x == startCol || x == endCol) {
                    charMap[y][x] = RenderPanel.WALL;
                }
                // Place a floor
                else {
                    charMap[y][x] = RenderPanel.FLOOR;
                }
            }
        }
    }

    public void addItem(Tile[][] tileMap, Item item) {
        int y = center.getY();
        int x = center.getX();

        tileMap[y][x].addItem(item);
    }

    public Vector2i getEntrance() {
        return entrance;
    }

    public Vector2i getCenter() {
        return center;
    }
}
