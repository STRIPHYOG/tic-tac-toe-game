import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6370);
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket clientSocket1 = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket1);
                Socket clientSocket2 = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket2);
                ClientHandler clientHandler = new ClientHandler(clientSocket1, clientSocket2);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}