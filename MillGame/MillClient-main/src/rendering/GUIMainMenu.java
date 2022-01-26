package rendering;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GUIMainMenu extends JPanel {
    private final Game GameWindow;

    private static final Color BackgroundColor = new Color(255,255,255);
    private static final Color MenuButtonsBackgroundColor = new Color(255, 255, 255);

    private final JPanel MenuLeftMenu;
    private final JPanel MenuButtons;
    private final MenuJButton MenuButtonNewGame;
    private final MenuJButton MenuButtonContinue;
    private final MenuJButton MenuButtonSettings;
    private final MenuJButton MenuButtonQuitGame;

    private JTextField MenuPreGamePlayer1;

    // OFFLINE: player 2 name
//    private JTextField MenuPreGamePlayer2;

    private JButton MenuPreGameButtonStartGame;
    private final JPanel MenuPreGame;
    private final JPanel MenuPopUps;
    private final JPanel MenuPopUpsBackground;

    private final JPanel MenuSettingsMenu;

    private Boolean newGameShown = true;
    private Boolean settingsShown = false;

    private Boolean MenuPreGameField1Filled = false;

    // OFFLINE: player 2 name
//    private Boolean MenuPreGameField2Filled = false;

    public GUIMainMenu(Game GameWindow) {
        this.GameWindow = GameWindow;
        this.GameWindow.setSize(1280,  720);

        // buttons
        this.MenuButtonNewGame = new MenuJButton(this.GameWindow,"New Game");
        this.MenuButtonContinue = new MenuJButton(this.GameWindow," Continue  ");
        this.MenuButtonSettings = new MenuJButton(this.GameWindow,"  Settings  ");
        this.MenuButtonQuitGame = new MenuJButton(this.GameWindow,"Quit Game");

        // button panel
        this.MenuButtons = new JPanel();
        this.MenuButtons.setLayout(new BoxLayout(MenuButtons, BoxLayout.Y_AXIS));
        this.MenuButtons.setBackground(MenuButtonsBackgroundColor);
        this.MenuButtons.add(this.MenuButtonNewGame);
        this.MenuButtons.add(Box.createRigidArea(new Dimension(0,10)));
        this.MenuButtons.add(this.MenuButtonContinue);
        this.MenuButtons.add(Box.createRigidArea(new Dimension(0,10)));
        this.MenuButtons.add(this.MenuButtonSettings);
        this.MenuButtons.add(Box.createRigidArea(new Dimension(0,10)));
        this.MenuButtons.add(this.MenuButtonQuitGame);

        // left menu
        this.MenuLeftMenu = new JPanel(new BorderLayout(10,20));
        this.MenuLeftMenu.setBorder(BorderFactory.createMatteBorder(10, 20, 10, 20, MenuButtonsBackgroundColor));
        this.MenuLeftMenu.add(this.MenuButtons);

        // new game menu
        this.MenuPreGame = setupPreGameMenu();

        // settings menu
        this.MenuSettingsMenu = setupSettingsPanel();

        // center popup menu
        this.MenuPopUps = new JPanel();
        this.MenuPopUps.setLayout(new BorderLayout(0, 10));
        this.MenuPopUpsBackground = new JPanelWithBackground("img/Background_2.jpg");
        this.MenuPopUps.add(MenuPopUpsBackground, BorderLayout.CENTER);
        this.MenuPopUpsBackground.add(MenuPreGame);
        this.MenuPopUpsBackground.add(MenuSettingsMenu);

        // MainMenuPanel
        super.setBackground((BackgroundColor));
        super.setLayout(new BorderLayout(0,20));
        super.add(this.MenuLeftMenu, BorderLayout.WEST);
        super.add(this.MenuPopUps, BorderLayout.CENTER);

        this.validate();

        // adding actionListeners
        setupButtonsActionListeners(GameWindow);
    }

    private JPanel setupPreGameMenu() {
        // pre game menu: text
        JLabel MenuPreGameText = new JLabel("New Game");
        MenuPreGameText.setFont(new Font("Verdana", Font.PLAIN, 25));
        MenuPreGameText.setPreferredSize(new Dimension(250,40));

        JLabel MenuPreGameLabelP1 = new JLabel("Player name:");
        MenuPreGameLabelP1.setFont(new Font("Verdana", Font.PLAIN, 15));

        // OFFLINE: player 2 name
//        JLabel MenuPreGameLabelP2 = new JLabel("Player two:");
//        MenuPreGameLabelP2.setFont(new Font("Verdana", Font.PLAIN, 15));

        // pre game menu: input fields
        this.MenuPreGamePlayer1 = new JTextField("");
        this.MenuPreGamePlayer1.setFont(new Font("Verdana", Font.PLAIN, 15));

        // OFFLINE: player 2 name
//        this.MenuPreGamePlayer2 = new JTextField("");
//        this.MenuPreGamePlayer2.setFont(new Font("Verdana", Font.PLAIN, 15));

        // pre game menu: button
        this.MenuPreGameButtonStartGame = new JButton("Start game");
        this.MenuPreGameButtonStartGame.setEnabled(false);

        // pre game menu
        JPanel MenuPreGame = new JPanel();
        MenuPreGame.setLayout(new BoxLayout(MenuPreGame, BoxLayout.Y_AXIS));
        MenuPreGame.setOpaque(false);
        MenuPreGame.setVisible(newGameShown);
        MenuPreGame.add(MenuPreGameText);
        MenuPreGame.add(Box.createRigidArea(new Dimension(0,30)));
        MenuPreGame.add(MenuPreGameLabelP1);
        MenuPreGame.add(MenuPreGamePlayer1);
        MenuPreGame.add(Box.createRigidArea(new Dimension(0,20)));

        // OFFLINE: player 2 name
//        MenuPreGame.add(MenuPreGameLabelP2);
//        MenuPreGame.add(MenuPreGamePlayer2);
//        MenuPreGame.add(Box.createRigidArea(new Dimension(0,40)));

        MenuPreGame.add(MenuPreGameButtonStartGame);

        return MenuPreGame;
    }

    private JPanel setupSettingsPanel() {
        // settings: text
        JLabel settingsText = new JLabel("Settings");
        settingsText.setVerticalAlignment(JLabel.TOP);
        settingsText.setFont(new Font("Verdana", Font.PLAIN, 25));
        settingsText.setPreferredSize(new Dimension(250,40));

        // settings: resolution
        String[] resolutionOptions_asString = {"1280 x 720", "1920 x 1080", "2560 x 1440", "3840 x 2160"};
        int[][] resolutionOptions = {{1280, 720}, {1920, 1080}, {2560, 1440}, {3840, 2160}};

        // settings: resolution: dropdown render
        JComboBox<String> resolutionPicker = new JComboBox<>(resolutionOptions_asString);

        // settings: GUISize (Buttons, etc.)
        String[] guiSizeOptions_asString = {"small", "medium", "large", "extra large"};
        int[] guiSizeOptions = {1, 2, 3, 4};

        // settings: GUISize: dropdown render
        JComboBox<String> guiSizePicker = new JComboBox<>(guiSizeOptions_asString);

        // settings
        JPanel settingsPanel = new JPanel();
        settingsPanel.setOpaque(false);
        settingsPanel.setVisible(settingsShown);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.add(settingsText);
        settingsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        settingsPanel.add(resolutionPicker);
        settingsPanel.add(Box.createRigidArea(new Dimension(0,40)));
        settingsPanel.add(guiSizePicker);

        // settings: resolution: dropdown actionListener
        resolutionPicker.addActionListener(e1 -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            int index = resolutionPicker.getSelectedIndex();

            // resizing window
            GameWindow.setSize(resolutionOptions[index][0], resolutionOptions[index][1]);
            if(GameWindow.inGame != null) {
                GameWindow.mainMenu.setSize(resolutionOptions[index][0], resolutionOptions[index][1]);
                GameWindow.inGame.setSize(resolutionOptions[index][0], resolutionOptions[index][1]);
            }

            // centering frame
            Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
            int scrnWidth = GameWindow.getWidth();
            int scrnHeight = GameWindow.getHeight();
            int x = (scrnSize.width - scrnWidth) / 2;
            int y = (scrnSize.height - scrnHeight) / 2;

            GameWindow.setLocation(x, y);
        });

        // settings: guiSize: dropdown actionListener
        guiSizePicker.addActionListener(e1 -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            int index = guiSizePicker.getSelectedIndex();

            // resizing buttons
            for (Component item : MenuButtons.getComponents()) {
                if(item.getClass() == MenuButtonNewGame.getClass())
                    ((MenuJButton) item).setRelativeSize(GameWindow, guiSizeOptions[index]);
            }
        });

        return settingsPanel;
    }

    private void setupButtonsActionListeners(Game GameWindow) {
        // new game button
        MenuButtonNewGame.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            if(!newGameShown) {
                this.MenuSettingsMenu.setVisible(false);
                this.settingsShown = false;

                this.MenuPreGame.setVisible(true);
                this.newGameShown = true;
            } else {
                newGameShown = false;
            }
        });

        // continue button
        MenuButtonContinue.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            GameWindow.changeView();
        });

        // settings button
        MenuButtonSettings.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            if(!settingsShown) {
                this.MenuPreGame.setVisible(false);
                this.newGameShown = false;

                this.MenuSettingsMenu.setVisible(true);
                this.settingsShown = true;
            } else {
                settingsShown = false;
            }
        });

        // quit button
        MenuButtonQuitGame.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            //calling an optionDialog to confirm the players decision to quit the game
            if(JOptionPane.showOptionDialog(GameWindow, "Do you want to leave the game?", "Exit game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "Yes") == 0) {
                this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

                GameWindow.dispose();
            } else {
                this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);
            }
        });

        MenuPreGameButtonStartGame.addActionListener(e -> {
            this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

            Player player1 = new Player(MenuPreGamePlayer1.getText(), new Color(255,255,255));

            // OFFLINE: player 2 name
//            Player player2 = new Player(MenuPreGamePlayer2.getText(), new Color(0,0,0));

            GameWindow.newGameWindow(player1);
        });

        // OFFLINE: checking for different player names
        MenuPreGamePlayer1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                nullCheck();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                nullCheck();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                nullCheck();
            }

            public void nullCheck() {
                if(!Objects.equals(MenuPreGamePlayer1.getText(), "") ) {
                    MenuPreGameField1Filled = true;
                    MenuPreGameButtonStartGame.setEnabled(true);
                } else if(!Objects.equals(MenuPreGamePlayer1.getText(), "")) {
                    MenuPreGameField1Filled = true;
                } else if(Objects.equals(MenuPreGamePlayer1.getText(), "")) {
                    MenuPreGameField1Filled = false;
                    MenuPreGameButtonStartGame.setEnabled(false);
                }
            }
        });

        // OFFLINE: checking for different player names
//        MenuPreGamePlayer2.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                nullCheck();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                nullCheck();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                nullCheck();
//            }
//
//            public void nullCheck() {
//                if(!Objects.equals(MenuPreGamePlayer2.getText(), "") && MenuPreGameField1Filled) {
//                    MenuPreGameField2Filled = true;
//                    MenuPreGameButtonStartGame.setEnabled(true);
//                } else if(!Objects.equals(MenuPreGamePlayer2.getText(), "")) {
//                    MenuPreGameField2Filled = true;
//                } else if(Objects.equals(MenuPreGamePlayer2.getText(), "")) {
//                    MenuPreGameButtonStartGame.setEnabled(false);
//                    MenuPreGameField2Filled = false;
//                }
//                if(Objects.equals(MenuPreGamePlayer2.getText(), MenuPreGamePlayer1.getText())) {
//                    MenuPreGameButtonStartGame.setEnabled(false);
//                }
//            }
//        });
    }
}



// background panel from given url
class JPanelWithBackground extends JPanel {
    private Image backgroundImage;

    //tries to read an image file via ImageIO.read() and catches error
    public JPanelWithBackground(String fileName) {
        try {
//            backgroundImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(fileName));
            System.out.println(fileName);
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName)));
        } catch(Exception e){
            System.out.println(e);
        }
        setLayout(new GridBagLayout());
    }

    //painting the Graphics component g to the JPanel and draw on it
    public void paintComponent(Graphics g) {
        Graphics2D comp2D = (Graphics2D)g;
        comp2D.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
