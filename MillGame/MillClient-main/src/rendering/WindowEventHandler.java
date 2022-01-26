package rendering;

import TCP.MillClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowEventHandler implements WindowListener {
    public Game GameWindow;

    public WindowEventHandler(Game GameWindow){
        super();
        this.GameWindow = GameWindow;
    }

    public void windowActivated(WindowEvent e) {};

    public void windowClosed(WindowEvent e) {};

    public void windowClosing(WindowEvent e) {
        this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

        if(JOptionPane.showOptionDialog(GameWindow, "Do you want to leave the game?", "Exit game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "Yes") == 0) {
            if(this.GameWindow.inGame != null) {
                this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);

                this.GameWindow.millClient.out.println("QUIT");
            } else {
                this.GameWindow.playButtonClick("/img/buttonClick1.wav", 0.0f);
            }
            GameWindow.dispose();
        }
    };

    public void windowDeactivated(WindowEvent e) {};

    public void windowDeiconified(WindowEvent e) {};

    public void windowIconified(WindowEvent e) {};

    public void windowOpened(WindowEvent e) {};

}
