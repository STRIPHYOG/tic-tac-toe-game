import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket1;
    private Socket clientSocket2;
    private ObjectInputStream inputStream1;
    private ObjectOutputStream outputStream1;
    private ObjectInputStream inputStream2;
    private ObjectOutputStream outputStream2;

    private char[][] board;
    private char currentPlayer;

    public ClientHandler(Socket clientSocket1, Socket clientSocket2) {
        this.clientSocket1 = clientSocket1;
        this.clientSocket2 = clientSocket2;

        try {
            outputStream1 = new ObjectOutputStream(clientSocket1.getOutputStream());
            inputStream1 = new ObjectInputStream(clientSocket1.getInputStream());
            outputStream2 = new ObjectOutputStream(clientSocket2.getOutputStream());
            inputStream2 = new ObjectInputStream(clientSocket2.getInputStream());
            board = new char[3][3];
            currentPlayer = 'X';
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            sendMessage1("Welcome to Tic Tac Toe! You are player " + currentPlayer);
            sendMessage2("Welcome to Tic Tac Toe! You are player " + ((currentPlayer == 'X') ? 'O' : 'X'));

            while (true) {
                sendMessage1("Current Board:\n" + displayBoard());
                sendMessage2("Current Board:\n" + displayBoard());
                if (currentPlayer == 'X') {
                    sendMessage1("Enter row (1-3) and column (1-3) e.g. 11 : \nYour move (" + currentPlayer + ") : ");
                    sendMessage2("Move for X");
                    String move = (String) inputStream1.readObject();

                    int row = (move.charAt(0) - 49);
                    int col = (move.charAt(1) - 49);

                    if (isValidMove(row, col)) {
                        makeMove(row, col);
                        if (isWinner()) {
                            printWin(currentPlayer);
                            break;
                        } else if (isBoardFull()) {
                            printTie();
                            break;
                        } else {
                            switchPlayer();
                        }
                    } else {
                        sendMessage1("Invalid move. Try again.");
                    }
                } else {
                    sendMessage2("Enter row (1-3) and column (1-3) e.g. 11 : \nYour move (" + currentPlayer + ") : ");
                    sendMessage1("Move for 0");
                    String move = (String) inputStream2.readObject();
                    int row = (move.charAt(0) - 49);
                    int col = (move.charAt(1) - 49);

                    if (isValidMove(row, col)) {
                        makeMove(row, col);
                        if (isWinner()) {
                            printWin(currentPlayer);
                            break;
                        } else if (isBoardFull()) {
                            printTie();
                            break;
                        } else {
                            switchPlayer();
                        }
                    } else {
                        sendMessage2("Invalid move. Try again.");
                    }
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket1.close();
                clientSocket2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printTie() {
        try {
            sendMessage1(displayBoard() + "\nThe game is a tie!");
            sendMessage2(displayBoard() + "\nThe game is a tie!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printWin(char ch) {
        try {
            sendMessage1("Current Board:\n" + displayBoard());
            sendMessage2("Current Board:\n" + displayBoard());
            if (ch == 'X') {
                sendMessage1("\nYou win!");
                sendMessage2("\nYou lose");
            } else {
                sendMessage2("\nYou win!");
                sendMessage1("\nYou lose");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == '\0';
    }

    private void makeMove(int row, int col) {
        board[row][col] = currentPlayer;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private boolean isWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true; // Check rows
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true; // Check columns
            }
        }
        return (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer)
                || (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    return false;
                }
            }
        }
        return true; // Board is full, and no one has won
    }

    private String displayBoard() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                display.append(board[i][j] == '\0' ? " - " : " " + board[i][j] + " ");
                if (j < 2) {
                    display.append("|");
                }
            }
            display.append("\n");
            if (i < 2) {
                display.append("-----------\n");
            }
        }
        return display.toString();
    }

    private void sendMessage1(String message) throws IOException {
        outputStream1.writeObject(message);
        outputStream1.flush();
    }

    private void sendMessage2(String message) throws IOException {
        outputStream2.writeObject(message);
        outputStream2.flush();
    }
}