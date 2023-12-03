package advent.day1;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Trebuchet {
    private static final Logger logger = Logger.getLogger(Trebuchet.class.getName());
    private static final String CALIBRATION_VALUES = "day1/calibration-values.txt";


    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(Trebuchet.class, CALIBRATION_VALUES)) {
                Integer result = reader.lines()
                        .mapToInt(Solution1::calibrationValue)
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int calibrationValue(String value) {
            List<Integer> digits = value.chars()
                    .filter(Character::isDigit)
                    .mapToObj(Character::toString)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            String resultValue = "%d%d".formatted(digits.get(0), digits.get(digits.size() - 1));
            return Integer.parseInt(resultValue);
        }
    }

    public static class Solution2 {

        private static final Map<String, Integer> DIGIT_TEXT_VALUES = Map.of(
                "one", 1,
                "two", 2,
                "three", 3,
                "four", 4,
                "five", 5,
                "six", 6,
                "seven", 7,
                "eight", 8,
                "nine", 9
        );

        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(Trebuchet.class, CALIBRATION_VALUES)) {
                Integer result = reader.lines()
                        .mapToInt(Solution2::calibrationValue)
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int calibrationValue(String value) {
            Integer firstDigit = null;
            Integer lastDigit = null;
            for (int i = 0; i < value.length(); i++) {
                Optional<Integer> foundInt = integerValue(value, i);
                if (foundInt.isPresent()) {
                    firstDigit = foundInt.get();
                    break;
                }
            }
            for (int i = value.length() - 1; i >= 0; i--) {
                Optional<Integer> foundInt = integerValue(value, i);
                if (foundInt.isPresent()) {
                    lastDigit = foundInt.get();
                    break;
                }
            }
            Objects.requireNonNull(firstDigit);
            Objects.requireNonNull(lastDigit);
            return Integer.parseInt("%d%d".formatted(firstDigit, lastDigit));
        }

        private static Optional<Integer> integerValue(String value, int offset) {
            char character = value.charAt(offset);
            if (Character.isDigit(character)) {
                return Optional.of(Integer.parseInt(Character.toString(character)));
            }
            return DIGIT_TEXT_VALUES.entrySet()
                    .stream()
                    .filter(entry -> value.startsWith(entry.getKey(), offset))
                    .map(Map.Entry::getValue)
                    .findFirst();
        }
    }
}
