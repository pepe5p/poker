package com.pkaras;

import com.pkaras.enums.Rank;
import com.pkaras.enums.Suite;

import java.util.Objects;

public record Card(Rank rank, Suite suite) implements Comparable<Card> {

    @Override
    public String toString() {
        return toString("\u001B[33m");
    }

    public String toString(String actualColor) {
        return String.format(
            "[%s%s%s%s]",
            suite.color,
            rank.rankName,
            suite.suiteName,
            actualColor
        );
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
