package TCP;

import rendering.GUIInGame;
import rendering.Player;
import rendering.StoneButton;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;

public class MillClient implements Runnable {

    private final Socket socket;
    private final Scanner in;
    public final PrintWriter out;

    private final int[] board = new int[24];
    private StoneButton currentButton;

    public StoneButton[] stoneButtonList;
    public GUIInGame inGameWindow;
    public Player playerSelf;

    public MillClient(String serverAddress, StoneButton[] stoneButtonList, GUIInGame inGameWindow, Player player) throws Exception {

        socket = new Socket(serverAddress, 8000);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        this.stoneButtonList = stoneButtonList;
        this.playerSelf = player;
        this.inGameWindow = inGameWindow;

        for (StoneButton stoneButton : this.stoneButtonList) {
            stoneButton.addActionListener(e -> {
                    currentButton = stoneButton;
                    out.println("MOVE " + stoneButton.buttonID);
            });
        }
    }

    public void run() {
        try {
            String response = in.nextLine();
            char mark = response.charAt(8);
            char opponentMark = mark == 'X' ? 'O' : 'X';
            System.out.println("Mill: Player: " + mark);

            playerSelf.colour = mark == 'X' ? Color.BLACK : Color.WHITE;
            Color opponentColour = playerSelf.colour == Color.BLACK ? Color.WHITE : Color.BLACK;

            while(in.hasNextLine()) {
                response = in.nextLine();
                if( response.startsWith("VALID_MOVE")) {
                    this.inGameWindow.GameWindow.playButtonClick("/img/stonePlacement.wav", 15.0f);

                    currentButton.setBackground(playerSelf.colour);
                    currentButton.repaint();
                    this.inGameWindow.actionDisplay.setText("<html><p style=\"width:130px\">Valid move, please wait</p></html>");

                } else if(response.startsWith("OPPONENT_MOVED")) {
                    this.inGameWindow.GameWindow.playButtonClick("/img/stonePlacement.wav", 15.0f);

                    int loc = Integer.parseInt(response.substring(15));
                    stoneButtonList[loc].setBackground(opponentColour);
                    stoneButtonList[loc].repaint();
                    this.inGameWindow.actionDisplay.setText("<html><p style=\"width:130px\">Opponent moved, your turn</p></html>");

                } else if(response.startsWith("DELETE")) {
                    this.inGameWindow.GameWindow.playButtonClick("/img/stonePlacement.wav", 15.0f);

                    int loc = Integer.parseInt(response.substring(7));
                    stoneButtonList[loc].setBackground(new Color(129, 129, 129));
                    stoneButtonList[loc].repaint();

                } else if(response.startsWith("SELECT")) {
                    this.inGameWindow.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

                    int loc = Integer.parseInt(response.substring(7));
                    stoneButtonList[loc].setBackground(new Color(255,136,0));
                    stoneButtonList[loc].repaint();

                } else if(response.startsWith("MESSAGE")) {
                    this.inGameWindow.actionDisplay.setText("<html><p style=\"width:130px\">" + response.substring(8) + "</p></html>");

                    if(response.startsWith("MESSAGE Remove") || response.startsWith("MESSAGE Your opponent is removing")) {
                        this.inGameWindow.GameWindow.playButtonClick("/img/millSound.wav", 10.0f);
                    }

                } else if(response.startsWith("MOVE_ERROR")) {
                    this.inGameWindow.actionDisplay.setText("<html><p style=\"width:130px\">" + response.substring(10) + "</p></html>");

                } else if(response.startsWith("VICTORY")) {
                    this.inGameWindow.GameWindow.playButtonClick("/img/winningSound.wav", 0.0f);

                    System.out.println("Winner Winner");
                    this.inGameWindow.displayEndScreen("VICTORY", playerSelf.colour);
                    break;

                } else if(response.startsWith("DEFEAT")) {
                    this.inGameWindow.GameWindow.playButtonClick("/img/losingSound.wav", 0.0f);

                    System.out.println("Sorry you lost");
                    this.inGameWindow.displayEndScreen("DEFEAT", opponentColour);
                    break;

                } else if(response.startsWith("TIE")) {
                    System.out.println("Tie");
                    break;

                } else if(response.startsWith("OTHER_PLAYER_LEFT")) {
                    System.out.println("Other player left");
                    JOptionPane.showMessageDialog(inGameWindow, "Other player left");
                    this.inGameWindow.GameWindow.GameQuitGame();
                    break;
                }
            }
            System.out.println("QUIT");
            out.println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void quitGame() {
        out.println("QUIT");

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) throws Exception {
////        if (args.length != 1) {
////            System.err.println("Pass the server IP as the sole command line argument");
////            return;
////        }
//        TCP.MillClient client = new TCP.MillClient("localhost");
//        client.play();
//    }
}
