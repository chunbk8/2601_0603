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
        if (isShift) {
            int dx = x - this.x0;
            int dy = y - this.y0;

            // 🌟 선은 45도, 수직, 수평을 스스로 판단합니다.
            if (Math.abs(dx) > Math.abs(dy) * 2) {
                y = this.y0; // 수평선
            } else if (Math.abs(dy) > Math.abs(dx) * 2) {
                x = this.x0; // 수직선
            } else {
                int size = Math.max(Math.abs(dx), Math.abs(dy));
                x = this.x0 + (dx > 0 ? size : -size);
                y = this.y0 + (dy > 0 ? size : -size); // 45도 사선
            }
        }

        Line2D l = (Line2D) shape;
        l.setLine(this.x0, this.y0, x, y);
    }

    @Override
    public GShape clone() {
        GLine cloned = (GLine) super.clone();
        // GShape가 해주지 못하는 Line2D 알맹이 객체의 깊은 복사(Deep Copy)를 직접 수행합니다!
        cloned.shape = (Shape) ((Line2D.Double) this.shape).clone();
        return cloned;
    }
    @Override
    public void translate(int dx, int dy) {
        Line2D l = (Line2D) shape;

        // 1. 선의 두 끝점(x1, y1)과 (x2, y2)를 모두 가져와서 각각 마우스 이동 거리(dx, dy)만큼 밀어줍니다.
        l.setLine(
                l.getX1() + dx, l.getY1() + dy,
                l.getX2() + dx, l.getY2() + dy
        );

        // 2. 🌟 (매우 중요) 선이 이동한 만큼, 회전의 기준이 되는 중심축도 함께 이동시켜야
        // 나중에 회전(Rotate) 후 이동할 때 사선으로 미끄러지는 버그가 발생하지 않습니다!
        this.rotCx += dx;
        this.rotCy += dy;
    }

    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        Line2D l = (Line2D) shape;

        // 두 끝점을 고정점(tx, ty) 기준으로 배율(sx, sy)만큼 밀어냅니다.
        double newX1 = tx + (l.getX1() - tx) * sx;
        double newY1 = ty + (l.getY1() - ty) * sy;
        double newX2 = tx + (l.getX2() - tx) * sx;
        double newY2 = ty + (l.getY2() - ty) * sy;

        l.setLine(newX1, newY1, newX2, newY2);

        // 🌟 크기 조절 시 회전 중심축 동기화
        this.rotCx = tx + (this.rotCx - tx) * sx;
        this.rotCy = ty + (this.rotCy - ty) * sy;
    }

    @Override
    public void rotate(double dAngle, double cx, double cy) {
        // GRectangle, GOval과 마찬가지로 각도만 부모에게 넘겨주고,
        // 실제 화면에 그릴 때는 GShape의 draw()가 캔버스를 회전시켜서 그려줍니다.
        this.angle += dAngle;
        this.rotCx = cx;
        this.rotCy = cy;
    }

}
