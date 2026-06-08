package frames;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class GStatusBar extends JPanel {
    private JLabel coordinateLabel;

    public GStatusBar() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        coordinateLabel = new JLabel("X: 0, Y: 0");
        this.add(coordinateLabel);
    }

    public void updateCoordinates(int x, int y) {
        coordinateLabel.setText("X: " + x + ", Y: " + y);
    }
}