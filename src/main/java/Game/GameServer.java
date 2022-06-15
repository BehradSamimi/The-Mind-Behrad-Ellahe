package Game;

import com.sun.source.tree.SynchronizedTree;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GameServer extends Thread {
    public Boolean isGameOn = true;
    public String name = "";
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Bot> bots = new ArrayList<>();
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
        System.out.println("GAME STARTED");
    //    startGameScanner.close();
        int x = 4 - players.size();
        for (int i = 0; i < x; i++) {
            bots.add(new Bot("bot" + String.valueOf(i + 1)));
        }
        for (Player player : players) {
            player.start();
        }
        for (Bot bot : bots) {
   //         bot.start();
        }
    }
    public Boolean isEqual(Player player, String massage, String text) {
        if (massage.length() < 10 || !massage.substring(0, 10).equals(player.autoToken)) {
            return false;
        }
        if (massage.substring(10).equalsIgnoreCase(text)) {
            return true;
        }
        return false;
    }
    public void sendEmoji(Player sender, String emoji) throws IOException {
        synchronized (this) {
            for (Player player : players) {
                if (player == sender) {
                    continue;
                }
                System.out.println("::" + player.name);
                PrintWriter printWriter = new PrintWriter(player.socket.getOutputStream(), true);
                printWriter.println(player.autoToken + sender.name + ": " + emoji + "\n$$");
        //        printWriter.close();
            }
        }
    }
}
