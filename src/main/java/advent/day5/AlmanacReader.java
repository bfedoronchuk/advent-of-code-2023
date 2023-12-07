package advent.day5;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AlmanacReader {

    public Almanac readAlmanac(List<String> inputFileLines) {
        Objects.requireNonNull(inputFileLines);

        List<Long> seeds = Arrays.stream(
                inputFileLines.get(0)
                        .replaceAll("seeds: ", "")
                        .trim()
                        .split(" "))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(LinkedList::new));

        LinkedList<List<Almanac.PropertyMappingRange>> transformationMaps = inputFileLines.stream()
                .skip(2)
                .collect(Collector.of(
                        LinkedList::new,
                        (collector, line) -> {
                            if (line.isBlank()) {
                                return;
                            }
                            if (line.contains("map")) {
                                collector.add(new ArrayList<>());
                                return;
                            }
                            collector.getLast().add(parseRange(line));

                        },
                        (collector1, collector2) -> {
                            collector1.addAll(collector2);
                            return collector1;
                        }
                ));
        return new Almanac(seeds, transformationMaps);
    }

    private Almanac.PropertyMappingRange parseRange(String line) {
        String[] splitTokens = line.trim().split(" ");
        return new Almanac.PropertyMappingRange(
                Long.parseLong(splitTokens[1]),
                Long.parseLong(splitTokens[0]),
                Long.parseLong(splitTokens[2])
        );
    }
}
