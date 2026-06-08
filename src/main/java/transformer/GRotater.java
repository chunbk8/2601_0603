package transformer;

import shapes.GShape;

import java.awt.*;

public class GRotater extends GTransformer{
    private double startAngle;
    private double cx, cy;

    public GRotater(GShape shape) {
        super(shape);
    }

    @Override
    public void start(int x, int y) {
        Rectangle r = shape.getShape().getBounds();
        this.cx = r.getCenterX();
        this.cy = r.getCenterY();
        this.startAngle = Math.atan2(y - cy, x - cx);
    }

    @Override
    public void keep(int x, int y, boolean isShift) {
        double currentAngle = Math.atan2(y - cy, x - cx);
        double dAngle = currentAngle - this.startAngle;

        shape.rotate(dAngle, cx, cy);

        this.startAngle = currentAngle;

    }

    @Override
    public void finish(int x, int y,  boolean isShift) {

    }
}
