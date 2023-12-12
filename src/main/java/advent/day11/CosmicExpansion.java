package advent.day11;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CosmicExpansion {
    private static final Logger logger = Logger.getLogger(CosmicExpansion.class.getName());
    private static final String IMAGE = "day11/space-image.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(CosmicExpansion.class, IMAGE)) {
                char[][] spaceImage = parseImage(reader.lines().collect(Collectors.toList()));
                Set<Galaxy> galaxies = collectGalaxies(spaceImage);
                int result = sumAllPaths(galaxies);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int sumAllPaths(Set<Galaxy> galaxies) {
            int sum = 0;
            Set<GalaxyPair> visitedPairs = new HashSet<>();
            EmptySpace emptySpace = new EmptySpace(galaxies);

            for (Galaxy galaxy1: galaxies) {
                for (Galaxy galaxy2: galaxies) {
                    GalaxyPair pair = new GalaxyPair(galaxy1, galaxy2);
                    if (visitedPairs.contains(pair) || galaxy1 == galaxy2) {
                        continue;
                    }
                    sum += path(pair, emptySpace);
                    visitedPairs.add(pair);
                }
            }
            return sum;
        }

        public static int path(GalaxyPair galaxyPair, EmptySpace emptySpace) {
            Galaxy galaxy1 = galaxyPair.galaxy1();
            Galaxy galaxy2 = galaxyPair.galaxy2();
            return Math.abs(galaxy1.x - galaxy2.x) + Math.abs(galaxy1.y - galaxy2.y) + emptySpace.spaceBetween(galaxy1, galaxy2);
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(CosmicExpansion.class, IMAGE)) {
                char[][] spaceImage = parseImage(reader.lines().collect(Collectors.toList()));
                Set<Galaxy> galaxies = collectGalaxies(spaceImage);
                BigInteger result = sumAllPaths(galaxies);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static BigInteger sumAllPaths(Set<Galaxy> galaxies) {
            BigInteger sum = BigInteger.ZERO;
            Set<GalaxyPair> visitedPairs = new HashSet<>();
            EmptySpace emptySpace = new EmptySpace(galaxies);

            for (Galaxy galaxy1: galaxies) {
                for (Galaxy galaxy2: galaxies) {
                    GalaxyPair pair = new GalaxyPair(galaxy1, galaxy2);
                    if (visitedPairs.contains(pair) || galaxy1 == galaxy2) {
                        continue;
                    }
                    sum = sum.add(path(pair, emptySpace));
                    visitedPairs.add(pair);
                }
            }
            return sum;
        }

        public static BigInteger path(GalaxyPair galaxyPair, EmptySpace emptySpace) {
            Galaxy galaxy1 = galaxyPair.galaxy1();
            Galaxy galaxy2 = galaxyPair.galaxy2();
            int horizontalPath = Math.abs(galaxy1.x - galaxy2.x);
            int verticalPath = Math.abs(galaxy1.y - galaxy2.y);
            BigInteger pathWithoutExpansion = BigInteger.valueOf(horizontalPath + verticalPath);
            BigInteger expansionDelta = BigInteger.valueOf(emptySpace.spaceBetween(galaxy1, galaxy2))
                    .multiply(BigInteger.valueOf(1_000_000 - 1));
            return pathWithoutExpansion.add(expansionDelta);
        }
    }

    public static record Galaxy(int x, int y) implements Comparable<Galaxy> {
        @Override
        public int compareTo(Galaxy galaxy) {
            if (this.x != galaxy.x) {
                return this.x - galaxy.x;
            }
            return this.y - galaxy.y;
        }
    }

    private static Set<Galaxy> collectGalaxies(char[][] spaceImage) {
        Set<Galaxy> galaxies = new HashSet<>();
        for (int i = 0; i < spaceImage.length; i++) {
            for (int j = 0; j < spaceImage[i].length; j++) {
                if (spaceImage[i][j] == '#') {
                    galaxies.add(new Galaxy(i, j));
                }
            }
        }
        return galaxies;
    }

    private static char[][] parseImage(List<String> lines) {
        char[][] image = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                image[i][j] = line.charAt(j);
            }
        }
        return image;
    }

    public static record GalaxyPair(Galaxy galaxy1, Galaxy galaxy2) {
        public GalaxyPair(Galaxy galaxy1, Galaxy galaxy2) {
            if (galaxy1.compareTo(galaxy2) >= 0) {
                this.galaxy1 = galaxy1;
                this.galaxy2 = galaxy2;
            } else {
                this.galaxy1 = galaxy2;
                this.galaxy2 = galaxy1;
            }
        }
    }

    public static class EmptySpace {
        private final SortedSet<Integer> emptyRows = new TreeSet<>();
        private final SortedSet<Integer> emptyColumns = new TreeSet<>();

        public EmptySpace(Set<Galaxy> galaxies) {
            List<Galaxy> sortedByX = galaxies.stream().sorted(Comparator.comparingInt(g -> g.x)).toList();
            List<Galaxy> sortedByY = galaxies.stream().sorted(Comparator.comparingInt(g -> g.y)).toList();

            for (int i = 0; i < galaxies.size() - 1; i++) {
                emptyRows.addAll(between(sortedByX.get(i).x(), sortedByX.get(i + 1).x()));
                emptyColumns.addAll(between(sortedByY.get(i).y(), sortedByY.get(i + 1).y()));
            }
        }

        private Collection<Integer> between(int a, int b) {
            return IntStream.range(a + 1, b).boxed().collect(Collectors.toList());
        }

        public int spaceBetween(Galaxy g1, Galaxy g2) {
            int minX = Math.min(g1.x(), g2.x());
            int maxX = Math.max(g1.x(), g2.x());
            int minY = Math.min(g1.y(), g2.y());
            int maxY = Math.max(g1.y(), g2.y());
            long emptyRows = this.emptyRows.stream().filter(row -> row > minX && row < maxX).count();
            long emptyColumns = this.emptyColumns.stream().filter(column -> column > minY && column < maxY).count();
            return (int) (emptyRows + emptyColumns);
        }
    }
}
