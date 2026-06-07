package shapes;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

public class GShapeList implements Serializable {
    private static final long serialVersionUID = 1L;
    private Vector<GShape> shapes;

    public GShapeList() {
        this.shapes = new Vector<>();
    }

    // 🌟 캡슐화: 데이터에 접근하는 통로를 메서드로 제공
    public void add(GShape shape) {
        shapes.add(shape);
    }

    public void remove(GShape shape) {
        shapes.remove(shape);
    }

    public void clear() {
        shapes.clear();
    }

    public Vector<GShape> getShapes() {
        return shapes;
    }

    // 🌟 Z-Order 및 순회 로직도 여기서 관리
    public void moveToFront(GShape shape) {
        if (shapes.remove(shape)) { // 성공적으로 제거되면
            shapes.add(shape);      // 맨 뒤에 추가 (그리기 순서상 맨 앞)
        }
    }

    public void bringSelectedToFront() {
        for (GShape shape : this.shapes) {
            if (shape.isSelected()) {
                this.moveToFront(shape); // 리스트 순서 변경
                return;
            }
        }
    }

    // GShapeList.java 내부에 추가
    public void sendSelectedToBack() {
        for (GShape shape : this.shapes) { // this.shapes는 도형들이 담긴 리스트
            if (shape.isSelected()) {
                this.sendToBack(shape); // 실제 순서 변경 로직 호출
                return; // 찾았으면 처리하고 종료
            }
        }
    }

    // 순서를 뒤로(리스트 맨 앞) 보내는 저수준 메서드
    public void sendToBack(GShape shape) {
        if (shapes.remove(shape)) { // 성공적으로 제거되면
            shapes.add(0, shape);   // 리스트 맨 앞(인덱스 0)에 추가
        }
    }

    // GDrawingPanel이 그릴 때 사용하는 도우미 메서드
    public void drawAll(java.awt.Graphics2D g2d) {
        for (GShape shape : shapes) {
            shape.draw(g2d);
        }
    }

    public void setShapes(Vector<GShape> loadedShapes) {
        this.shapes.clear();
        this.shapes.addAll(loadedShapes);
    }

    public void deleteSelected() {
        Iterator<GShape> iter = shapes.iterator();
        while (iter.hasNext()) {
            if (iter.next().isSelected()) iter.remove();
        }
    }

    public void copyTo(GShapeList clipboard) {
        clipboard.clear();
        for (GShape shape : shapes) {
            if (shape.isSelected()) {
                clipboard.add(shape.clone());
            }
        }
    }

    public void pasteFrom(GShapeList clipboard, int offset) {
        for (GShape shape : shapes) shape.setSelected(false);

        for (GShape clipShape : clipboard.getShapes()) {
            GShape newShape = clipShape.clone();
            newShape.translate(offset, offset);
            newShape.setSelected(true);
            this.shapes.add(newShape);
        }


    }
}
