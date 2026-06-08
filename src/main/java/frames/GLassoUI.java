package frames;

import java.awt.*;

public class GLassoUI {
    private Point startPoint;
    private Rectangle lassoRect;

    public GLassoUI() {
        this.lassoRect = new Rectangle(0, 0, 0, 0);
    }

    // 올가미 시작
    public void startDrawing(int x, int y) {
        this.startPoint = new Point(x, y);
        this.lassoRect.setBounds(x, y, 0, 0);
    }

    // 마우스 드래그 중 올가미 크기 갱신
    public void keepDrawing(int x, int y) {
        if (startPoint == null) return;
        int w = Math.abs(x - startPoint.x);
        int h = Math.abs(y - startPoint.y);
        int rx = Math.min(x, startPoint.x);
        int ry = Math.min(y, startPoint.y);
        this.lassoRect.setBounds(rx, ry, w, h);
    }

    // 올가미 종료 (초기화)
    public void stopDrawing() {
        this.startPoint = null;
        this.lassoRect.setBounds(0, 0, 0, 0);
    }

    // 최종적으로 만들어진 올가미의 영역 반환 (도형 교차 검사용)
    public Rectangle getBounds() {
        return this.lassoRect;
    }

    // 자기 자신을 렌더링하는 책임
    public void draw(Graphics2D g) {
        if (lassoRect == null || lassoRect.isEmpty()) return;

        // 반투명 파란색 채우기
        g.setColor(new Color(0, 120, 215, 50));
        g.fill(lassoRect);

        // 파란색 점선 테두리 그리기
        g.setColor(new Color(0, 120, 215));
        Stroke oldStroke = g.getStroke();
        Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5.0f}, 0);
        g.setStroke(dashed);
        g.draw(lassoRect);

        g.setStroke(oldStroke); // 패널의 원래 선 스타일이 망가지지 않도록 복구
    }
}