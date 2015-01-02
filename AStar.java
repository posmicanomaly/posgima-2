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
    public ArrayList<Vector2i> getPath(Vector2i start, Vector2i end, boolean build) {
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
            for(AStarNode node : getSurroundingNodes(current)) {
                if(hasSameNode(closed, node) != null) {
                    continue;
                }

                if(hasSameNode(open, node) == null) {
                    open.add(node);
                    node.setParent(current);
                    if(build) {
                        node.setgCost(getCostOfGlyph(dungeon.getMap()[node.y][node.x]));
                    } else {
                        node.setgCost(getCostOfGlyph(dungeon.getTileMap()[node.y][node.x]));
                    }
                    node.calcHCost(end.getY(), end.getX());
                    node.calcFCost();
                } else {
                    if(current.fCost < node.fCost) {
                        node.setParent(current);
                    }
                }
            }
        }
        if(open.isEmpty()) {
            System.out.println("no path");
        } else {
            ArrayList<Vector2i> path = new ArrayList<Vector2i>();
            AStarNode parent = current;
            while(parent != null) {
                path.add(new Vector2i(parent.y, parent.x));
                parent = parent.parent;
            }
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
