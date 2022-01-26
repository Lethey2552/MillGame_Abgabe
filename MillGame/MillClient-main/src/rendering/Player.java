package rendering;

import java.awt.*;

public class Player{

    String name;
    public Color colour;
    int setzCounter = 9;
    int moveCounter = 2;
    boolean muehlenSpeicher = false;
    boolean flymode = false;
    boolean win = false;

    public Player(String name, Color colour){
        this.name = name;
        this.colour = colour;
    }
}
