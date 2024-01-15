import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            int port = 6370;
            Socket socket = null;
            System.out.println("trying port " + port);
            socket = new Socket("192.168.24.17", port);
            System.out.println("Connected to the server.");

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                String message = (String) inputStream.readObject();
                if (message.contains("Board"))
                    System.out.print("\033[H\033[2J");
                System.out.println(message);
                if (message.contains("Enter row")) {
                    String move = sc.nextLine();
                    outputStream.writeObject(move);
                    outputStream.flush();
                }

                if (message.contains("wins") || message.contains("tie") || message.contains("lose")) {
                    break;
                }
            }
            sc.close();
            socket.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}