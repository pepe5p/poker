import enums.HandRanking;
import enums.Rank;

import java.util.ArrayList;
import java.util.Objects;

public record HandValue(HandRanking handRanking, ArrayList<Rank> rankList) implements Comparable <HandValue> {

    public boolean isGreaterThan(HandValue handValue) {
        return this.compareTo(handValue) > 0;
    }

    public boolean isLesserThan(HandValue handValue) {
        return this.compareTo(handValue) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandValue handValue = (HandValue) o;
        return this.compareTo(handValue) == 0;
    }

    @Override
    public int compareTo(HandValue handValue) {
        if (this.handRanking.value > handValue.handRanking.value) return 1;
        if (this.handRanking.value < handValue.handRanking.value) return -1;
        for (int i = 0; i < this.rankList.size(); i++) {
            if (this.rankList.get(i).value > handValue.rankList.get(i).value) return 1;
            if (this.rankList.get(i).value < handValue.rankList.get(i).value) return -1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.handRanking.value);  // TODO: hash by rankList
    }
}
