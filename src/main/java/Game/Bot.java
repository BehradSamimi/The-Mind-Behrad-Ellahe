package Game;

import javax.print.DocFlavor;
import java.util.ArrayList;

public class Bot extends Thread {
    public String name = "";
    public ArrayList<Integer> cards = new ArrayList<>();
    public Bot(String name) {
        this.name = name;
    }
}
