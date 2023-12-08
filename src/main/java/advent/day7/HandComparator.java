package advent.day7;

import java.util.Comparator;

public class HandComparator implements Comparator<Hand> {

    private final Comparator<Card> cardComparator;

    public HandComparator(Comparator<Card> cardComparator) {
        this.cardComparator = cardComparator;
    }

    @Override
    public int compare(Hand o1, Hand o2) {
        int handsTypeComparison = o1.getHandType().compareTo(o2.getHandType());
        if (handsTypeComparison != 0) {
            return handsTypeComparison;
        }
        for (int i = 0; i < o1.getCards().size(); i++) {
            int cardsComparison = cardComparator.compare(o1.getCards().get(i), o2.getCards().get(i));
            if (cardsComparison != 0) {
                return cardsComparison;
            }
        }
        return 0;
    }
}
