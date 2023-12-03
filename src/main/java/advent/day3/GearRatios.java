package advent.day3;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GearRatios {
    private static final Logger logger = Logger.getLogger(GearRatios.class.getName());
    private static final String ENGINE_SCHEMATIC = "day3/engine-schematic.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(GearRatios.class, ENGINE_SCHEMATIC)) {
                List<String> schematicLines = reader.lines().collect(Collectors.toList());
                char[][] schematic = buildSchematicMatrix(schematicLines);
                int result = calculateAdjacentNumbersSum(schematic);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int calculateAdjacentNumbersSum(char[][] schematic) {
            int sum = 0;

            for (int i = 0; i < schematic.length; i++) {
                for (int j = 0; j < schematic[i].length; j++) {
                    char character = schematic[i][j];
                    if (Character.isDigit(character)) {
                        StringBuilder numericValue = new StringBuilder();
                        boolean isAdjacent = false;
                        for (int sequenceIndex = j; sequenceIndex < schematic[i].length; sequenceIndex++) {
                            char digit = schematic[i][sequenceIndex];
                            if (!Character.isDigit(digit)) {
                                j = sequenceIndex;
                                break;
                            }
                            numericValue.append(schematic[i][sequenceIndex]);
                            isAdjacent = isAdjacent || isAdjacentToSymbol(i, sequenceIndex, schematic);
                        }
                        if (isAdjacent) {
                            sum += Integer.parseInt(numericValue.toString());
                        }
                    }
                }
            }

            return sum;
        }

        private static boolean isAdjacentToSymbol(int i, int j, char[][] schematic) {
            return List.of(
                    charAt(i-1, j, schematic),
                    charAt(i+1, j, schematic),
                    charAt(i, j-1, schematic),
                    charAt(i, j+1, schematic),
                    charAt(i-1, j-1, schematic),
                    charAt(i-1, j+1, schematic),
                    charAt(i+1, j-1, schematic),
                    charAt(i+1, j+1, schematic)
            )
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(Solution1::isSymbol);
        }

        private static boolean isSymbol(char character) {
            return character != ".".charAt(0) && !Character.isDigit(character);
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(GearRatios.class, ENGINE_SCHEMATIC)) {
                List<String> schematicLines = reader.lines().collect(Collectors.toList());
                char[][] schematic = buildSchematicMatrix(schematicLines);
                int result = gearRatioSum(schematic);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int gearRatioSum(char[][] schematic) {
            int sum = 0;
            for (int i = 0; i < schematic.length; i++) {
                for (int j = 0; j < schematic[i].length; j++) {
                    if (schematic[i][j] != "*".charAt(0)) {
                        continue;
                    }
                    List<Integer> adjacentNumbers = getAdjacentNumbers(i, j, schematic);
                    if (adjacentNumbers.size() != 2) {
                        continue;
                    }
                    sum += adjacentNumbers.get(0) * adjacentNumbers.get(1);
                }
            }
            return sum;
        }

        private static List<Integer> getAdjacentNumbers(int i, int j, char[][] schematic) {
            return List.of(
                    new Point(i-1, j),
                    new Point(i+1, j),
                    new Point(i, j-1),
                    new Point(i, j+1),
                    new Point(i-1, j-1),
                    new Point(i-1, j+1),
                    new Point(i+1, j-1),
                    new Point(i+1, j+1)
            ).stream()
                    .filter(point -> charAt(point.x(), point.y(), schematic)
                            .map(Character::isDigit)
                            .orElse(false))
                    .map(point -> exploreNumber(point, schematic))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }

        private static Optional<Integer> exploreNumber(Point point, char[][] schematic) {
            char charAtPoint = schematic[point.x()][point.y()];
            if (!Character.isDigit(charAtPoint)) {
                return Optional.empty();
            }
            StringBuilder leftPart = digitsBefore(point, schematic);
            StringBuilder rightPart = digitsAfter(point, schematic);
            String resultingNumber = leftPart.append(rightPart).toString();
            return Optional.of(Integer.parseInt(resultingNumber));
        }

        private static StringBuilder digitsBefore(Point point, char[][] schematic) {
            StringBuilder digits = new StringBuilder();
            LinkedList<Character> accumulator = new LinkedList<>();
            for (int j = point.y() - 1; j >= 0; j--) {
                char current = schematic[point.x()][j];
                if (Character.isDigit(current)) {
                    schematic[point.x()][j] = '.';
                    accumulator.addFirst(current);
                } else {
                    break;
                }
            }
            accumulator.forEach(digits::append);
            return digits;
        }

        private static StringBuilder digitsAfter(Point point, char[][] schematic) {
            StringBuilder digits = new StringBuilder();
            for (int j = point.y(); j < schematic[point.x()].length; j++) {
                char current = schematic[point.x()][j];
                if (Character.isDigit(current)) {
                    schematic[point.x()][j] = '.';
                    digits.append(current);
                } else {
                    break;
                }
            }
            return digits;
        }
    }

    private static Optional<Character> charAt(int i, int j, char[][] schematic) {
        if (i < 0 || i >= schematic.length) {
            return Optional.empty();
        }
        if (j < 0 || j >= schematic[i].length) {
            return Optional.empty();
        }
        return Optional.of(schematic[i][j]);
    }

    private static char[][] buildSchematicMatrix(List<String> schematicLines) {
        char[][] matrix =  new char[schematicLines.size()][schematicLines.get(0).length()];
        for (int i = 0; i < schematicLines.size(); i++) {
            String line = schematicLines.get(i);
            for (int j = 0; j < line.length(); j++) {
                matrix[i][j] = line.charAt(j);
            }
        }
        return matrix;
    }

    public static record Point(int x, int y) {}
}
