package advent.day2;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CubeConundrum {
    private static final Logger logger = Logger.getLogger(CubeConundrum.class.getName());
    private static final String KUBE_GAMES = "day2/kube-games.txt";

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(CubeConundrum.class, KUBE_GAMES)) {
                Integer result = reader.lines()
                        .map(CubeConundrum::createGame)
                        .filter(CubeConundrum.Solution1::isGamePossible)
                        .mapToInt(Game::id)
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static boolean isGamePossible(Game game) {
            final int maxRed = 12;
            final int maxGreen = 13;
            final int maxBlue = 14;

            return game.kubeSets().stream()
                    .filter(kubeSet -> kubeSet.red() > maxRed || kubeSet.green() > maxGreen || kubeSet.blue() > maxBlue)
                    .findFirst()
                    .isEmpty();
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(CubeConundrum.class, KUBE_GAMES)) {
                Integer result = reader.lines()
                        .map(CubeConundrum::createGame)
                        .mapToInt(CubeConundrum.Solution2::calculateGamePower)
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int calculateGamePower(Game game) {
            KubeSet firstKubeSet = game.kubeSets().get(0);
            int maxRed = firstKubeSet.red();
            int maxGreen = firstKubeSet.green();
            int maxBlue = firstKubeSet.blue();

            for (KubeSet kubeSet: game.kubeSets()) {
                if (kubeSet.red() > maxRed) {
                    maxRed = kubeSet.red();
                }
                if (kubeSet.green() > maxGreen) {
                    maxGreen = kubeSet.green();
                }
                if (kubeSet.blue() > maxBlue) {
                    maxBlue = kubeSet.blue();
                }
            }

            return maxRed * maxGreen * maxBlue;
        }
    }

    private static Game createGame(String textValues) {
        String[] gameTokens = textValues.split(":");
        String gameToken = gameTokens[0].trim();
        String kubeSetsToken = gameTokens[1].trim();

        Integer gameId = Integer.parseInt(gameToken.replace("Game ", ""));
        List<KubeSet> gameKubeSets = new LinkedList<>();
        String[] splitKubeSetsTokens = kubeSetsToken.split(";");
        for (String kubeSetToken: splitKubeSetsTokens) {
            String[] colourTokens = kubeSetToken.trim().split(",");
            Map<String, Integer> kubesCount = new HashMap<>(Map.of(
                    RED, 0,
                    GREEN, 0,
                    BLUE, 0
            ));
            for (String colourToken: colourTokens) {
                String[] splitColourTokens = colourToken.trim().split(" ");
                String kubeColour = splitColourTokens[1];
                int kubeAmounts = Integer.parseInt(splitColourTokens[0]);
                kubesCount.put(kubeColour, kubeAmounts);
            }
            KubeSet kubeSet = new KubeSet(
                    kubesCount.get(RED),
                    kubesCount.get(GREEN),
                    kubesCount.get(BLUE)
            );
            gameKubeSets.add(kubeSet);
        }
        return new Game(gameId, gameKubeSets);
    }

    public static record KubeSet(int red, int green, int blue) {}

    public static record Game(int id, List<KubeSet> kubeSets) {}
}
