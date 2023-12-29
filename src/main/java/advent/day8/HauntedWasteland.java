package advent.day8;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HauntedWasteland {
    private static final Logger logger = Logger.getLogger(HauntedWasteland.class.getName());
    private static final String MAP_1 = "day8/map-1.txt";
    private static final String MAP_2 = "day8/map-2.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(HauntedWasteland.class, MAP_1)) {
                List<String> lines = reader.lines().toList();
                List<Direction> directions = readDirections(lines.get(0));
                Map<String, SimpleEntry<String, String>> navigations = readNavigations(lines);
                int result = countSteps(directions, navigations);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int countSteps(List<Direction> directions,
                                      Map<String, SimpleEntry<String, String>> navigations) {
            final String firstNode = "AAA";
            final String lastNode = "ZZZ";

            int steps = 0;
            DirectionsInfiniteIterator directionsIterator = new DirectionsInfiniteIterator(directions);
            String current = firstNode;

            while (!lastNode.equals(current)) {
                SimpleEntry<String, String> nextSteps = navigations.get(current);
                Direction direction = directionsIterator.next();
                current = direction == Direction.LEFT ? nextSteps.getKey() : nextSteps.getValue();
                steps++;
            }

            return steps;
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(HauntedWasteland.class, MAP_2)) {
                List<String> lines = reader.lines().toList();
                List<Direction> directions = readDirections(lines.get(0));
                Map<String, SimpleEntry<String, String>> navigations = readNavigations(lines);
                long result = countSteps(directions, navigations);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static long countSteps(List<Direction> directions,
                                      Map<String, SimpleEntry<String, String>> navigations) {
            List<String> startingNodes = navigations.keySet().stream()
                    .filter(node -> node.endsWith("A"))
                    .collect(Collectors.toList());

            List<Long> stepsPerNode = startingNodes.stream()
                    .map(node -> countStepsForNode(node, directions, navigations))
                    .collect(Collectors.toList());
            return leastCommonMultiple(stepsPerNode);
        }

        private static long leastCommonMultiple(List<Long> stepsPerNode) {
            long lsm = 1L;
            for (long steps: stepsPerNode) {
                lsm = leastCommonMultiple(lsm, steps);
            }
            return lsm;
        }

        private static long leastCommonMultiple(long a, long b) {
            return a * b / greatestCommonDivisor(a, b);
        }

        private static long greatestCommonDivisor(long a, long b) {
            if (b == 0) {
                return a;
            }
            return greatestCommonDivisor(b, a % b);
        }

        private static long countStepsForNode(String node,
                                                 List<Direction> directions,
                                                 Map<String, SimpleEntry<String, String>> navigations) {
            long steps = 0L;
            DirectionsInfiniteIterator directionsIterator = new DirectionsInfiniteIterator(directions);
            String currentNode = node;
            while (!currentNode.endsWith("Z")) {
                Direction direction = directionsIterator.next();
                SimpleEntry<String, String> nextSteps = navigations.get(currentNode);
                currentNode = direction == Direction.LEFT ? nextSteps.getKey() : nextSteps.getValue();
                steps++;
            }
            return steps;
        }
    }

    private static HashMap<String, SimpleEntry<String, String>> readNavigations(List<String> lines) {
        return lines.stream()
                .skip(2)
                .collect(Collector.of(
                        HashMap::new,
                        (map, line) -> {
                            String[] tokens = line.split(" = ");
                            String node = tokens[0];
                            String[] leftAndRightTokens = tokens[1]
                                    .replaceAll("[)(]", "")
                                    .split(", ");
                            String left = leftAndRightTokens[0];
                            String right = leftAndRightTokens[1];
                            map.put(node, new SimpleEntry<>(left, right));
                        },
                        (map1, map2) -> {
                            map1.putAll(map2);
                            return map1;
                        }
                ));
    }

    private static List<Direction> readDirections(String directionsLine) {
        Objects.requireNonNull(directionsLine);
        return Arrays.stream(directionsLine.split(""))
                .map(token -> {
                    if ("L".equals(token)) {
                        return Direction.LEFT;
                    } else {
                        return Direction.RIGHT;
                    }
                }).collect(Collectors.toList());
    }

    public enum Direction {
        LEFT,
        RIGHT
    }

    public static class DirectionsInfiniteIterator implements Iterator<Direction> {

        private final List<Direction> directions;
        private Iterator<Direction> iterator;

        public DirectionsInfiniteIterator(List<Direction> directions) {
            this.directions = directions;
            this.iterator = directions.iterator();
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Direction next() {
            if (!this.iterator.hasNext()) {
                this.iterator = directions.iterator();
            }
            return iterator.next();
        }
    }
}
