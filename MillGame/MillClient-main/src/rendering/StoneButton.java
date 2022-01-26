package rendering;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class StoneButton extends JButton implements Serializable {
    public int buttonID;
    public int[] buttonCoords = new int[2];
    private String buttonColour;
    public Player player = null;

    private final JPanelBoard board;
    private final BufferedImage img = null;

    StoneButton(int[] coordinates, int id, JPanelBoard board) {
        super();

        this.buttonCoords[0] = coordinates[0];
        this.buttonCoords[1] = coordinates[1];
        this.buttonID = id;
        this.board = board;

        this.setContentAreaFilled(false);
        this.setBackground(new Color(129, 129, 129));
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setBounds(this.buttonCoords[0] - 25, this.buttonCoords[1] - 25, 50, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isArmed()) {
            g2.setColor(Color.lightGray);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

        super.paintComponent(g2);
    }
}
