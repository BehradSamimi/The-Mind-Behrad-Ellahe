package Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameServer extends Thread {
    public String name = "";
    public ArrayList<Player> players = new ArrayList<>();
    public int maxPlayer = 1;
    @Override
    public void run() {
        Scanner startGameScanner = null;
        try {
            startGameScanner = new Scanner(players.get(0).socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            String massage = startGameScanner.nextLine();
            if (isEqual(players.get(0), massage, "START")) {
                break;
            }
        }
        System.out.println(players.get(0).name + " won the game");
    }
    public Boolean isEqual(Player player, String massage, String text) {
        if (massage.length() < 10 || !massage.substring(0, 10).equalsIgnoreCase(player.autoToken)) {
            return false;
        }
        if (massage.substring(10).equals(text)) {
            return true;
        }
        return false;
    }
}
