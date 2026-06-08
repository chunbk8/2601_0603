package shapes;

import java.awt.*;
import java.util.Vector;

public class GGroup extends GShape {
    private Vector<GShape> childShapes;

    public GGroup() {
        this.childShapes = new Vector<>();
        this.shape = new Rectangle(); // 그룹 전체를 감싸는 바운딩 박스
    }

    public void addShape(GShape shape) {
        this.childShapes.add(shape);
        updateBounds();
    }

    public Vector<GShape> getChildShapes() {
        return childShapes;
    }

    // 자식 도형들이 추가/변형될 때마다 그룹 전체의 바운딩 박스를 갱신
    private void updateBounds() {
        if (childShapes.isEmpty()) {
            ((Rectangle) shape).setFrame(0, 0, 0, 0);
            return;
        }
        Rectangle bounds = childShapes.get(0).getShape().getBounds();
        for (int i = 1; i < childShapes.size(); i++) {
            bounds = bounds.union(childShapes.get(i).getShape().getBounds());
        }
        ((Rectangle) shape).setFrame(bounds);
        this.rotCx = bounds.getCenterX();
        this.rotCy = bounds.getCenterY();
    }

    @Override
    public void draw(Graphics2D g) {
        // 자식 도형들 그리기
        for (GShape child : childShapes) {
            boolean wasSelected = child.isSelected();
            if (this.isSelected) child.setSelected(false); // 그룹이 선택되었다면 개별 앵커는 숨김
            child.draw(g);
            child.setSelected(wasSelected);
        }

        // 그룹 자체가 선택되었을 때만 점선 테두리와 그룹 앵커 그리기
        if (this.isSelected) {
            Stroke oldStroke = g.getStroke();
            g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f}, 0.0f));
            g.setColor(Color.GRAY);
            Rectangle r = shape.getBounds();
            g.drawRect(r.x, r.y, r.width, r.height);
            g.setStroke(oldStroke);

            this.drawAnchors(g);
        }
    }

    // 변형 명령이 들어오면 모든 자식에게 일괄 전달합니다! (GTransformer 수정 필요 없음)
    @Override
    public void translate(int dx, int dy) {
        for (GShape child : childShapes) child.translate(dx, dy);
        updateBounds();
    }

    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        for (GShape child : childShapes) child.scale(sx, sy, tx, ty);
        updateBounds();
    }

    @Override
    public void rotate(double dAngle, double cx, double cy) {
        for (GShape child : childShapes) child.rotate(dAngle, cx, cy);
        updateBounds();
    }

    // 스타일 변경도 자식들에게 일괄 적용
    @Override
    public void setStyle(Color lineColor, Color fillColor, int thickness) {
        for (GShape child : childShapes) child.setStyle(lineColor, fillColor, thickness);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        for (GShape child : childShapes) child.setSelected(selected);
    }

    @Override
    public GShape clone() {
        GGroup cloned = (GGroup) super.clone();
        cloned.childShapes = new Vector<>();
        for (GShape child : this.childShapes) {
            cloned.childShapes.add(child.clone());
        }
        cloned.shape = (Shape) ((Rectangle) this.shape).clone();
        return cloned;
    }
}