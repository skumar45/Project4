import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy {


    private static class Node  {
        Point point;
        int g;
        int h;
        Node parent;

        Node(Point point, int g, int h, Node parent) {
            this.point = point;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        int getF() {
            return g+h;
        }

    }

    //start and ending points of the path
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {

        // initializing open list, open map and closed map
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        Map<Point, Node> openMap = new HashMap<>();
        Map<Point, Node> closedMap = new HashMap<>();

        //set start node and add it to open list and open map
        Node startNode = new Node(start, 0, heuristicDistance(start, end), null);
        openList.add(startNode);
        openMap.put(start, startNode);

        //a* search loop
        while (!openList.isEmpty()) {
            // current node is node with lowest f score
            Node currNode = openList.poll();
            openMap.remove(currNode.point);


            // if goal is reached, return reconstructed path
            if (withinReach.test(currNode.point, end)) {
                return Path(currNode);
            }

            // analyze adjacent nodes that are not on closed list
            potentialNeighbors.apply(currNode.point)
                    .filter(canPassThrough)
                    .forEach(neighbor -> {

                        // distance from start node
                        int g = currNode.g + 1;
                        // distance of adjacent node to end point
                        int h = heuristicDistance(neighbor, end);
                        int f = g + h;

                        //save prior node of this neighbor
                        Node neighborNode = openMap.get(neighbor);

                        if (neighborNode == null ) {
                            neighborNode = new Node(neighbor, g, h, currNode);
                            openList.add(neighborNode);
                            openMap.put(neighbor, neighborNode);
                        } else if (g < neighborNode.g) {
                            //if g value is better than previous, replace old node with new one
                            neighborNode.g = g;
                            neighborNode.parent = currNode;

                            openList.remove(neighborNode);
                            openList.add(neighborNode);
                        }
                    });
            // move current node to closed list
            closedMap.put(currNode.point, currNode);
        }
        // return empty if no path found
        return new LinkedList<>();

    }
    private List<Point> Path(Node node) {
        LinkedList<Point> path = new LinkedList<>();
        Node current = node;

        while (current.parent != null) {
            path.addFirst(current.point);
            current = current.parent;
        }

        return path;
    }

    private int heuristicDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }


}