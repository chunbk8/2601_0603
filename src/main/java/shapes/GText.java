package shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GText extends GShape {
    private String text;

    public GText() {
        this.shape = new Rectangle2D.Double();
        this.text = ""; // 초기 텍스트
    }

    public void setText(String text) { this.text = text; }
    public String getText() { return text; }

    @Override
    public void setLocation0(int x, int y) {
        Rectangle2D r = (Rectangle2D) shape;
        r.setFrame(x, y, 0, 0);
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void setLocation1(int x, int y) {
        Rectangle2D r = (Rectangle2D) shape;
        int newX = Math.min(this.x0, x);
        int newY = Math.min(this.y0, y);
        int w = Math.abs(x - this.x0);
        int h = Math.abs(y - this.y0);
        r.setFrame(newX, newY, w, h);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g); // 부모 클래스의 앵커 및 테두리 렌더링 호출

        if (text != null && !text.isEmpty()) {
            g.setColor(this.textColor);
            g.setFont(new Font("맑은 고딕", Font.PLAIN, 20)); // 폰트 설정
            Rectangle2D r = (Rectangle2D) shape;
            // 사각형 테두리 안쪽 여백을 조금 두고 텍스트 렌더링
            g.drawString(text, (int) r.getX() + 5, (int) r.getY() + 25);
        }
    }

    @Override
    public GShape clone() {
        GText cloned = (GText) super.clone();
        cloned.shape = (Shape) ((Rectangle2D.Double) this.shape).clone();
        cloned.text = this.text;
        return cloned;
    }

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