package Game;

import javax.print.DocFlavor;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Bot extends Thread {
    public String name = "";
    public ArrayList<Integer> cards = new ArrayList<>();
    public GameServer gameServer;
    public double passionateNumber = 2;
    public Bot(String name, GameServer gameServer) {
        this.name = name;
        this.gameServer = gameServer;
    }
    @Override
    public void run() {
        while (gameServer.isGameOn) {
            int waitDuration = waitDuration();
            Duration passedDuration = Duration.between(gameServer.lastPlay, LocalDateTime.now());
            if (waitDuration < passedDuration.getSeconds()) {
                try {
                    gameServer.actBot(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public int waitDuration() {
        if (cards.size() == 0) {
            return 10000;
        }
        return (int) ((double) (cards.get(0) - gameServer.lastCardPlayed) * passionateNumber);
    }
}
