package shapes;

import java.awt.geom.Ellipse2D;

public class GOval extends GShape{

    public GOval() {
        this.shape = new Ellipse2D.Double();

    }

    public void setLocation0(int x, int y) {
        Ellipse2D r = (Ellipse2D) shape;
        r.setFrame(x,y,0,0);
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void setLocation1(int x, int y, boolean isShift) {
        if (isShift) {
            int dx = x - this.x0;
            int dy = y - this.y0;
            int size = Math.max(Math.abs(dx), Math.abs(dy));
            x = this.x0 + (dx > 0 ? size : -size);
            y = this.y0 + (dy > 0 ? size : -size);
        }

        Ellipse2D o = (Ellipse2D) shape;
        int newX = Math.min(this.x0, x);
        int newY = Math.min(this.y0, y);
        int w = Math.abs(x - this.x0);
        int h = Math.abs(y - this.y0);

        o.setFrame(newX, newY, w, h);
    }

    public void translate(int dx, int dy) {
        Ellipse2D r = (Ellipse2D) shape;
        r.setFrame(r.getX()+dx, r.getY()+dy, r.getWidth(), r.getHeight());
        this.rotCx += dx;
        this.rotCy += dy;

    }
    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        Ellipse2D r = (Ellipse2D) shape;

        double newX1 = tx + (r.getX() - tx) * sx;
        double newY1 = ty + (r.getY() - ty) * sy;
        double newX2 = tx + (r.getX() + r.getWidth() - tx) * sx;
        double newY2 = ty + (r.getY() + r.getHeight() - ty) * sy;

        r.setFrame(
                Math.min(newX1, newX2), Math.min(newY1, newY2),
                Math.abs(newX2 - newX1), Math.abs(newY2 - newY1)
        );
    }

    @Override
    public void rotate(double dAngle, double cx, double cy) {
        this.angle += dAngle;
        this.rotCx = cx;
        this.rotCy = cy;
    }



}
