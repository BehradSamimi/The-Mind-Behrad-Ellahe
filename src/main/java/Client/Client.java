package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static String autoToken = "";
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = null;
                try {
                    scanner = new Scanner(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                autoToken = scanner.nextLine();
                System.out.println("please Enter Your Name : \n");
                while (true) {
                    String massage = scanner.nextLine();
                    while (true) {
                        String tmp = "";
                        tmp = scanner.nextLine();
                        if (tmp.equals("$$")) {
                            break;
                        }
                        else {
                            massage += "\n" + tmp;
                        }
                    }
                    if (massage.length() < 10 || !massage.substring(0, 10).equals(autoToken)) {
                        continue;
                    }
                    System.out.println(massage.substring(10));
                }
            }
        }).start();
        Scanner systemScanner = new Scanner(System.in);
        PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);
        while (true) {
            String massage = systemScanner.nextLine();
            serverPrintWriter.println(autoToken + massage);
        }
    }
}
