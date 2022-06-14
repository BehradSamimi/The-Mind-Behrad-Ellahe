package Game;

import java.net.Socket;

public class Player extends Thread{
    public Socket socket;
    public String autoToken = "", name = "";
    public Player(Socket socket) {
        this.socket = socket;
    }
}
