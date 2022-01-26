package rendering;

import TCP.MillClient;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GUIInGame extends JPanel {
    public static Color InGameButtonsBackgroundColor = new Color(255,255,255);
    private static Color textColor;

    public final Player player1;

    // OFFLINE: player 2 name
//    public final Player player2;

    private final JPanel InGameLeftMenu;
    private final JPanel InGameButtons;
    private final JButton InGameButtonReturnToMenu;
    private final JButton InGameButtonExitGame;

    private final JPanel p1Display;
    public final JLabel turnDisplay;
    public JLabel actionDisplay;

    public final JPanelBoard board;
    private final JPanelWithBackground background;

    public final Game GameWindow;

    public GUIInGame(Game GameWindow, Player player1) {
        // GameWindow
        this.GameWindow = GameWindow;

        System.out.println("InGame initiating...");

        // players
        this.player1 = player1;

        // OFFLINE: player 2 name
//        this.player2 = player2;

        // buttons
        this.InGameButtonReturnToMenu = new JButton("     Back      ");
        this.InGameButtonExitGame = new JButton(" Exit Game ");

        // button panel
        this.InGameButtons = new JPanel();
        this.InGameButtons.setLayout(new BoxLayout(InGameButtons, BoxLayout.Y_AXIS));
        this.InGameButtons.setBackground(InGameButtonsBackgroundColor);
        this.InGameButtons.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, InGameButtonsBackgroundColor));
        this.InGameButtons.add(this.InGameButtonReturnToMenu);
        this.InGameButtons.add(Box.createRigidArea(new Dimension(0,10)));
        this.InGameButtons.add(this.InGameButtonExitGame);

        // left menu
        this.InGameLeftMenu = new JPanel(new BorderLayout(10,0));
        this.InGameLeftMenu.add(this.InGameButtons);

        // board
        this.board = new JPanelBoard(this);

        // nameDisplays
        this.turnDisplay = new JLabel("Turn:");
        this.turnDisplay.setFont(new Font("Verdana", Font.PLAIN, 30));
        this.turnDisplay.setForeground(Color.BLACK);

        // actionDisplays
//        TODO:
        this.actionDisplay = new JLabel();
        this.actionDisplay.setFont(new Font("Verdana", Font.PLAIN, 20));
        this.actionDisplay.setForeground(Color.BLACK);

        // OFFLINE:
//        if(this.GameWindow.gameLoop.getPassivePlayer().colour == player1.colour) {
//            textColor = Color.BLACK;
//        } else {
//            textColor = Color.WHITE;
//        }
//        this.actionDisplay.setForeground(textColor);

        // playerDisplays
        this.p1Display = new JPanel();
        this.p1Display.setBorder(BorderFactory.createEmptyBorder(30,40,10,10));
        this.p1Display.setLayout(new BoxLayout(p1Display, BoxLayout.Y_AXIS));
        this.p1Display.add(turnDisplay);
        this.p1Display.add(Box.createRigidArea(new Dimension(0,10)));
        this.p1Display.add(actionDisplay);
        this.p1Display.setOpaque(false);
        this.board.add(p1Display, BorderLayout.WEST);

        // background
        this.background = new JPanelWithBackground("img/Background_InGame_1.jpg");
        this.background.setLayout(new BorderLayout());
        this.background.add(board, BorderLayout.CENTER);


        // InGamePanel
        this.setLayout(new BorderLayout(0,0));
        this.add(InGameButtons, BorderLayout.WEST);
        this.add(background, BorderLayout.CENTER);

        setupButtonsActionListener(GameWindow);
    }

    private void setupButtonsActionListener(Game GameWindow) {

        // back button
        InGameButtonReturnToMenu.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            GameWindow.changeView();
        });

        InGameButtonExitGame.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            if(JOptionPane.showOptionDialog(GameWindow, "Do you want to leave the game?", "Exit game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "Yes") == 0) {
                this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

                this.GameWindow.GameQuitGame();
            } else {
                this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);
            }
        });
    }

    public void displayEndScreen(String status, Color winnerColor) {
        this.board.setVisible(false);
        String message;

        if(Objects.equals(status, "DEFEAT")) {
            message = "You lost the game";
        } else {
            message = "You won the game";
        }
        JLabel winText = new JLabel(message, JLabel.CENTER);
        winText.setFont(new Font("Verdana", Font.PLAIN, 50));

        winText.setForeground(winnerColor);

        this.background.add(winText, BorderLayout.CENTER);
        this.GameWindow.revalidate();
    }
}



// the game board
class JPanelBoard extends JPanel{

    public GUIInGame inGameWindow;
    public int[][] stoneButtonCoords = new int[24][2];
    public StoneButton[] stoneButtonList;

    public JPanelBoard(GUIInGame GameWindow){

        System.out.println("Board initiating...");

        this.inGameWindow = GameWindow;
        this.setLayout(new BorderLayout(0,0));
        this.setBorder(BorderFactory.createEmptyBorder(0,0,0,50));
        setSize(inGameWindow.getWidth(), inGameWindow.getHeight());
        setOpaque(false);
    }

    // paints the game board and instantiates the buttons
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0));
        g2.setStroke(new BasicStroke(10));

        //outer square
        int s1Height = (int)(inGameWindow.getHeight() / 1.5);
        int s1Width = s1Height;
        int s1X = (inGameWindow.getWidth() - s1Width) / 2;
        int s1Y = (inGameWindow.getHeight() - s1Height) / 2;
        g2.drawRect(s1X, s1Y, s1Width, s1Height);
        stoneButtonCoords[0][0] = s1X; stoneButtonCoords[0][1] = s1Y;
        stoneButtonCoords[1][0] = s1X + s1Width; stoneButtonCoords[1][1] = s1Y;
        stoneButtonCoords[2][0] = s1X; stoneButtonCoords[2][1] = s1Y + s1Height;
        stoneButtonCoords[3][0] = s1X + s1Width; stoneButtonCoords[3][1] = s1Y + s1Height;

        //middle square
        int s2Height = (int)(s1Height / 1.5);
        int s2Width = s2Height;
        int s2X = s1X + (s1Width - s2Width) /2;
        int s2Y = s1Y + (s1Height - s2Height) /2;
        g2.drawRect(s2X, s2Y, s2Width, s2Height);
        stoneButtonCoords[4][0] = s2X; stoneButtonCoords[4][1] = s2Y;
        stoneButtonCoords[5][0] = s2X + s2Width; stoneButtonCoords[5][1] = s2Y;
        stoneButtonCoords[6][0] = s2X; stoneButtonCoords[6][1] = s2Y + s2Height;
        stoneButtonCoords[7][0] = s2X + s2Width; stoneButtonCoords[7][1] = s2Y + s2Height;

        //inner square
        int s3Height = (int)(s1Height / 2.75);
        int s3Width = s3Height;
        int s3X = s1X + (s1Width - s3Width) /2;
        int s3Y = s1Y + (s1Height - s3Height) /2;
        g2.drawRect(s3X, s3Y, s3Width, s3Height);
        stoneButtonCoords[8][0] = s3X; stoneButtonCoords[8][1] = s3Y;
        stoneButtonCoords[9][0] = s3X + s3Width; stoneButtonCoords[9][1] = s3Y;
        stoneButtonCoords[10][0] = s3X; stoneButtonCoords[10][1] = s3Y + s3Height;
        stoneButtonCoords[11][0] = s3X + s3Width; stoneButtonCoords[11][1] = s3Y + s3Height;

        //uv = upper vertical
        int uvLineX1 = s1X + s1Width / 2;
        int uvLineY1 = s1Y;
        int uvLineX2 = uvLineX1;
        int uvLineY2 = s3Y;
        g2.drawLine(uvLineX1, uvLineY1, uvLineX2, uvLineY2);
        stoneButtonCoords[12][0] = uvLineX1; stoneButtonCoords[12][1] = uvLineY1;
        stoneButtonCoords[13][0] = uvLineX1; stoneButtonCoords[13][1] = uvLineY1 + (uvLineY2 - uvLineY1) / 2;
        stoneButtonCoords[14][0] = uvLineX2; stoneButtonCoords[14][1] = uvLineY2;

        //lv = lower vertical
        int lvLineX1 = uvLineX1;
        int lvLineY1 = s1Height + s1Y;
        int lvLineX2 = uvLineX1;
        int lvLineY2 = s3Height + s3Y;
        g2.drawLine(lvLineX1, lvLineY1, lvLineX2, lvLineY2);
        stoneButtonCoords[15][0] = lvLineX1; stoneButtonCoords[15][1] = lvLineY1;
        stoneButtonCoords[16][0] = lvLineX1; stoneButtonCoords[16][1] = lvLineY1 + (lvLineY2 - lvLineY1) / 2;
        stoneButtonCoords[17][0] = lvLineX2; stoneButtonCoords[17][1] = lvLineY2;

        //lh = left horizontal
        int lhLineX1 = s1X;
        int lhLineY1 = (int)(s1Height / 2) + s1Y;
        int lhLineX2 = s3X;
        int lhLineY2 = lhLineY1;
        g2.drawLine(lhLineX1, lhLineY1, lhLineX2, lhLineY2);
        stoneButtonCoords[18][0] = lhLineX1; stoneButtonCoords[18][1] = lhLineY1;
        stoneButtonCoords[19][0] = s2X; stoneButtonCoords[19][1] = lhLineY1;
        stoneButtonCoords[20][0] = lhLineX2; stoneButtonCoords[20][1] = lhLineY2;

        //rh = right horizontal
        int rhLineX1 = s1X + s1Width;
        int rhLineY1 = lhLineY1;
        int rhLineX2 = s3X + s3Width;
        int rhLineY2 = lhLineY1;
        g2.drawLine(rhLineX1, rhLineY1, rhLineX2, rhLineY2);
        stoneButtonCoords[21][0] = rhLineX1; stoneButtonCoords[21][1] = rhLineY1;
        stoneButtonCoords[22][0] = s2X + s2Width; stoneButtonCoords[22][1] = rhLineY1;
        stoneButtonCoords[23][0] = rhLineX2; stoneButtonCoords[23][1] = rhLineY2;

        // ensures stoneButton constructor is only called, if there are no stoneButtons yet
        if(stoneButtonList == null) {

            System.out.println("Initiating StoneButtons...");

            stoneButtonList = new StoneButton[24];

            makeStoneButtons(stoneButtonCoords);

            try {
                MillClient client = new MillClient("192.168.0.122", this.stoneButtonList, this.inGameWindow, this.inGameWindow.player1);
                this.inGameWindow.GameWindow.millClient = client;
                this.inGameWindow.GameWindow.millClientThread = new Thread(client);
                this.inGameWindow.GameWindow.millClientThread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            int x = 0;
            for (StoneButton item : stoneButtonList) {
                item.setBounds(stoneButtonCoords[x][0] - 25, stoneButtonCoords[x][1] - 25, 50, 50);
                x++;
            }
        }
    }

    public void makeStoneButtons(int[][] stoneButtonCoords) {
        int id = 0;

        for (int[] item : stoneButtonCoords) {
            StoneButton temp = new StoneButton(item, id, this);
            stoneButtonList[id] = temp;
            id++;
            this.add(temp);
        }
    }
}

