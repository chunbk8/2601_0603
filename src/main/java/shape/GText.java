package shape;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class GText extends GShape {
    private String text;
    private int startX, startY; // 드래그 시작점을 기억할 변수

    // 드래그해서 생성할 때 사용할 기본 생성자
    public GText() {
        super();
        this.shape = new Rectangle(0, 0, 0, 0);
        this.text = "";
    }

    // 텍스트 Getter / Setter
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    // =========================================================
    // 🌟 1. 영역(설계도) 생성: 마우스 누를 때 (시작점 기억)
    // =========================================================
    @Override
    public void setLocation0(int x, int y) {
        this.startX = x;
        this.startY = y;
        this.shape = new Rectangle(x, y, 0, 0);
    }

    // =========================================================
    // 🌟 2. 영역(설계도) 갱신: 마우스 드래그할 때 (크기 조절)
    // =========================================================
    @Override
    public void setLocation1(int x, int y) {
        int currentX = Math.min(startX, x);
        int currentY = Math.min(startY, y);
        int width = Math.abs(startX - x);
        int height = Math.abs(startY - y);
        this.shape = new Rectangle(currentX, currentY, width, height);
    }

    // =========================================================
    // 🌟 3. 화면에 그리기 (Renderer)
    // =========================================================
    @Override
    public void draw(Graphics2D g) {
        g.rotate(this.angle, this.rotCx, this.rotCy);
        Rectangle bounds = this.shape.getBounds();

        // [상황 A] 글자가 비어있을 때 (드래그 중이거나 입력 대기 중)
        if (this.text == null || this.text.trim().isEmpty()) {
            if (this.isSelected) {
                g.setColor(global.GConstants.EColor.eBlack.getColor());
                g.setStroke(new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_MITER, 1.0f, new float[]{5.0f}, 0.0f));
                g.draw(this.shape);
                this.drawAnchors(g);
            }
            g.rotate(-this.angle, this.rotCx, this.rotCy);
            return; // 글자가 없으니 여기서 그리기 종료
        }

        // [상황 B] 글자가 있을 때
        // 1. 배경색 (투명이 아닐 경우)
        if (!this.fillColor.equals(global.GConstants.EColor.eTransparent.getColor())) {
            g.setColor(this.fillColor);
            g.fill(this.shape);
        }

        // 2. 텍스트 그리기 (상자 높이에 맞춰 폰트 크기 반응형 조절)
        g.setColor(this.lineColor);
        int fontSize = Math.max(10, bounds.height - 10);
        g.setFont(new Font("맑은 고딕", Font.BOLD, fontSize));

        FontMetrics fm = g.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();

        g.drawString(this.text, textX, textY);

        // 3. 선택 시 테두리와 앵커 그리기
        if (this.isSelected) {
            g.setColor(global.GConstants.EColor.eBlack.getColor());
            g.setStroke(new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_MITER, 1.0f, new float[]{5.0f}, 0.0f));
            g.draw(this.shape);
            this.drawAnchors(g);
        }

        g.rotate(-this.angle, this.rotCx, this.rotCy);
    }

    @Override
    public boolean isEditable() {return true;}
}