package advent.day7;

import java.util.List;
import java.util.Objects;

public class Card {
    public static final List<Character> ORDER =
            List.of('2' ,'3' ,'4' ,'5' ,'6' ,'7' ,'8' ,'9' ,'T' ,'J' ,'Q' ,'K' ,'A');

    private final Character symbol;

    public Card(Character symbol) {
        this.symbol = symbol;
    }

    public Character getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(symbol, card.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
