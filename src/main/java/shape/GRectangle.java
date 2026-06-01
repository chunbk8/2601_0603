package shape;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GRectangle extends GShape{

    public GRectangle() {
        this.shape = new Rectangle();

    }


    public void setLocation0(int x, int y) {
        Rectangle r = (Rectangle) shape;
        r.setFrame(x,y,0,0);
    }

    public void setLocation1(int x, int y) {
        Rectangle r = (Rectangle) shape;
        double w = x-r.getX(); //현재의 점에서 원점을 뺀 것
        double h = y-r.getY();
        r.setFrame(r.getX(),r.getY(),w,h);
    }

    public void translate(int dx, int dy) {
        Rectangle r = (Rectangle) shape;
        r.setFrame(r.getX()+dx, r.getY()+dy, r.getWidth(), r.getHeight());

    }

}