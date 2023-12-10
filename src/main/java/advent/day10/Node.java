package advent.day10;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public enum Node {
    VERTICAL('|'),
    HORIZONTAL('-'),
    NORTH_TO_EAST('L'),
    NORTH_TO_WEST('J'),
    SOUTH_TO_EAST('F'),
    SOUTH_TO_WEST('7'),
    GROUND('.'),
    START('S');

    String GROUND_EXCEPTION_MSG = "Ground could not have any connections";
    String UNKNOWN_NODE_EXCEPTION_MSG = "Unknown node";

    private final char symbol;

    Node(char symbol) {
        this.symbol = symbol;
    }

    public static Node from(char character) {
        return Arrays.stream(Node.values())
                .filter(node -> node.getSymbol() == character)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown character"));
    }

    public Set<Node> possibleTopDirections() {
        switch (this) {
            case VERTICAL, NORTH_TO_EAST, NORTH_TO_WEST, START -> {
                return Set.of(VERTICAL, SOUTH_TO_EAST, SOUTH_TO_WEST, START);
            }
            case HORIZONTAL, SOUTH_TO_EAST, SOUTH_TO_WEST -> {
                return Collections.emptySet();
            }
            case GROUND -> throw new IllegalStateException(GROUND_EXCEPTION_MSG);
            default -> throw new IllegalStateException(UNKNOWN_NODE_EXCEPTION_MSG);
        }
    }

    public Set<Node> possibleBottomDirections() {
        switch (this) {
            case VERTICAL, SOUTH_TO_EAST, SOUTH_TO_WEST, START -> {
                return Set.of(VERTICAL, NORTH_TO_EAST, NORTH_TO_WEST, START);
            }
            case HORIZONTAL, NORTH_TO_EAST, NORTH_TO_WEST -> {
                return Collections.emptySet();
            }
            case GROUND -> throw new IllegalStateException(GROUND_EXCEPTION_MSG);
            default -> throw new IllegalStateException(UNKNOWN_NODE_EXCEPTION_MSG);
        }
    }

    public Set<Node> possibleLeftDirections() {
        switch (this) {
            case HORIZONTAL, SOUTH_TO_WEST, NORTH_TO_WEST, START -> {
                return Set.of(HORIZONTAL, SOUTH_TO_EAST, NORTH_TO_EAST, START);
            }
            case VERTICAL, SOUTH_TO_EAST, NORTH_TO_EAST -> {
                return Collections.emptySet();
            }
            case GROUND -> throw new IllegalStateException(GROUND_EXCEPTION_MSG);
            default -> throw new IllegalStateException(UNKNOWN_NODE_EXCEPTION_MSG);
        }
    }

    public Set<Node> possibleRightDirections() {
        switch (this) {
            case HORIZONTAL, SOUTH_TO_EAST, NORTH_TO_EAST, START -> {
                return Set.of(HORIZONTAL, SOUTH_TO_WEST, NORTH_TO_WEST, START);
            }
            case VERTICAL, SOUTH_TO_WEST, NORTH_TO_WEST -> {
                return Collections.emptySet();
            }
            case GROUND -> throw new IllegalStateException(GROUND_EXCEPTION_MSG);
            default -> throw new IllegalStateException(UNKNOWN_NODE_EXCEPTION_MSG);
        }
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
