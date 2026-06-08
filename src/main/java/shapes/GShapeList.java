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

    public void moveToFront(GShape shape) {
        if (shapes.remove(shape)) {
            shapes.add(shape);
        }
    }

    public void bringSelectedToFront() {
        for (GShape shape : this.shapes) {
            if (shape.isSelected()) {
                this.moveToFront(shape);
                return;
            }
        }
    }


    public void sendSelectedToBack() {
        for (GShape shape : this.shapes) {
            if (shape.isSelected()) {
                this.sendToBack(shape);
                return;
            }
        }
    }

    public void sendToBack(GShape shape) {
        if (shapes.remove(shape)) {
            shapes.add(0, shape);
        }
    }

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
