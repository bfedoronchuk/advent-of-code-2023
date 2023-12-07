package advent.day5;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FoodProductionProblem {
    private static final Logger logger = Logger.getLogger(FoodProductionProblem.class.getName());
    private static final String ALMANAC = "day5/almanac.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(FoodProductionProblem.class, ALMANAC)) {
                Almanac almanac = new AlmanacReader().readAlmanac(
                        reader.lines().collect(Collectors.toCollection(LinkedList::new))
                );
                long result = almanac.seeds().stream()
                        .mapToLong(seed -> seedToLocation(seed, almanac.transformationMaps()))
                        .min()
                        .orElse(-1);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static long seedToLocation(Long seed, LinkedList<List<Almanac.PropertyMappingRange>> transformationMaps) {
            long propertyValue = seed;

            for (List<Almanac.PropertyMappingRange> transformationMap: transformationMaps) {
                propertyValue = mapProperty(propertyValue, transformationMap);
            }

            return propertyValue;
        }

        private static long mapProperty(long propertyValue, List<Almanac.PropertyMappingRange> transformationMap) {
            Optional<Almanac.PropertyMappingRange> rangeCandidate = transformationMap.stream()
                    .filter(propertyMappingRange -> propertyValue >= propertyMappingRange.source() && propertyValue < propertyMappingRange.source() + propertyMappingRange.range())
                    .findFirst();
            return rangeCandidate.map(propertyMappingRange -> propertyValue - propertyMappingRange.source() + propertyMappingRange.destination())
                    .orElse(propertyValue);
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(FoodProductionProblem.class, ALMANAC)) {
                Almanac almanac = new AlmanacReader().readAlmanac(
                        reader.lines().collect(Collectors.toCollection(LinkedList::new))
                );
                LinkedList<PropertyRange> seedRanges = toSeedRanges(almanac.seeds());
                long result = lowestLocation(seedRanges, almanac.transformationMaps());
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static long lowestLocation(LinkedList<PropertyRange> seedRanges,
                                           LinkedList<List<Almanac.PropertyMappingRange>> transformationMaps) {
            LinkedList<PropertyRange> currentRanges = seedRanges;

            for (List<Almanac.PropertyMappingRange> transformationMap: transformationMaps) {
                currentRanges = transformRanges(currentRanges, transformationMap);
            }
            return currentRanges.stream()
                    .mapToLong(PropertyRange::start)
                    .min()
                    .orElse(-1);
        }

        private static LinkedList<PropertyRange> transformRanges(LinkedList<PropertyRange> ranges,
                                                                 List<Almanac.PropertyMappingRange> transformationMap) {
            LinkedList<PropertyRange> mappedRanges = new LinkedList<>();

            LinkedList<PropertyRange> rangesQueue = new LinkedList<>(ranges);
            while (!rangesQueue.isEmpty()) {
                PropertyRange current = rangesQueue.pollFirst();
                Optional<IntersectionResult> intersection = transformationMap.stream()
                        .map(mapping -> checkIntersection(current, mapping))
                        .filter(IntersectionResult::isIntersected)
                        .findFirst();
                if (intersection.isPresent()) {
                    IntersectionResult intersectionResult = intersection.get();
                    PropertyRange intersectedRange = intersectionResult.intersected();
                    long shift = intersectionResult.mapping().shift();
                    PropertyRange mappedRange = new PropertyRange(intersectedRange.start() + shift, intersectedRange.end() + shift);
                    mappedRanges.add(mappedRange);

                    rangesQueue.addAll(intersectionResult.rest());
                } else {
                    // no mappings available so pass as is
                    mappedRanges.add(current);
                }
            }
            return mappedRanges;
        }

        private static IntersectionResult checkIntersection(PropertyRange range, Almanac.PropertyMappingRange mapping) {
            if (range.start() >= mapping.sourceStart() && range.end() <= mapping.sourceEnd()) {
                // range is part of mapping
                return new IntersectionResult(range, Collections.emptyList(), mapping);
            }
            if (range.start() <= mapping.sourceEnd() && range.start() > mapping.sourceStart()) {
                // mapping overlaps range from left
                PropertyRange overlapping = new PropertyRange(range.start(), mapping.sourceEnd());
                PropertyRange nonOverlapping = new PropertyRange(mapping.sourceEnd() + 1, range.end());
                return new IntersectionResult(overlapping, List.of(nonOverlapping), mapping);
            }
            if (range.end() >= mapping.sourceStart() && range.end() < mapping.sourceEnd()) {
                // mapping overlaps range from right
                PropertyRange overlapping = new PropertyRange(mapping.sourceStart(), range.end());
                PropertyRange nonOverlapping = new PropertyRange(range.start(), mapping.sourceStart() - 1);
                return new IntersectionResult(overlapping, List.of(nonOverlapping), mapping);
            }
            if (range.start() < mapping.sourceStart() && range.end() > mapping.sourceEnd()) {
                // mapping range is inside of range
                PropertyRange overlapping = new PropertyRange(mapping.sourceStart(), mapping.sourceEnd());
                List<PropertyRange> rest = List.of(
                        new PropertyRange(range.start(), mapping.sourceStart() - 1),
                        new PropertyRange(mapping.sourceEnd() + 1, range.end())
                );
                return new IntersectionResult(overlapping, rest, mapping);
            }
            if (range.start() == mapping.sourceStart()) {
                // mapping range is left part of range
                PropertyRange overlapping = new PropertyRange(mapping.sourceStart(), mapping.sourceEnd());
                PropertyRange nonOverlapping = new PropertyRange(mapping.sourceEnd() + 1, range.end());
                return new IntersectionResult(overlapping, List.of(nonOverlapping), mapping);
            }
            if (range.end() == mapping.sourceEnd()) {
                // mapping range is right part of range
                PropertyRange overlapping = new PropertyRange(mapping.sourceStart(), mapping.sourceEnd());
                PropertyRange nonOverlapping = new PropertyRange(range.start(), mapping.sourceStart() - 1);
                return new IntersectionResult(overlapping, List.of(nonOverlapping), mapping);
            }
            return new IntersectionResult(null,  Collections.emptyList(), mapping);
        }

        private static LinkedList<PropertyRange> toSeedRanges(List<Long> seedsInput) {
            LinkedList<PropertyRange> seedRanges = new LinkedList<>();
            for (int i = 0; i < seedsInput.size(); i+=2) {
                seedRanges.add(new PropertyRange(
                        seedsInput.get(i),
                        seedsInput.get(i) + seedsInput.get(i + 1) - 1
                ));
            }
            return seedRanges;
        }

        public static record PropertyRange(long start, long end) {}

        public static record IntersectionResult(
                PropertyRange intersected,
                List<PropertyRange> rest,
                Almanac.PropertyMappingRange mapping) {

            public boolean isIntersected() {
                return intersected != null;
            }
        }
    }
}
