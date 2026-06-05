package shape;

import java.awt.*;
import java.util.Arrays;

public class GPolygon extends GShape {
    private Polygon polygon;

    public GPolygon() {
        this.polygon = new Polygon();
        this.shape = this.polygon;
    }

    @Override
    public void setLocation0(int x, int y) {
        this.polygon.reset();
        this.polygon.addPoint(x, y); // 고정된 첫 번째 꼭짓점
        this.polygon.addPoint(x, y); // 마우스를 따라 움직일 두 번째 꼭짓점
        this.polygon.invalidate();
    }

    @Override
    public void setLocation1(int x, int y) {
        if (this.polygon.npoints > 0) {
            // 현재 드래그 중인 마지막 점의 위치를 마우스 좌표로 계속 갱신합니다.
            this.polygon.xpoints[this.polygon.npoints - 1] = x;
            this.polygon.ypoints[this.polygon.npoints - 1] = y;

            this.polygon.invalidate();
        }
    }

    @Override
    public void addPoint(int x, int y) {

        this.polygon.addPoint(x, y);
        this.polygon.invalidate();
    }

    /*@Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }*/

    @Override
    public GShape clone() {
        GPolygon cloned = (GPolygon) super.clone();
        // 배열 복사를 통해 원본과 완전히 독립된 새로운 Polygon 객체 생성
        cloned.polygon = new Polygon(
                Arrays.copyOf(this.polygon.xpoints, this.polygon.npoints),
                Arrays.copyOf(this.polygon.ypoints, this.polygon.npoints),
                this.polygon.npoints
        );
        cloned.shape = cloned.polygon;
        return cloned;
    }

    @Override
    public void translate(int dx, int dy) {
        for (int i = 0; i < polygon.npoints; i++) {
            polygon.xpoints[i] += dx;
            polygon.ypoints[i] += dy;
        }
        polygon.invalidate();
    }
    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        for (int i = 0; i < polygon.npoints; i++) {
            polygon.xpoints[i] = (int) Math.round(tx + (polygon.xpoints[i] - tx) * sx);
            polygon.ypoints[i] = (int) Math.round(ty + (polygon.ypoints[i] - ty) * sy);
        }
        polygon.invalidate();
    }

    @Override
    public void rotate(double dAngle, double cx, double cy) {
        double cos = Math.cos(dAngle);
        double sin = Math.sin(dAngle);

        for (int i = 0; i < polygon.npoints; i++) {
            double dx = polygon.xpoints[i] - cx;
            double dy = polygon.ypoints[i] - cy;

            polygon.xpoints[i] = (int) Math.round(cx + dx * cos - dy * sin);
            polygon.ypoints[i] = (int) Math.round(cy + dx * sin + dy * cos);
        }
        polygon.invalidate();
    }
}