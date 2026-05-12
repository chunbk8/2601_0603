package shape;

import java.awt.*;

public class GOval extends GShape{

    public GOval() {

    }


    public void draw (Graphics2D g) {
        Graphics2D g2D = (Graphics2D) g.create();
        g2D.setColor(Color.BLACK);
        g2D.drawOval(x0, y0, x1-x0, y1-y0);
//        g.setColor(Color.BLACK);
//        g.drawOval(x0, y0, x1-x0, y1-y0);
    }

}
