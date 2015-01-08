package posgima2;

import posgima2.swing.RenderPanel;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/1/2015.
 */
public class FieldOfView {

    public static void shadowCasting(Vector2i location, int radius) {
        int slope;
    }

    public static ArrayList<Vector2i> bresenhamFov(Tile[][] grid, int y0, int x0, int radius) {
        ArrayList<Vector2i> fov = new ArrayList<Vector2i>();
        int yTop = y0 - radius;
        int yBottom = y0 + radius;

        int xLeft = x0 - radius;
        int xRight = x0 + radius;

        if(radius == 0) {
            yTop = 0;
            yBottom = grid.length - 1;
            xLeft = 0;
            xRight = grid[0].length - 1;
        }
        for(int x = xLeft; x < xRight; x++) {
            fov.addAll(findLine(grid, y0, x0, yTop, x));
            fov.addAll(findLine(grid, y0, x0, yBottom, x));
        }
        for(int y = yTop; y < yBottom; y++) {
            fov.addAll(findLine(grid, y0, x0, y, xLeft));
            fov.addAll(findLine(grid, y0, x0, y, xRight));
        }
        return fov;
    }

    /**
     * Returns the list of array elements that comprise the line.
     * @param grid the 2d array
     * @param x0 the starting point x
     * @param y0 the starting point y
     * @param x1 the finishing point x
     * @param y1 the finishing point y
     * @return the line as a list of array elements
     */
    public static ArrayList<Vector2i> findLine(Tile[][] grid, int y0, int x0, int y1, int x1) {
        ArrayList<Vector2i> line = new ArrayList<Vector2i>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx-dy;
        int e2;
        int currentX = x0;
        int currentY = y0;
        while(true) {
            if(currentX == x1 && currentY == y1) {
                line.add(new Vector2i(currentY, currentX));
                break;
            } else if(isBlocked(grid, currentY, currentX)) {
                line.add(new Vector2i(currentY, currentX));
                break;
            } else if(!(currentY == y0 && currentX == x0)) {
                line.add(new Vector2i(currentY, currentX));
            }
            e2 = 2 * err;
            if (e2 > -1 * dy) {
                err = err - dy;
                currentX = currentX + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                currentY = currentY + sy;
            }

        }
        return line;
    }

    private static boolean isBlocked(Tile[][] grid, int currentY, int currentX) {
        switch(grid[currentY][currentX].getGlyph()) {
            case RenderPanel.WALL: return true;
            case RenderPanel.DOOR_CLOSED: return true;
        }
        return false;
    }

}
