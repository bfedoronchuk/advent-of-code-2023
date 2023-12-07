package advent.day5;

import java.util.LinkedList;
import java.util.List;

public record Almanac(
        List<Long> seeds,
        LinkedList<List<PropertyMappingRange>> transformationMaps) {

    public static record PropertyMappingRange(long source, long destination, long range) {
        public long sourceStart() {
            return source;
        }
        public long sourceEnd() {
            return source + range - 1;
        }
        public long shift() {
           return destination - source;
        }
    }
}
