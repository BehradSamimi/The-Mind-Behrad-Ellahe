package Game;

import com.sun.source.tree.SynchronizedTree;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class GameServer extends Thread {

    public String sendAboveGameState;
    public String GameState;
    public ArrayList<FloorCard> floorCards = new ArrayList<>();
    public int heartNumber = 0;
    public int ninjaNumber = 2;
    public int gameLevel = 1;
    public LocalDateTime lastPlay = LocalDateTime.now();

    public Boolean isGameOn = true;
    public String name = "";
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Bot> bots = new ArrayList<>();
    public int maxPlayer = 1;
    @Override
    public void run() {
        heartNumber = maxPlayer;
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
        int x = maxPlayer - players.size();
        for (int i = 0; i < x; i++) {
            bots.add(new Bot("bot" + String.valueOf(i + 1)));
        }
        for (Player player : players) {
            player.start();
        }
        for (Bot bot : bots) {
   //         bot.start();
        }
        lastPlay = LocalDateTime.now();
        try {
            startLevel(1);
        } catch (IOException e) {
            e.printStackTrace();
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

    public synchronized void actPlayer(Player player) throws IOException {
        sendAboveGameState = "";
        System.out.println("FGWSF");
        Duration duration = Duration.between(lastPlay, LocalDateTime.now());
        if (duration.getSeconds() < 1 || player.cards.size() == 0) {
            return;
        }
        System.out.println("dghghj");
        int beforeSize = floorCards.size();
        for (Player nowPlayer : players) {
            while (nowPlayer.cards.size() > 0 && nowPlayer.cards.get(0) <= player.cards.get(0)) {
                floorCards.add(new FloorCard(nowPlayer.name, nowPlayer.cards.get(0)));
                nowPlayer.cards.remove(0);
            }
        }
        System.out.println("dgfeeeehghj");
        //TODO
        if (floorCards.size() - beforeSize > 1) {
            sendAboveGameState += "OOPS player : " + player.name + " played his min card But we had smaller card than that card SOO\n";
            sendAboveGameState += "YOU GUYS LOST ONE HEART CARD\n";
            heartNumber--;
            if (heartNumber == 0) {
                //TODO
            }
            sendAboveGameState += "New GameState : \n";
        }
        else {
            sendAboveGameState += "GREAT SHOT\nplayer : " + player.name + " played his min card and it was the RIGHT ONE\n";
            sendAboveGameState += "New GameState : \n";
        }
        sendStateForPlayer();
        lastPlay = LocalDateTime.now();
        System.out.println("THATS FUCKED");
    }
    public synchronized void actBot(Bot bot) {
        Duration duration = Duration.between(LocalDateTime.now(), lastPlay);
        if (duration.getSeconds() < 1) {
            return;
        }




        lastPlay = LocalDateTime.now();
    }
    public synchronized void startLevel(int level) throws IOException {
        sendAboveGameState = "";
        sendAboveGameState = "level " + String.valueOf(level) + " started.\n";
        ArrayList<Integer> cards = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            cards.add(i + 1);
        Collections.shuffle(cards);
        if (level == 3 || level == 6 || level == 9) {
            heartNumber++;
            sendAboveGameState += "For your gift You Guys Got a new HEART Card\n";
        }
        if (level == 2 || level == 5 || level == 8) {
            ninjaNumber++;
            sendAboveGameState += "For your gift You Guys Got a new NINJA Card\n";
        }
        for (Player player : players) {
            player.cards.clear();
            for (int i = 0; i < level; i++) {
                player.cards.add(cards.get(0));
                cards.remove(0);
            }
            Collections.sort(player.cards);
        }
        for (Bot bot : bots) {
            bot.cards.clear();;
            for (int i = 0; i < level; i++) {
                bot.cards.add(cards.get(0));
                cards.remove(0);
            }
            Collections.sort(bot.cards);
        }
        sendStateForPlayer();
    }
    public synchronized void sendStateForPlayer() throws IOException {
        sendAboveGameState += "FLOOR CARDS ::\n";
        for (FloorCard floorCard : floorCards) {
            sendAboveGameState += "card number : " + floorCard.cardNumber + " owned by : " + floorCard.name + "\n";
        }
        sendAboveGameState += "YOUR HAND ::\n";
        for (Player player : players) {
            PrintWriter printWriter = new PrintWriter(player.socket.getOutputStream(), true);
            String res = sendAboveGameState;
            for (Integer cardNumber : player.cards) {
                res += String.valueOf(cardNumber) + " ";
            }
            res += "\n$$";
            System.out.println(sendAboveGameState);
            printWriter.println(player.autoToken + res);
        }

    }
}
