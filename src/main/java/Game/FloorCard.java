package Game;

import java.util.Comparator;

public class FloorCard {
    public String name = "";
    public int cardNumber = 0;
    public FloorCard(String name, int cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }
}

class SortFloorCard implements Comparator<FloorCard> {

    @Override
    public int compare(FloorCard o1, FloorCard o2) {
        return o1.cardNumber - o2.cardNumber;
    }
}