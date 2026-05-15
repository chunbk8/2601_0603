package shape;

import java.awt.*;

public class GRectangle extends GShape{

    public GRectangle() {
        this.shape = new Rectangle();

    }


    public void draw (Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, x1-x0, y1-y0);
    }

}