package Server;

import Game.GameServer;
import Game.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class MainServer {
    public static final int autoTokenLen = 10;
    public static ArrayList<GameServer> pendingGameServers = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Player newPlayer = new Player(socket);
                    newPlayer.autoToken = generateAutoToken();
                    PrintWriter printWriter = null;
                    try {
                        printWriter = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printWriter.println(newPlayer.autoToken);
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(socket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (true) {
                        String newMassage = scanner.nextLine();
                        if (newMassage.length() < 10 || !newMassage.substring(0, 10).equals(newPlayer.autoToken)) {
                            continue;
                        }
                        else {
                            newPlayer.name = newMassage.substring(10);
                            System.out.println(newPlayer.name);
                            break;
                        }
                    }
                    printWriter.println(newPlayer.autoToken + askForGameServer() + "\n$$");
                    while (true) {
                        String newMassage = scanner.nextLine();
                        if (newMassage.length() < 10 || !newMassage.substring(0, 10).equals(newPlayer.autoToken)) {
                            continue;
                        }
                        else {
                            Boolean wantNewServer = true;
                            String res = newMassage.substring(10);
                            System.out.println(res + ".");
                            for (int i = 0; i < pendingGameServers.size(); i++) {
                                if (res.equals(String.valueOf(i + 1))) {
                                    pendingGameServers.get(i).players.add(newPlayer);
                                    newPlayer.gameServer = pendingGameServers.get(i);
                                    PrintWriter tellOwner = null;
                                    try {
                                         tellOwner = new PrintWriter(pendingGameServers.get(i).players.get(0).socket.getOutputStream(), true);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    tellOwner.println(pendingGameServers.get(0).players.get(0).autoToken + newPlayer.name + " joined your Game Server." + "\n$$");
                           //         tellOwner.close();
                                    printWriter.println(newPlayer.autoToken + "You joined " + pendingGameServers.get(i).name + " game server" + "\npleas wait for " + pendingGameServers.get(i).name + " to Start the game \n$$");
                                    wantNewServer = false;
                                }
                            }
                            if (wantNewServer) {
                                GameServer gameServer = new GameServer();
                                gameServer.name = newPlayer.name;
                                pendingGameServers.add(gameServer);
                                gameServer.players.add(newPlayer);
                                newPlayer.gameServer = gameServer;
                                printWriter.println(newPlayer.autoToken + "please enter max number of online player that you want (a number between 1 to 4) : " + "\n$$");
                                while (true) {
                                    newMassage = scanner.nextLine();
                                    if (newMassage.length() < 10 || !newMassage.substring(0, 10).equals(newPlayer.autoToken)) {
                                        continue;
                                    }
                                    gameServer.maxPlayer = Integer.valueOf(newMassage.substring(10));
                                    break;
                                }
                                printWriter.println(newPlayer.autoToken + "when ever you want to start the game please enter 'START' . " + "\n$$");
                                gameServer.start();
                            }
                        }
                        break;
                    }
                //    printWriter.close();
                //    scanner.close();
                }
            }).start();

        }

    }
    public static String generateAutoToken() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(autoTokenLen);
        for (int i = 0; i < autoTokenLen; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
    public static String askForGameServer() {
        String massage = "please enter number : \n";
        int i = 0;
        for (i = 0; i < pendingGameServers.size(); i++) {
            massage = massage + String.valueOf(i + 1) + " : join " + pendingGameServers.get(i).name + " game server \n";
        }
        massage = massage + String.valueOf(i + 1) + " : creating Your Own Game sever \n";
        return massage;
    }
}
