package shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class GImage extends GShape {
    private ImageIcon imageIcon; // 직렬화(저장)를 지원하는 이미지 객체

    public GImage(String filePath, int x, int y) {
        this.imageIcon = new ImageIcon(filePath);
        // 원본 이미지 크기만큼 사각형 영역 잡기
        this.shape = new Rectangle2D.Double(x, y, imageIcon.getIconWidth(), imageIcon.getIconHeight());
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void draw(Graphics2D g) {
        // 1. 회전 적용
        g.rotate(this.angle, this.rotCx, this.rotCy);

        Rectangle2D r = (Rectangle2D) shape;

        // 2. 사각형 영역에 꽉 차게 이미지 그리기 (스케일 조절 대응)
        if (imageIcon != null) {
            g.drawImage(imageIcon.getImage(), (int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight(), null);
        }

        // 3. 선택되었을 때 앵커(윤곽선) 그리기
        if (this.isSelected) {
            this.drawAnchors(g);
        }

        // 4. 회전 복구
        g.rotate(-this.angle, this.rotCx, this.rotCy);
    }

    @Override
    public GShape clone() {
        GImage cloned = (GImage) super.clone();
        cloned.shape = (Shape) ((Rectangle2D.Double) this.shape).clone();
        // ImageIcon은 불변(Immutable) 성격이 강해 그대로 참조해도 무방합니다.
        return cloned;
    }

    // 마우스로 그리는 용도가 아니므로 빈 메서드로 둡니다.
    @Override public void setLocation0(int x, int y) {}
    @Override public void setLocation1(int x, int y) {}

    // GRectangle과 동일하게 동작
    @Override
    public void translate(int dx, int dy) {
        Rectangle2D r = (Rectangle2D) shape;
        r.setFrame(r.getX() + dx, r.getY() + dy, r.getWidth(), r.getHeight());
        this.rotCx += dx;
        this.rotCy += dy;
    }

    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        Rectangle2D r = (Rectangle2D) shape;
        double newX1 = tx + (r.getX() - tx) * sx;
        double newY1 = ty + (r.getY() - ty) * sy;
        double newX2 = tx + (r.getX() + r.getWidth() - tx) * sx;
        double newY2 = ty + (r.getY() + r.getHeight() - ty) * sy;

        r.setFrame(
                Math.min(newX1, newX2), Math.min(newY1, newY2),
                Math.abs(newX2 - newX1), Math.abs(newY2 - newY1)
        );
    }
}