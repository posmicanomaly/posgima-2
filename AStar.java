import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/31/2014.
 */
public class AStar {
    Dungeon dungeon;
    ArrayList<AStarNode> open;
    ArrayList<AStarNode> closed;

    public AStar(Dungeon dungeon) {
        this.dungeon = dungeon;
    }
    public ArrayList<Vector2i> getPath(Vector2i start, Vector2i end, boolean build, boolean withinRange) {

        open = new ArrayList<AStarNode>();
        closed = new ArrayList<AStarNode>();

        AStarNode startNode = new AStarNode(start.getY(), start.getX());
        startNode.setgCost(0);

        open.add(startNode);
        AStarNode current = null;
        while(!open.isEmpty()) {
            current = getBestNode(open);
            closed.add(current);
            if(current.x ==end.getX() && current.y == end.getY())
                break;
            open.remove(current);
            ArrayList<AStarNode> surroundingNodes = null;
            if(!withinRange) {
                surroundingNodes = getSurroundingNodes(current);
            } else {
                surroundingNodes = getSurroundingNodesWithinRange(current, start, end);
            }
            for(AStarNode node : surroundingNodes) {
                if(hasSameNode(closed, node) != null) {
                    continue;
                }

                if(hasSameNode(open, node) == null) {
                    open.add(node);
                    node.setParent(current);
                    if(build) {
                        node.setgCost(getBuildCostOfGlyph(dungeon.getMap()[node.y][node.x]));
                    } else {
                        node.setgCost(getCostOfGlyph(dungeon.getTileMap()[node.y][node.x]));
                    }
                    node.calcHCost(end.getY(), end.getX());
                    node.calcFCost();
                } else {
                    //current.calcFCost();
                    //node.calcFCost();
                    if(current.fCost < node.fCost) {
                        node.setParent(current);
                    }
                }
            }
        }
        if(open.isEmpty()) {
            WindowFrame.setupWindow.println("no path");
        } else {
            ArrayList<Vector2i> path = new ArrayList<Vector2i>();
            AStarNode parent = current;
            while(parent != null) {
                path.add(new Vector2i(parent.y, parent.x));
                parent = parent.parent;
            }
            //WindowFrame.setupWindow.println("closed " + closed.size());
            return path;
        }
        return null;
    }



    public AStarNode hasSameNode(ArrayList<AStarNode> nodes, AStarNode node) {
        int y = node.y;
        int x = node.x;
        for(AStarNode a : nodes) {
            if(a.x == x && a.y == y)
                return a;
        }
        return null;
    }

    public int getBuildCostOfGlyph(char glyph) {
        switch(glyph) {
            case RenderPanel.WALL: return 3;
            case Dungeon.VOID: return 2;
            case Dungeon.TEMP_CORRIDOR: return 1;
            case RenderPanel.FLOOR: return 1000;
            case RenderPanel.DOOR_CLOSED: return 1000;
            default: return 2;
        }
    }

    public int getCostOfGlyph(char glyph) {
        switch(glyph) {
            case RenderPanel.WALL: return 10;
            case Dungeon.VOID: return 5;
            case Dungeon.TEMP_CORRIDOR: return 1;
            default: return 2;
        }
    }

    public int getCostOfGlyph(Tile tile) {
        if(tile.hasEntity())
            return 10;
        return getCostOfGlyph(tile.getGlyph());
    }

    private ArrayList<AStarNode> getSurroundingNodesWithinRange(AStarNode current, Vector2i start, Vector2i end) {
        ArrayList<AStarNode> nodes = new ArrayList<>();

        //for range
        int xd = Math.abs(start.getX() - end.getX());
        int yd = Math.abs(start.getY() - end.getY());

        int minY = start.y - yd * 4;
        int maxY = start.y + yd * 4;

        int minX = start.x - xd * 4;
        int maxX = start.x + xd * 4;

        if(current.y - 1 > 0 && current.y - 1 > minY)
            nodes.add(new AStarNode(current.y - 1, current.x));
        //down
        if(current.y + 1 < dungeon.getMap().length && current.y + 1 < maxY)
            nodes.add(new AStarNode(current.y + 1, current.x));
        //left
        if(current.x - 1 > 0 && current.x - 1 > minX)
            nodes.add(new AStarNode(current.y, current.x - 1));
        //right
        if(current.x + 1 < dungeon.getMap()[0].length && current.x + 1 < maxX)
            nodes.add(new AStarNode(current.y, current.x + 1));

        return nodes;
    }

    public ArrayList<AStarNode> getSurroundingNodes(AStarNode current) {
        ArrayList<AStarNode> nodes = new ArrayList<AStarNode>();
        //up
        if(current.y - 1 > 0)
            nodes.add(new AStarNode(current.y - 1, current.x));
        //down
        if(current.y + 1 < dungeon.getMap().length)
            nodes.add(new AStarNode(current.y + 1, current.x));
        //left
        if(current.x - 1 > 0)
            nodes.add(new AStarNode(current.y, current.x - 1));
        //right
        if(current.x + 1 < dungeon.getMap()[0].length)
            nodes.add(new AStarNode(current.y, current.x + 1));

        return nodes;
    }

    public class AStarNode{
        int y;
        int x;
        int gCost;
        int hCost;
        int fCost;
        AStarNode parent;
        public AStarNode(int y, int x) {
            this.y = y;
            this.x = x;
        }

        public void setgCost(int gCost) {
            this.gCost = gCost;
        }

        public void calcHCost(int targetY, int targetX) {
            hCost = 10*(Math.abs(x-targetX) + Math.abs(y-targetY));
        }

        public void calcFCost() {
            fCost = gCost + hCost;
        }


        public void setParent(AStarNode parent) {
            this.parent = parent;
        }
    }

    public AStarNode getBestNode(ArrayList<AStarNode> nodes) {
        AStarNode bestNode = nodes.get(0);
        for(int i = 0; i < nodes.size(); i++) {
            if(nodes.get(i).gCost < bestNode.gCost) {
                bestNode = nodes.get(i);
            }
        }
        return bestNode;
    }



}
