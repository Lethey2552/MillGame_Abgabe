package rendering;

import javax.swing.*;
import java.awt.*;

public class MenuJButton extends JButton {
    Game GameWindow;

    public MenuJButton(Game GameWindow, String name) {
        super(name);
        this.GameWindow = GameWindow;

        setRelativeSize(GameWindow, 2);
    }

    public void setRelativeSize(JFrame GameWindow, int size){
        int width = GameWindow.getWidth() / 12;
        int height = GameWindow.getHeight() / 20;

        switch(size) {
            case (1) -> {
                width = GameWindow.getWidth() / 16;
                height = GameWindow.getHeight() / 24;
            }
            case (2) -> {
                width = GameWindow.getWidth() / 12;
                height = GameWindow.getHeight() / 20;
            }
            case (3) -> {
                width = GameWindow.getWidth() / 10;
                height = GameWindow.getHeight() / 18;
            }
            case (4) -> {
                width = GameWindow.getWidth() / 8;
                height = GameWindow.getHeight() / 16;
            }
        }
//        System.out.println(width + "    " + height);

        // size
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
    }
}
