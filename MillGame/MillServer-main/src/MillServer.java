import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class MillServer {
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(8000)) {
            System.out.println("Mill Server is Running...");
            var pool = Executors.newFixedThreadPool(200);

            while(true) {
                GameServer gameServer = new GameServer();
                pool.execute(gameServer.new PlayerServer(listener.accept(), 'X'));
                pool.execute(gameServer.new PlayerServer(listener.accept(), 'O'));
            }
        }
    }
}

class GameServer {

    private final PlayerServer[] board = new PlayerServer[24];

    PlayerServer currentPlayerServer;
    PlayerServer selectedPlayerServer;
    int oldStoneID;

    public boolean hasWinner() {
        return false;
    }

    public boolean boardFilledUp() {
        return false;
    }

    public synchronized void move(int location, PlayerServer playerServer, PrintWriter output) {
        if(playerServer != currentPlayerServer) {
            output.println("MOVE_ERROR Not your turn");
            throw new IllegalStateException("Not your turn");
        } else if (playerServer.opponent == null) {
            output.println("MOVE_ERROR You don't have an opponent yet");
            throw new IllegalStateException("You don't have an opponent yet");
        } else if (board[location] != null && !currentPlayerServer.muehlenSpeicher && currentPlayerServer.setzCounter > 0) {
            output.println("MOVE_ERROR Cell already occupied");
            throw new IllegalStateException("Cell already occupied");
        }

        checkGameState(location, playerServer, output);
    }

    private void checkGameState(int loc, PlayerServer playerServer, PrintWriter output) {

        // placing a stone and checking for mill
        if(board[loc] == null && !currentPlayerServer.muehlenSpeicher && currentPlayerServer.setzCounter > 0){
            --currentPlayerServer.setzCounter;

            board[loc] = currentPlayerServer;
            output.println("VALID_MOVE");
            playerServer.opponent.output.println("OPPONENT_MOVED " + loc);

            if (millCheck(loc, currentPlayerServer)){
                currentPlayerServer.muehlenSpeicher = true;
                output.println("MESSAGE Remove one of your opponents stones");
                playerServer.opponent.output.println("MESSAGE Your opponent is removing one of your stones");
                return;
            }

            if(currentPlayerServer.win) {
                return;
            }
            currentPlayerServer = currentPlayerServer.opponent;

        // removing a stone from the opponent
        } else if(currentPlayerServer.muehlenSpeicher) {

            if(board[loc] != currentPlayerServer && board[loc] != null) {
                if(!millCheck(loc, currentPlayerServer.opponent)) {

                    currentPlayerServer.muehlenSpeicher = false;

                    board[loc] = null;
                    output.println("DELETE " + loc);
                    playerServer.opponent.output.println("DELETE " + loc);

                    output.println("MESSAGE Valid delete, please wait");
                    playerServer.opponent.output.println("MESSAGE Opponent deleted, your turn");

                    if(currentPlayerServer.setzCounter < 1) {
                        checkEnemyStoneCount();
                        checkIfEnemyBlocked();
                    }

                    if(currentPlayerServer.win) {
                        return;
                    }
                    currentPlayerServer = currentPlayerServer.opponent;

                } else if(millCheck(loc, currentPlayerServer.opponent)) {
                    if(checkIfEveryStoneMill()) {

                        currentPlayerServer.muehlenSpeicher = false;

                        board[loc] = null;
                        output.println("DELETE " + loc);
                        playerServer.opponent.output.println("DELETE " + loc);

                        output.println("MESSAGE Valid delete, please wait");
                        playerServer.opponent.output.println("MESSAGE Opponent deleted, your turn");

                        if(currentPlayerServer.setzCounter < 1) {
                            checkEnemyStoneCount();
                            checkIfEnemyBlocked();
                        }

                        if(currentPlayerServer.win) {
                            return;
                        }
                        currentPlayerServer = currentPlayerServer.opponent;
                    }
                }
            }

        // moving a stone
        } else if(currentPlayerServer.setzCounter < 1 && currentPlayerServer.moveCounter > 0 && !currentPlayerServer.flymode) {
            if(board[loc] == currentPlayerServer && currentPlayerServer.moveCounter > 1) {
                if(!checkStoneBlocked(loc)) {
                    return;
                }
                oldStoneID = loc;

                board[loc] = selectedPlayerServer;
                output.println("SELECT " + loc);
                playerServer.opponent.output.println("SELECT " + loc);

                output.println("MESSAGE Valid select, please move");
                playerServer.opponent.output.println("MESSAGE Opponent selected, please wait");

                --currentPlayerServer.moveCounter;
            }

            if(checkIfNearStone(loc, oldStoneID) && board[loc] == null) {

                board[loc] = currentPlayerServer;
                output.println("VALID_MOVE " + loc);
                playerServer.opponent.output.println("OPPONENT_MOVED " + loc);


                board[oldStoneID] = null;
                output.println("DELETE " + oldStoneID);
                playerServer.opponent.output.println("DELETE " + oldStoneID);

                output.println("MESSAGE Valid move, please wait");
                playerServer.opponent.output.println("MESSAGE Opponent moved, your turn");

                checkIfEnemyBlocked();

                currentPlayerServer.moveCounter = 2;

                if(millCheck(loc, currentPlayerServer)) {
                    currentPlayerServer.muehlenSpeicher = true;
                    output.println("MESSAGE Remove one of your opponents stones");
                    playerServer.opponent.output.println("MESSAGE Your opponent is removing one of your stones");
                } else {

                    if(currentPlayerServer.win) {
                        return;
                    }
                    currentPlayerServer = currentPlayerServer.opponent;
                }
            }

        // fly mode
        } else if(currentPlayerServer.setzCounter < 1 && currentPlayerServer.moveCounter > 0) {
            if(board[loc] == currentPlayerServer && currentPlayerServer.moveCounter > 1) {
                oldStoneID = loc;

                board[loc] = selectedPlayerServer;
                output.println("SELECT " + loc);
                playerServer.opponent.output.println("SELECT " + loc);

                output.println("MESSAGE Valid select, please move");
                playerServer.opponent.output.println("MESSAGE Opponent selected, please wait");

                --currentPlayerServer.moveCounter;
                return;
            }

            if(board[loc] == null && currentPlayerServer.moveCounter == 1) {

                board[loc] = currentPlayerServer;
                output.println("VALID_MOVE " + loc);
                playerServer.opponent.output.println("OPPONENT_MOVED " + loc);

                board[oldStoneID] = null;
                output.println("DELETE " + oldStoneID);
                playerServer.opponent.output.println("DELETE " + oldStoneID);

                output.println("MESSAGE Valid move, please wait");
                playerServer.opponent.output.println("MESSAGE Opponent moved, your turn");

                checkIfEnemyBlocked();
                currentPlayerServer.moveCounter = 2;

                if(millCheck(loc, currentPlayerServer)) {
                    currentPlayerServer.muehlenSpeicher = true;
                } else {

                    if(currentPlayerServer.win) {
                        return;
                    }
                    currentPlayerServer = currentPlayerServer.opponent;
                }
            }
        }
    }

    public boolean millCheck(int buttonID, PlayerServer playerToCheck){
        return board[0] == playerToCheck && board[12] == playerToCheck && board[1] == playerToCheck && (buttonID == 0 || buttonID == 12 || buttonID == 1) ||
                board[4] == playerToCheck && board[13] == playerToCheck && board[5] == playerToCheck && (buttonID == 4 || buttonID == 13 || buttonID == 5) ||
                board[8] == playerToCheck && board[14] == playerToCheck && board[9] == playerToCheck && (buttonID == 8 || buttonID == 14 || buttonID == 9) ||
                board[18] == playerToCheck && board[19] == playerToCheck && board[20] == playerToCheck && (buttonID == 18 || buttonID == 19 || buttonID == 20) ||
                board[23] == playerToCheck && board[22] == playerToCheck && board[21] == playerToCheck && (buttonID == 23 || buttonID == 22 || buttonID == 21) ||
                board[10] == playerToCheck && board[17] == playerToCheck && board[11] == playerToCheck && (buttonID == 10 || buttonID == 17 || buttonID == 11) ||
                board[6] == playerToCheck && board[16] == playerToCheck && board[7] == playerToCheck && (buttonID == 6 || buttonID == 16 || buttonID == 7) ||
                board[2] == playerToCheck && board[15] == playerToCheck && board[3] == playerToCheck && (buttonID == 2 || buttonID == 15 || buttonID == 3) ||
                board[0] == playerToCheck && board[18] == playerToCheck && board[2] == playerToCheck && (buttonID == 0 || buttonID == 18 || buttonID == 2) ||
                board[4] == playerToCheck && board[19] == playerToCheck && board[6] == playerToCheck && (buttonID == 4 || buttonID == 19 || buttonID == 6) ||
                board[8] == playerToCheck && board[20] == playerToCheck && board[10] == playerToCheck && (buttonID == 8 || buttonID == 20 || buttonID == 10) ||
                board[12] == playerToCheck && board[13] == playerToCheck && board[14] == playerToCheck && (buttonID == 12 || buttonID == 13 || buttonID == 14) ||
                board[17] == playerToCheck && board[16] == playerToCheck && board[15] == playerToCheck && (buttonID == 17 || buttonID == 16 || buttonID == 15) ||
                board[9] == playerToCheck && board[23] == playerToCheck && board[11] == playerToCheck && (buttonID == 9 || buttonID == 23 || buttonID == 11) ||
                board[5] == playerToCheck && board[22] == playerToCheck && board[7] == playerToCheck && (buttonID == 5 || buttonID == 22 || buttonID == 7) ||
                board[1] == playerToCheck && board[21] == playerToCheck && board[3] == playerToCheck && (buttonID == 1 || buttonID == 21 || buttonID == 3);
    }

    public void checkEnemyStoneCount(){
        int passivePlayerStoneCount = 0;
        for(int i = 0; i < 24; i++){
            if(board[i] == currentPlayerServer.opponent){
                ++passivePlayerStoneCount;
            }
        }
        if(passivePlayerStoneCount < 3){
            currentPlayerServer.win = true;
        }
        if(passivePlayerStoneCount == 3){
            currentPlayerServer.output.println("MESSAGE Your opponent can jump now");
            currentPlayerServer.opponent.output.println("MESSAGE You can jump now");
            currentPlayerServer.opponent.flymode = true;
        }
    }

    public void checkIfEnemyBlocked(){
        for(int i = 0; i < 24; i++){
            if(board[i] == currentPlayerServer.opponent){
                if(checkStoneBlocked(i)){
                    return;
                }
            }
        }
        currentPlayerServer.win=true;
    }

    public boolean checkStoneBlocked(int loc){
        if (loc == 0 && (board[12] == null || board[18] == null))
            return true;
        if (loc == 12 && (board[0] == null || board[13] == null || board[1] == null))
            return true;
        if (loc == 1 && (board[12] == null || board[21] == null))
            return true;
        if (loc == 4 && (board[19] == null || board[13] == null))
            return true;
        if (loc == 13 && (board[4] == null || board[12] == null || board[5] == null || board[14] == null))
            return true;
        if (loc == 5 && (board[13] == null || board[22] == null))
            return true;
        if (loc == 8 && (board[20] == null || board[14] == null))
            return true;
        if (loc == 14 && (board[8] == null || board[13] == null|| board[9] == null))
            return true;
        if (loc == 9 && (board[14] == null || board[23] == null))
            return true;
        if (loc == 18 && (board[0] == null || board[19] == null || board[2] == null))
            return true;
        if (loc == 19 && (board[18] == null || board[4] == null || board[20] == null || board[6] == null))
            return true;
        if (loc == 20 && (board[19] == null || board[8] == null || board[10] == null))
            return true;
        if(loc == 23 && (board[9] == null || board[22] == null || board[11] == null))
            return true;
        if(loc == 22 && (board[23] == null || board[5] == null || board[21] == null || board[7] == null))
            return true;
        if(loc == 21 && (board[1] == null || board[22] == null || board[3] == null))
            return true;
        if(loc == 10 && (board[20] == null || board[17] == null))
            return true;
        if (loc == 17 && (board[10] == null || board[11] == null || board[16] == null))
            return true;
        if (loc == 11 && (board[17] == null || board[23] == null))
            return true;
        if (loc == 6 && (board[19] == null || board[16] == null))
            return true;
        if (loc == 16 && (board[6] == null || board[17] == null || board[7] == null || board[15] == null))
            return true;
        if (loc == 7 && (board[16] == null || board[22] == null))
            return true;
        if (loc == 2 && (board[18] == null || board[15] == null))
            return true;
        if (loc == 15 && (board[2] == null || board[16] == null || board[3] == null))
            return true;
        return loc == 3 && (board[15] == null || board[21] == null);
    }

    public boolean checkIfEveryStoneMill(){
        for(int i=0; i<24; i++){
            if(board[i] == currentPlayerServer.opponent){
                if (!millCheck(i, currentPlayerServer.opponent)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkIfNearStone(int loc, int oldStoneID) {
        return oldStoneID == 0 && (loc == 12 || loc == 18) ||
                oldStoneID == 12 && (loc == 0 || loc == 13 || loc == 1) ||
                oldStoneID == 1 && (loc == 12 || loc == 21) ||
                oldStoneID == 4 && (loc == 19 || loc == 13) ||
                oldStoneID == 13 && (loc == 4 || loc == 12 || loc == 5 || loc == 14) ||
                oldStoneID == 5 && (loc == 13 || loc == 22) ||
                oldStoneID == 8 && (loc == 20 || loc == 14) ||
                oldStoneID == 14 && (loc == 8 || loc == 13 || loc == 9) ||
                oldStoneID == 9 && (loc == 14 || loc == 23) ||
                oldStoneID == 18 && (loc == 0 || loc == 19 || loc == 2) ||
                oldStoneID == 19 && (loc == 18 || loc == 4 || loc == 20 || loc == 6) ||
                oldStoneID == 20 && (loc == 19 || loc == 8 || loc == 10) ||
                oldStoneID == 23 && (loc == 9 || loc == 22 || loc == 11) ||
                oldStoneID == 22 && (loc == 23 || loc == 5 || loc == 21 || loc == 7) ||
                oldStoneID == 21 && (loc == 1 || loc == 22 || loc == 3) ||
                oldStoneID == 10 && (loc == 20 || loc == 17) ||
                oldStoneID == 17 && (loc == 10 || loc == 11 || loc == 16) ||
                oldStoneID == 11 && (loc == 17 || loc == 23) ||
                oldStoneID == 6 && (loc == 19 || loc == 16) ||
                oldStoneID == 16 && (loc == 6 || loc == 17 || loc == 7 || loc == 15) ||
                oldStoneID == 7 && (loc == 16 || loc == 22) ||
                oldStoneID == 2 && (loc == 18 || loc == 15) ||
                oldStoneID == 15 && (loc == 2 || loc == 16 || loc == 3) ||
                oldStoneID == 3 && (loc == 15 || loc == 21);
    }


    class PlayerServer implements Runnable {

        String name;
        public Color colour;
        int setzCounter = 9;
        int moveCounter = 2;
        boolean muehlenSpeicher = false;
        boolean flymode = false;
        boolean win = false;

        char mark;
        PlayerServer opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;

        public PlayerServer(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
        }

        @Override
        public void run() {
            try {
                setup();
                processCommands();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println(("WELCOME " + mark));

            if(mark == 'X') {
                currentPlayerServer = this;
                output.println("MESSAGE Waiting for opponent to connect");
            } else {
                opponent = currentPlayerServer;
                opponent.opponent = this;
                opponent.output.println("MESSAGE Your move");
            }
        }

        private void processCommands() {
            while(input.hasNextLine()) {
                String command = input.nextLine();

                if(command.startsWith("QUIT")) {
                    return;
                } else if(command.startsWith("MOVE")) {
                    processMoveCommand(Integer.parseInt(command.substring(5)));
                    System.out.println("Player " + mark + ": " + command);
                }
            }
        }

        private void processMoveCommand(int location) {
            try {
                move(location, this, output);
//                output.println("VALID_MOVE");
//                opponent.output.println("OPPONENT_MOVED " + location);
                if(currentPlayerServer.win) {
                    output.println("VICTORY");
                    opponent.output.println("DEFEAT");
                }

            } catch (IllegalStateException e) {
                output.println("MESSAGE " + e.getMessage());
            }
        }
    }
}
