package advent.day4;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Scratchcards {
    private static final Logger logger = Logger.getLogger(Scratchcards.class.getName());
    private static final String CARDS = "day4/cards.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(Scratchcards.class, CARDS)) {
                int result = reader.lines()
                        .map(Scratchcards::toCard)
                        .mapToInt(Solution1::score)
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int score(Card card) {
            long matches = card.countMatches();
            if (matches == 0) {
                return 0;
            }
            return (int) Math.pow(2, matches - 1);
        }
    }

    public static class Solution2 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(Scratchcards.class, CARDS)) {
                LinkedList<Card> allCards = reader.lines()
                        .map(Scratchcards::toCard)
                        .collect(Collectors.toCollection(LinkedList::new));
                int result = countWinningScratchcards(allCards);
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        private static int countWinningScratchcards(LinkedList<Card> cards) {
            Map<Integer, Card> cardById = cards.stream().collect(Collectors.toMap(Card::id, Function.identity()));
            int winningCards = 0;

            Map<Integer, Integer> winningCardsMemo = new HashMap<>();
            for (Card card: cards) {
                winningCards += countWinningScratchcards(card, cardById, winningCardsMemo);
            }

            return winningCards;
        }

        private static int countWinningScratchcards(Card card,
                                                    Map<Integer, Card> cardById,
                                                    Map<Integer, Integer> winningCardsMemo) {
            if (winningCardsMemo.containsKey(card.id())) {
                return winningCardsMemo.get(card.id());
            }

            int winningCards = 1;
            winningCards += IntStream.rangeClosed(card.id() + 1, card.id() + (int) card.countMatches())
                    .mapToObj(cardById::get)
                    .mapToInt(nextCard -> countWinningScratchcards(nextCard, cardById, winningCardsMemo))
                    .sum();

            winningCardsMemo.put(card.id(), winningCards);
            return winningCards;
        }
    }

    private static Card toCard(String line) {
        Objects.requireNonNull(line);
        String[] lineTokens = line.split(":");
        String cardToken = lineTokens[0].trim();
        String numbersToken = lineTokens[1].trim();

        Integer id = Integer.parseInt(cardToken.replaceAll("Card\s*", ""));

        String[] splitNumbersToken = numbersToken.split("\\|");
        String winningNumbersToken = splitNumbersToken[0].trim();
        String actualNumbersToken = splitNumbersToken[1].trim();

        Set<Integer> winningNumbers = Arrays.stream(winningNumbersToken.split("\s"))
                .filter(value -> !value.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        List<Integer> actualNumbers = Arrays.stream(actualNumbersToken.split("\s"))
                .filter(value -> !value.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return new Card(id, winningNumbers, actualNumbers);
    }

    public static record Card (Integer id,
                               Set<Integer> winningNumbers,
                               List<Integer> numbers) {
        public long countMatches() {
            return this.numbers.stream().filter(this.winningNumbers::contains).count();
        }
    }
}
