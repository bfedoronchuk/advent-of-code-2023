package advent.day7;

import advent.ResourceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CamelCards {
    private static final Logger logger = Logger.getLogger(CamelCards.class.getName());
    private static final String HANDS = "day7/hands.txt";

    public static class Solution1 {
        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(CamelCards.class, HANDS)) {
                HandTypeProvider handTypeProvider = new RegularHandTypeProvider();
                List<Hand> sortedHands = reader.lines()
                        .map(line -> line.trim().split(" "))
                        .map(textValues -> {
                            String cards = textValues[0];
                            int bid = Integer.parseInt(textValues[1]);
                            return new Hand(cards, bid, handTypeProvider);
                        })
                        .sorted(new HandComparator(new CardComparator()))
                        .collect(Collectors.toList());
                int result = IntStream.range(0, sortedHands.size())
                        .map(i -> sortedHands.get(i).getBid() * (i + 1))
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        public static class CardComparator implements Comparator<Card> {
            @Override
            public int compare(Card o1, Card o2) {
                return Card.ORDER.indexOf(o1.getSymbol()) - Card.ORDER.indexOf(o2.getSymbol());
            }
        }

        public static class RegularHandTypeProvider implements HandTypeProvider {
            @Override
            public HandType determineHandType(List<Card> cards) {
                {
                    Map<Card, Long> cardCounts = cards.stream()
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    List<Long> counts = cardCounts.values().stream().sorted(Comparator.reverseOrder()).toList();
                    if (counts.get(0) == 5) {
                        return HandType.FIVE_OF_KIND;
                    }
                    if (counts.get(0) == 4) {
                        return HandType.FOUR_OF_KIND;
                    }
                    if (counts.get(0) == 3 && counts.get(1) == 2) {
                        return HandType.FULL_HOUSE;
                    }
                    if (counts.get(0) == 3) {
                        return HandType.THREE_OF_KIND;
                    }
                    if (counts.get(0) == 2 && counts.get(1) == 2) {
                        return HandType.TWO_PAIR;
                    }
                    if (counts.get(0) == 2) {
                        return HandType.ONE_PAIR;
                    }
                    return HandType.HIGH_CARD;
                }
            }
        }
    }

    public static class Solution2 {
        private static final char JOKER_CARD_SYMBOL = 'J';

        public static void main(String[] args) {
            try (BufferedReader reader = ResourceUtils.resourceReader(CamelCards.class, HANDS)) {
                HandTypeProvider handTypeProvider = new JokerHandTypeProvider();
                List<Hand> sortedHands = reader.lines()
                        .map(line -> line.trim().split(" "))
                        .map(textValues -> {
                            String cards = textValues[0];
                            int bid = Integer.parseInt(textValues[1]);
                            return new Hand(cards, bid, handTypeProvider);
                        })
                        .sorted(new HandComparator(new JokerCardComparator()))
                        .collect(Collectors.toList());
                int result = IntStream.range(0, sortedHands.size())
                        .map(i -> sortedHands.get(i).getBid() * (i + 1))
                        .sum();
                logger.log(Level.INFO, "Result = {0}", result);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to read input file", ex);
            }
        }

        public static class JokerCardComparator implements Comparator<Card> {
            @Override
            public int compare(Card o1, Card o2) {
                return orderValue(o1) - orderValue(o2);
            }

            private int orderValue(Card card) {
                return card.getSymbol() != JOKER_CARD_SYMBOL ? Card.ORDER.indexOf(card.getSymbol()) + 1 : 0;
            }
        }

        public static class JokerHandTypeProvider implements HandTypeProvider {
            @Override
            public HandType determineHandType(List<Card> cards) {
                int jokersCount = (int) cards.stream()
                        .filter(card -> card.getSymbol() == JOKER_CARD_SYMBOL)
                        .count();
                Map<Card, Long> nonJokerCardCounts = cards.stream()
                        .filter(card -> card.getSymbol() != JOKER_CARD_SYMBOL)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<Long> counts = nonJokerCardCounts.values().stream()
                        .sorted(Comparator.reverseOrder())
                        .toList();

                long mostFrequentCard = counts.size() != 0 ? counts.get(0): 0;
                long secondFrequentCard = counts.size() > 1 ? counts.get(1): 0;

                if (mostFrequentCard == 5 || mostFrequentCard + jokersCount >= 5) {
                    return HandType.FIVE_OF_KIND;
                }
                if (mostFrequentCard == 4 || mostFrequentCard + jokersCount >= 4) {
                    return HandType.FOUR_OF_KIND;
                }
                if ((mostFrequentCard == 3 && secondFrequentCard == 2)
                        || (mostFrequentCard <= 3 && secondFrequentCard <= 2
                        && mostFrequentCard + secondFrequentCard + jokersCount >= 5)) {
                    return HandType.FULL_HOUSE;
                }

                if (mostFrequentCard == 3 || mostFrequentCard + jokersCount >= 3) {
                    return HandType.THREE_OF_KIND;
                }
                if ((mostFrequentCard == 2 && secondFrequentCard == 2)
                        || (mostFrequentCard <= 2 && secondFrequentCard <= 2
                        && mostFrequentCard + secondFrequentCard + jokersCount >= 4)) {
                    return HandType.TWO_PAIR;
                }
                if (mostFrequentCard == 2 || mostFrequentCard + jokersCount >= 2) {
                    return HandType.ONE_PAIR;
                }
                return HandType.HIGH_CARD;
            }
        }
    }
}
