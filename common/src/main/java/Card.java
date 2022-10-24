import enums.Rank;
import enums.Suite;

import java.util.Objects;

public record Card(Rank rank, Suite suite) implements Comparable<Card> {
    public String toString() {
        return String.format("[%s%s]", rank.name, suite.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank.value == card.rank.value;
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.rank.value, o.rank.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank);
    }
}
