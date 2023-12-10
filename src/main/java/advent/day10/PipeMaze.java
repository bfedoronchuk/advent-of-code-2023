package advent.day10;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                int maxPath = getNeighbours(startPoint, blueprint).stream()
                        .findFirst() // we expect that there are only two outgoing nodes from the start node forming a cycle
                        .map(neighbour -> maxPath(neighbour, startPoint, blueprint, new HashSet<>(Set.of(startPoint))))
                        .orElseThrow(() -> new IllegalStateException("There is no cycle"));
                int result = (int) Math.ceil(maxPath * 1.0 / 2);
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

        private static int maxPath(NodePoint start, NodePoint end, Node[][] blueprint, Set<NodePoint> visited) {
            visited.add(start);

            List<NodePoint> neighbours = getNeighbours(start, blueprint);
            boolean beginningOfTraversal = visited.size() == 2;
            if (neighbours.contains(end) && !beginningOfTraversal) {
                return 1;
            }
            List<NodePoint> unvisitedNeighbours =  getNeighbours(start, blueprint).stream()
                    .filter(neighbour -> !visited.contains(neighbour))
                    .collect(Collectors.toList());

            boolean deadEnd = unvisitedNeighbours.isEmpty();
            if (deadEnd) {
                return -1;
            }

            int maxPath = -1;
            for (NodePoint neighbour: unvisitedNeighbours) {
                int maxNeighbourPath = maxPath(neighbour, end, blueprint, visited);
                maxPath = Math.max(maxPath, maxNeighbourPath);
            }

            if (maxPath != -1) {
                maxPath++;
            }
            return maxPath;
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
