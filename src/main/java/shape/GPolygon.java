package shape;

import java.awt.*;

public class GPolygon extends GShape {
    private Polygon polygon;

    public GPolygon() {
        this.polygon = new Polygon();
        this.shape = this.polygon;
    }

    @Override
    public void setLocation0(int x, int y) {
        polygon.addPoint(x, y);
        polygon.addPoint(x, y);
    }

    @Override
    public void setLocation1(int x, int y) {

        polygon.xpoints[polygon.npoints - 1] = x;
        polygon.ypoints[polygon.npoints - 1] = y;
    }

    @Override
    public void addPoint(int x, int y) {
        polygon.addPoint(x, y);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }

    @Override
    public GShape clone() {
        GPolygon clone = (GPolygon) super.clone();
        clone.polygon = new Polygon(this.polygon.xpoints, this.polygon.ypoints, this.polygon.npoints);
        clone.shape = clone.polygon;
        return clone;
    }

    @Override
    public void translate(int dx, int dy) {
        // 다각형을 구성하는 모든 x, y 좌표에 이동한 거리(dx, dy)를 더해줍니다.
        for (int i = 0; i < polygon.npoints; i++) {
            polygon.xpoints[i] = polygon.xpoints[i] + dx;
            polygon.ypoints[i] = polygon.ypoints[i] + dy;
        }
        // 변경된 점들을 바탕으로 Polygon의 내부 경계(Bounds)를 재계산하도록 합니다.
        polygon.invalidate();
    }
}