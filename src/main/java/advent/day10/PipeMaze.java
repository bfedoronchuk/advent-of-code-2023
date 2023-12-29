package advent.day10;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PipeMaze {
    private static final Logger logger = Logger.getLogger(PipeMaze.class.getName());
    private static final String BLUEPRINT = "day10/pipes-blueprint.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(PipeMaze.class, BLUEPRINT)) {
                Node[][] blueprint = parseBlueprint(reader.lines().collect(Collectors.toList()));
                NodePoint startPoint = findStart(blueprint);

                int path = cyclePath(startPoint, blueprint);
                int result = (int) Math.ceil(path * 1.0 / 2);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static Node[][] parseBlueprint(List<String> lines) {
            Node[][] blueprint = new Node[lines.size()][lines.get(0).length()];
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                for (int j = 0; j < line.length(); j++) {
                    blueprint[i][j] = Node.from(line.charAt(j));
                }
            }
            return blueprint;
        }

        private static NodePoint findStart(Node[][] blueprint) {
            for (int i = 0; i < blueprint.length; i++) {
                for (int j = 0; j < blueprint[i].length; j++) {
                    if (blueprint[i][j] == Node.START) {
                        return new NodePoint(new Point(i, j), Node.START);
                    }
                }
            }
            throw new IllegalStateException("There is no starting node");
        }

        private static List<NodePoint> getNeighbours(NodePoint nodePoint,  Node[][] blueprint) {
            Point point = nodePoint.point();
            Node node = nodePoint.node();

            Optional<NodePoint> top = nodeAt(point.top(), blueprint);
            Optional<NodePoint> bottom = nodeAt(point.bottom(), blueprint);
            Optional<NodePoint> left = nodeAt(point.left(), blueprint);
            Optional<NodePoint> right = nodeAt(point.right(), blueprint);

            List<NodePoint> neighbors = new ArrayList<>();
            if (top.isPresent() && node.possibleTopDirections().contains(top.get().node())) {
                neighbors.add(top.get());
            }
            if (bottom.isPresent() && node.possibleBottomDirections().contains(bottom.get().node())) {
                neighbors.add(bottom.get());
            }
            if (left.isPresent() && node.possibleLeftDirections().contains(left.get().node())) {
                neighbors.add(left.get());
            }
            if (right.isPresent() && node.possibleRightDirections().contains(right.get().node())) {
                neighbors.add(right.get());
            }
            return neighbors;
        }

        private static Optional<NodePoint> nodeAt(Point point, Node[][] blueprint) {
            if (!isInbound(point, blueprint)) {
                return Optional.empty();
            }
            Node node = blueprint[point.x()][point.y()];
            return Optional.of(new NodePoint(point, node));
        }

        private static boolean isInbound(Point point, Node[][] blueprint) {
            if (point.x() < 0 || point.x() >= blueprint.length) {
                return false;
            }
            if (point.y() < 0 || point.y() >= blueprint[point.x()].length) {
                return false;
            }
            return true;
        }

        private static int cyclePath(NodePoint startingPoint, Node[][] blueprint) {
            NodePoint lastVisited = startingPoint;
            NodePoint current = getNeighbours(startingPoint, blueprint)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Starting point should have at least 2 ingoing pipes forming a cycle"));
            int path = 1;

            while (!current.equals(startingPoint)) {
                path++;
                Optional<NodePoint> nextPipe = Optional.empty();
                for (NodePoint neighborPipe : getNeighbours(current, blueprint)) {
                    if (!neighborPipe.equals(lastVisited)) {
                        nextPipe = Optional.of(neighborPipe);
                        break;
                    }
                }
                if (nextPipe.isEmpty()) {
                    throw new IllegalStateException("No path further at point " + current);
                }
                lastVisited = current;
                current = nextPipe.get();
            }

            return path;
        }
    }

    public static record Point(int x, int y) {
        public Point top() {
            return new Point(x - 1, y);
        }
        public Point bottom() {
            return new Point(x + 1, y);
        }
        public Point left() {
            return new Point(x, y - 1);
        }
        public Point right() {
            return new Point(x, y + 1);
        }
    }

    public static record NodePoint(Point point, Node node) {}
}
