package advent.day7;

import java.util.List;

public interface HandTypeProvider {
    HandType determineHandType(List<Card> cards);
}
