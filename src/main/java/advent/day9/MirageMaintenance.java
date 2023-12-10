package advent.day9;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MirageMaintenance {
    private static final Logger logger = Logger.getLogger(MirageMaintenance.class.getName());
    private static final String REPORT = "day9/sensor-report.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(MirageMaintenance.class, REPORT)) {
                int result = reader.lines()
                        .map(MirageMaintenance::lineToValueHistory)
                        .map(Solution1::extrapolateValueHistory)
                        .mapToInt(history -> history.get(history.size() - 1))
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static List<Integer> extrapolateValueHistory(List<Integer> history) {
            List<List<Integer>> extrapolationResults = new ArrayList<>();
            extrapolationResults.add(history);

            List<Integer> currentSequence = history;

            while (!currentSequence.stream().allMatch(value -> value == 0)) {
                List<Integer> nextSequence = new ArrayList<>();
                for (int i = 0; i < currentSequence.size() - 1; i++) {
                    int difference =  currentSequence.get(i + 1) - currentSequence.get(i);
                    nextSequence.add(difference);
                }
                if (!nextSequence.isEmpty()) {
                    extrapolationResults.add(nextSequence);
                }
                currentSequence = nextSequence;
            }

            for (int i = extrapolationResults.size() - 1; i > 0; i--) {
                List<Integer> sequence = extrapolationResults.get(i);
                List<Integer> upperSequence = extrapolationResults.get(i - 1);
                int difference = sequence.get(sequence.size() - 1) + upperSequence.get(upperSequence.size() - 1);
                upperSequence.add(difference);
            }
            return history;
        }
    }

    private static List<Integer> lineToValueHistory(String line) {
        return Arrays.stream(line.trim().split("\s+"))
                .map(value -> Integer.parseInt(value.trim()))
                .collect(Collectors.toList());
    }
}
