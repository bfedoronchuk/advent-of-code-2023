package advent.day6;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WaitForIt {
    private static final Logger logger = Logger.getLogger(WaitForIt.class.getName());
    private static final String RACE_DISTANCES = "day6/race-distances.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(WaitForIt.class, RACE_DISTANCES)) {
                List<RaceDistance> raceDistances = parseDistances(reader.lines().collect(Collectors.toList()));
                long result = raceDistances.stream()
                        .mapToLong(RaceDistance::winOptions)
                        .reduce(1L, (a, b) -> a * b);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static List<RaceDistance> parseDistances(List<String> lines) {
            List<Integer> time = Arrays.stream(lines.get(0)
                    .replace("Time:", "")
                    .trim()
                    .split("\s+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            List<Integer> distances = Arrays.stream(lines.get(1)
                    .replace("Distance:", "")
                    .trim()
                    .split("\s+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return IntStream.range(0, time.size())
                    .mapToObj(i -> new RaceDistance(time.get(i), distances.get(i)))
                    .collect(Collectors.toList());
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(WaitForIt.class, RACE_DISTANCES)) {
                RaceDistance raceDistance = parseDistance(reader.lines().collect(Collectors.toList()));
                long result = raceDistance.winOptions();
                logger.log(Level.INFO, "Result = {0}", result);
            }  catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static RaceDistance parseDistance(List<String> lines) {
            long time = Long.parseLong(
                    lines.get(0)
                            .replace("Time:", "")
                            .trim()
                            .replaceAll("\s*", ""));
            long distance = Long.parseLong(
                    lines.get(1)
                            .replace("Distance:", "")
                            .trim()
                            .replaceAll("\s*", ""));
            return new RaceDistance(time, distance);
        }
    }

    public static record RaceDistance(long time, long distance) {
        public long winOptions() {
            long winOptions = 0;
            for (long hold = 0; hold <= time; hold++) {
                long timeLeft = time - hold;
                long dist = hold * timeLeft;
                if (dist > distance) {
                    winOptions++;
                }
            }
            return winOptions;
        }
    }
}
