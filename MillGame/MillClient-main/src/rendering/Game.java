package rendering;

import TCP.MillClient;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

public class Game extends JFrame {

    public GUIMainMenu mainMenu;
    public GUIInGame inGame;
    public MillClient millClient;
    public Thread millClientThread;

    AudioFormat audioFormat;
    AudioInputStream audioInputStream;

    public Game() {

        // main menu
        this.mainMenu = new GUIMainMenu(this);

        // main frame
        this.add(mainMenu);

        this.setResizable(false);

        //setting up the event handlers
        WindowEventHandler HandlerWindow = new WindowEventHandler(this);
        addWindowListener(HandlerWindow);
    }

    // swapping between main menu and in game
    public void changeView() {
        if(this.inGame != null) {
            if (this.mainMenu.isVisible()) {
                this.mainMenu.setVisible(false);
                this.inGame.setVisible(true);
            } else {
                this.mainMenu.setVisible(true);
                this.inGame.setVisible(false);
            }
            this.revalidate();
            this.repaint();
        }
    }

    // new game window
    public void newGameWindow(Player player1) {
        if(this.inGame == null) {

            this.inGame = new GUIInGame(this, player1);
            this.add(inGame);
            changeView();

        } else {
            JOptionPane.showMessageDialog(this, "You can't start a new game while a game is running. Exit your game first");
        }
    }

    public void GameQuitGame() {
        changeView();
        this.inGame = null;
        this.millClient.quitGame();
    }

    public void playButtonClick(String fileName, Float volume) {
        try{
            audioInputStream = AudioSystem.
                    getAudioInputStream(Objects.requireNonNull(getClass().getResource(fileName)));
            audioFormat = audioInputStream.getFormat();

//            System.out.println(audioFormat);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            Arrays.toString(AudioSystem.getMixerInfo());

            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-volume);

            clip.start();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {

        //look and feel
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            Logger.getLogger(rendering.GUIMainMenu.class.getName()).log(Level.SEVERE, null, e);
        }
        for (UIManager.LookAndFeelInfo x: UIManager.getInstalledLookAndFeels()) {
            System.out.println(x.getClassName());
        }

        Game game = new Game();

        // centering frame
        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        int scrnWidth = game.getWidth();
        int scrnHeight = game.getHeight();
        int x = (scrnSize.width - scrnWidth) / 2;
        int y = (scrnSize.height - scrnHeight) / 2;

        game.setLocation(x, y);
        game.setVisible(true);
        game.validate();
    }
}
