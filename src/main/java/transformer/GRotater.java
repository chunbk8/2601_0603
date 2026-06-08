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
        this.startAngle = Math.atan2(y - cy, x - cx); // 최초 클릭 위치의 각도
    }

    @Override
    public void keep(int x, int y, boolean isShift) {
        double currentAngle = Math.atan2(y - cy, x - cx);
        double dAngle = currentAngle - this.startAngle; // 움직인 만큼의 각도 차이

        // 1. 도형에게 이 각도만큼 돌아가라고 명령!
        shape.rotate(dAngle, cx, cy);

        // 2. 다음번 계산을 위해 현재 각도로 갱신
        this.startAngle = currentAngle;

    }

    @Override
    public void finish(int x, int y,  boolean isShift) {

    }
}
