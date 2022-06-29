package Game;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Player extends Thread{
    public Socket socket;
    public String autoToken = "", name = "";
    public GameServer gameServer;
    public ArrayList<Integer> cards = new ArrayList<>();
    public Player(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        Scanner socketScanner = null;
        try {
            socketScanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (gameServer.isGameOn) {
            String massage = socketScanner.nextLine();
            if (massage.length() < 10 || !massage.substring(0, 10).equals(autoToken)) {
                continue;
            }
            massage = massage.substring(10);
            if (massage.equalsIgnoreCase("PMC")) {
                System.out.println("HOOOOO");
                try {
                    gameServer.actPlayer(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (massage.equalsIgnoreCase("PN")) {
                try {
                    gameServer.playNinja(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (massage.equals(":)") || massage.equals(":(") || massage.equals(":D")) {
                System.out.println("HOOOO");
                try {
                    gameServer.sendEmoji(this, massage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
