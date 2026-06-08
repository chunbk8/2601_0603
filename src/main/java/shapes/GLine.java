package shapes;

import java.awt.*;
import java.awt.geom.Line2D;

public class GLine extends GShape {
    public GLine() {

        this.shape = new Line2D.Double();
    }

    @Override
    public void setLocation0(int x, int y) {
        Line2D l = (Line2D) shape;

        l.setLine(x, y, x, y);


        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void setLocation1(int x, int y, boolean isShift) {
        if (isShift) { //45도씩 보정
            int dx = x - this.x0;
            int dy = y - this.y0;
            if (Math.abs(dx) > Math.abs(dy) * 2) {
                y = this.y0;
            } else if (Math.abs(dy) > Math.abs(dx) * 2) {
                x = this.x0;
            } else {
                int size = Math.max(Math.abs(dx), Math.abs(dy));
                x = this.x0 + (dx > 0 ? size : -size);
                y = this.y0 + (dy > 0 ? size : -size);
            }
        }

        Line2D l = (Line2D) shape;
        l.setLine(this.x0, this.y0, x, y);
    }

    @Override
    public GShape clone() {
        GLine cloned = (GLine) super.clone();
        cloned.shape = (Shape) ((Line2D.Double) this.shape).clone();
        return cloned;
    }
    @Override
    public void translate(int dx, int dy) {
        Line2D l = (Line2D) shape;
        l.setLine(
                l.getX1() + dx, l.getY1() + dy,
                l.getX2() + dx, l.getY2() + dy
        );
        this.rotCx += dx;
        this.rotCy += dy;
    }

    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        Line2D l = (Line2D) shape;

        double newX1 = tx + (l.getX1() - tx) * sx;
        double newY1 = ty + (l.getY1() - ty) * sy;
        double newX2 = tx + (l.getX2() - tx) * sx;
        double newY2 = ty + (l.getY2() - ty) * sy;

        l.setLine(newX1, newY1, newX2, newY2);

        this.rotCx = tx + (this.rotCx - tx) * sx;
        this.rotCy = ty + (this.rotCy - ty) * sy;
    }

    @Override
    public void rotate(double dAngle, double cx, double cy) {
        this.angle += dAngle;
        this.rotCx = cx;
        this.rotCy = cy;
    }

}
