package advent.day7;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Hand {
    private final List<Card> cards;
    private final HandType handType;
    private final Integer bid;

    public Hand(String hand, Integer bid, HandTypeProvider provider) {
        this.cards = IntStream.range(0, hand.length())
                .mapToObj(hand::charAt)
                .map(Card::new)
                .collect(Collectors.toList());
        this.handType = provider.determineHandType(this.cards);
        this.bid = bid;
    }

    public List<Card> getCards() {
        return cards;
    }

    public HandType getHandType() {
        return handType;
    }

    public Integer getBid() {
        return bid;
    }

}
