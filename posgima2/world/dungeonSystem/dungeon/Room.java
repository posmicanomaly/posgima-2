package posgima2.world.dungeonSystem.dungeon;

import posgima2.misc.Direction;
import posgima2.misc.Vector2i;
import posgima2.item.Item;
import posgima2.swing.RenderPanel;
import posgima2.swing.SetupWindow;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 12/31/2014.
 */
public class Room {
    private ArrayList<Vector2i> entrances;
    private ArrayList<Boolean> connectedEntrances;
    private int totalEntrances;
    private static final int maxEntrances = 4;
    private Vector2i center;
    private int height;
    private int width;

    public Room(int height, int width, Vector2i entrance, Direction direction, int offset, char[][] charMap) {
        entrances = new ArrayList<Vector2i>();
        connectedEntrances = new ArrayList<Boolean>();
        totalEntrances = -1;

        this.height = height;
        this.width = width;

        // only accept odd dimensions
        if(height % 2 == 0 || width % 2 == 0)
            return;

        // unsignedOffset is to check for an offset too large in relation to the dimension
        int unsignedOffset = Math.abs(offset);

        // posgima2.misc.Direction is only meant to go one way, so if Y is set, then offset will be based on width
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
        addEntrance(entrance, charMap);
    }

    public Room(int height, int width, Vector2i center, char[][] charMap) {
        entrances = new ArrayList<Vector2i>();
        connectedEntrances = new ArrayList<Boolean>();
        //totalEntrances = -1;

        this.height = height;
        this.width = width;
        this.center = center;

        // only accept odd dimensions
        if(height % 2 == 0 || width % 2 == 0)
            return;

        // unsignedOffset is to check for an offset too large in relation to the dimension
        //int unsignedOffset = Math.abs(offset);

        // posgima2.misc.Direction is only meant to go one way, so if Y is set, then offset will be based on width
//        if(direction.getyMod() != 0) {
//            if(unsignedOffset >= width / 2)
//                return;
//        }
//        // If X is set, then offset will be based on height
//        if(direction.getxMod() != 0) {
//            if(unsignedOffset >= height / 2)
//                return;
//        }


//        int entranceY = entrance.getY();
//        int entranceX = entrance.getX();
//
//        // Default with no direction means a room centered at the entrance, it will not have any way to get in or out
//        int centerY = entranceY;
//        int centerX = entranceX;
//
//        // y < 0 room is growing "up",
//        // y > 0 room is growing "down"
//        if(direction.getyMod() < 0) {
//            centerY = entranceY - (height / 2);
//            centerX = entranceX - offset;
//        } else if(direction.getyMod() > 0) {
//            centerY = entranceY + (height / 2);
//            centerX = entranceX - offset;
//        }
//
//        // x < 0 room is growing "left"
//        // x > 0 room is growing "right"
//        if(direction.getxMod() < 0) {
//            centerY = entranceY - offset;
//            centerX = entranceX - (height / 2);
//        } else if(direction.getxMod() > 0) {
//            centerY = entranceY - offset;
//            centerX = entranceX + (height / 2);
//        }

//        center = new posgima2.misc.Vector2i(centerY, centerX);

        int startRow = center.getY() - (height / 2);
        int startCol = center.getX() - (width / 2);

        int endRow = center.getY() + (height / 2);
        int endCol = center.getX() + (width / 2);

        for(int y = startRow; y <= endRow; y++) {
            for(int x = startCol; x <= endCol; x++) {
//                // Place a door
//                if(y == entranceY && x == entranceX) {
//                    charMap[y][x] = posgima2.swing.RenderPanel.DOOR_CLOSED;
//                }
                // Top or bottom row, place a wall
                if(y == startRow || y == endRow) {
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
        //addEntrance(entrance, charMap);
    }

    public void addItem(Tile[][] tileMap, Item item) {
        int y = center.getY();
        int x = center.getX();

        tileMap[y][x].addItem(item);
    }

    public Vector2i getMainEntrance() {
        return entrances.get(0);
    }

    public Vector2i getNextUnconnectedEntrance() {
        //System.out.println("getNext pre\n" + getEntranceInformation());
        for(int i = 0; i < entrances.size(); i++) {
            if(connectedEntrances.get(i) == false) {
                //System.out.println(i + "==false");
                return entrances.get(i);
            }
        }
        return null;
    }

    public void connectEntrance(Vector2i entrance) {
        //System.out.println("pre connection");
        //System.out.println(getEntranceInformation());
        for(int i = 0; i < entrances.size(); i++) {
            if(entrances.get(i).getY() == entrance.getY() && entrances.get(i).getX() == entrance.getX()) {
                connectedEntrances.set(i, new Boolean(true));
                break;
            }
        }
        //System.out.println("post connection");
        //System.out.println(getEntranceInformation());
    }

    public boolean canAddEntrance() {
        if(entrances.size() < maxEntrances) {
            return true;
        }
        SetupWindow.println("can't add any more entrances");
        return false;
    }

    public boolean addEntrance(Vector2i entrance, char[][] charMap) {
        entrances.add(entrance);
        connectedEntrances.add(new Boolean(false));

        charMap[entrance.getY()][entrance.getX()] = RenderPanel.DOOR_CLOSED;
        //SetupWindow.println("add entrance");
        return true;
    }

    public String getEntranceInformation() {
        String result = "";
        for(int i = 0; i < entrances.size(); i++) {
            result += "[" + entrances.get(i).getY() + ", " + entrances.get(i).getX() + ": ";
            if(connectedEntrances.get(i) == true)
                result += "T] ";
            else
                result += "F] ";
        }
        return result;
    }

    public Vector2i getCenter() {
        return center;
    }

    private int getSide(Vector2i entrance) {
        int topRow = center.getY() - (height / 2);
        int bottomRow = center.getY() + (height / 2);
        int leftSide = center.getX() - (width / 2);
        int rightSide = center.getX() + (width / 2);
        if(entrance.getY() == topRow) {
            return 0;
        } else if(entrance.getY() == bottomRow) {
            return 1;
        }

        if(entrance.getX() == leftSide) {
            return 2;
        } else if(entrance.getX() == rightSide) {
            return 3;
        }
        SetupWindow.println("getside returned -1, that's bad");
        return -1;
    }

    private Vector2i getRandomWallOnSide(int side) {
        if(side == -1) {
            SetupWindow.println("getRandomWallOnSide error side is -1, this is bad");
            return null;
        }
        int topRow = center.getY() - (height / 2);
        int bottomRow = center.getY() + (height / 2);
        int leftSide = center.getX() - (width / 2);
        int rightSide = center.getX() + (width / 2);
        int y = center.getY();
        int x = center.getX();
        int low = 0;
        int high = 0;
        switch(side) {
            case 0: y = topRow; break;
            case 1: y = bottomRow; break;
            case 2: x = leftSide; break;
            case 3: x = rightSide; break;
        }

        if(side == 0 || side == 1) {
            low = leftSide + 1;
            high = rightSide - 1;
            x = new Random().nextInt((high - low) + 1) + low;
        } else if(side == 2 || side == 3) {
            low = topRow + 1;
            high = bottomRow - 1;
            y = new Random().nextInt((high - low) + 1) + low;
        }
        return new Vector2i(y, x);
    }

    public Vector2i getViableEntranceLocation() {
        // 0: top, 1: bottom, 2: left: 3: right
        boolean[] sides = new boolean[4];
        for(Vector2i v : entrances) {
            int side = getSide(v);
            if(side != -1) {
                // mark this side as having an entrance
                sides[side] = true;
            }
        }

        // pick a random side that doesn't have an entrance already
        int viableSide;
        if(sides[0] && sides[1] && sides[2] && sides[3]) {
            SetupWindow.println("all sides have an entrance, we shouldn't be here, picking random entrance that exists");
            return getRandomWallOnSide(viableSide = (int)(Math.random() * 4));
        }
        do {
            viableSide = (int)(Math.random() * 4);
        } while(sides[viableSide] == true);

        return getRandomWallOnSide(viableSide);
    }

    public boolean hasUnconnectedEntrance() {
        for(Boolean b : connectedEntrances) {
            if(b == false)
                return true;
        }
        return false;
    }

    public ArrayList<Vector2i> getEntrances() {
        return entrances;
    }
}
