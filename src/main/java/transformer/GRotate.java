package transformer;

import shape.GShape;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GRotate extends GTransformer{
    private double startAngle;
    private Point center;

    public GRotate(GShape shape) {
        super(shape);
    }

    @Override
    public void start(int x, int y) {
        Rectangle r = shape.getAffineTransform().createTransformedShape(shape.getShape()).getBounds();
        this.center = new Point((int)r.getCenterX(), (int)r.getCenterY());
        this.startAngle = Math.atan2(y-center.y, x- center.x);
    }

    @Override
    public void keep(int x, int y) {
        double currentAngle = Math.atan2(y-center.y, x-center.x);
        double dAngle = currentAngle = startAngle;
        AffineTransform af = shape.getAffineTransform();
        af.rotate(dAngle, center.x,center.y);

        this.startAngle = currentAngle;

    }

    @Override
    public void finish(int x, int y) {

    }
}
