package shapes;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class GPen extends GShape {
    private ArrayList<Point> points;

    public GPen() {
        this.points = new ArrayList<>();
        this.shape = new Path2D.Double();
    }

    @Override
    public GShape clone() {
        GPen cloned = (GPen) super.clone();

        cloned.points = new ArrayList<>();
        for (Point p : this.points) {
            cloned.points.add(new Point(p.x, p.y));
        }

        cloned.shape = (Shape) ((Path2D.Double) this.shape).clone();
        return cloned;
    }

    private void updatePath() {
        Path2D.Double path = new Path2D.Double();
        if (!points.isEmpty()) {
            path.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);
            }
        }
        this.shape = path;
    }

    @Override
    public void setLocation0(int x, int y) {
        this.points.clear();
        this.points.add(new Point(x, y));

        this.x0 = x;
        this.y0 = y;

        updatePath();
    }

    @Override
    public void setLocation1(int x, int y, boolean isShift) {
        this.points.add(new Point(x, y));
        updatePath();
    }


    @Override
    public void translate(int dx, int dy) {
        for (Point p : points) {
            p.x += dx;
            p.y += dy;
        }
        updatePath();

        this.rotCx += dx;
        this.rotCy += dy;
    }

}