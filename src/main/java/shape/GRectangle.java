package shape;

import java.awt.*;

public class GRectangle extends GShape{

    public GRectangle(int x0, int y0, int x1, int y1) {
        super(x0, y0, x1, y1);
    }


    public void draw (Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, x1-x0, y1-y0);
    }

}