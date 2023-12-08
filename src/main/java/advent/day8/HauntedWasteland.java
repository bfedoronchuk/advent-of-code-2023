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
    private static final String MAP = "day8/map.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(HauntedWasteland.class, MAP)) {
                List<String> lines = reader.lines().toList();
                List<Direction> directions = readDirections(lines.get(0));
                Map<String, SimpleEntry<String, String>> navigations = lines.stream()
                        .skip(2)
                        .collect(Collector.of(
                                HashMap::new,
                                Solution1::addNavigation,
                                (map1, map2) -> {
                                    map1.putAll(map2);
                                    return map1;
                                }
                        ));
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

        private static void addNavigation(HashMap<String, SimpleEntry<String, String>> map, String line) {
            Objects.requireNonNull(map);
            Objects.requireNonNull(line);
            String[] tokens = line.split(" = ");
            String node = tokens[0];
            String[] leftAndRightTokens = tokens[1]
                    .replaceAll("[)(]", "")
                    .split(", ");
            String left = leftAndRightTokens[0];
            String right = leftAndRightTokens[1];
            map.put(node, new SimpleEntry<>(left, right));
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
