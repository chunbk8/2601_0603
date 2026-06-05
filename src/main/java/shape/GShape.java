package shape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.RectangularShape;

public abstract class GShape implements Cloneable{

    public enum EAnchor {
        eNW, eNN, eNE, eEE, eSE, eSS, eSW, eWW,
        eRotate,
        eMove,
        eResize
    }

    protected final float ANCHOR_WIDTH = 10;
    protected final float ANCHOR_HEIGHT = 10;
    protected int x0, y0, x1, y1;
    protected boolean isSelected;
    protected Shape shape;
    protected AffineTransform affineTransform;

    public GShape() {
        this.isSelected = false;
        this.affineTransform = new AffineTransform();

    }

    public GShape clone() {
        try {
            GShape cloned = (GShape) super.clone();

            if (this.shape instanceof RectangularShape) {
                cloned.shape = (Shape) (((RectangularShape)this.shape).clone());
            }

            cloned.affineTransform = (AffineTransform) this.affineTransform.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    }

    public void setAffineTransform(AffineTransform affineTransform) {
        this.affineTransform = affineTransform;
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private Ellipse2D getAnchor(int x, int y) {
        return new Ellipse2D.Float(x-ANCHOR_WIDTH/2, y-ANCHOR_HEIGHT/2, ANCHOR_WIDTH, ANCHOR_HEIGHT);
    }
    public EAnchor onShape(int x, int y) {
        Point p = new Point(x, y);
        try {
            this.affineTransform.inverseTransform(p,p);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

        if(this.isSelected) {
            Rectangle r = this.shape.getBounds();
            int w = r.width;
            int h = r.height;
            int x_ = r.x;
            int y_ = r.y;

            if (getAnchor(x_, y_).contains(p.x,p.y)) return EAnchor.eNW;
            if (getAnchor(x_+w/2, y_).contains(p.x,p.y)) return EAnchor.eNN;
            if (getAnchor(x_+w, y_).contains(p.x,p.y)) return EAnchor.eNE;
            if (getAnchor(x_+w, y_+h/2).contains(p.x,p.y)) return EAnchor.eEE;
            if (getAnchor(x_+w, y_+h).contains(p.x,p.y)) return EAnchor.eSE;
            if (getAnchor(x_+w/2,y_+h).contains(p.x,p.y)) return EAnchor.eSS;
            if (getAnchor(x_,y_+h).contains(p.x,p.y)) return EAnchor.eSW;
            if (getAnchor(x_, y_+h/2).contains(p.x,p.y)) return EAnchor.eWW;
            if (getAnchor(x_+w/2, y_-30).contains(p.x,p.y)) return EAnchor.eRotate;
        }

        if(this.shape.contains(p.x,p.y)) {
            return EAnchor.eMove;
        } else {
            return null;
        }
    }
    public void draw (Graphics2D g) {
        Shape transformedShape = this.affineTransform.createTransformedShape(this.shape);
        g.draw(transformedShape);
        if (this.isSelected){
            this.drawAnchors(g);
        }
    }
    private void drawAnchors(Graphics2D g) {
        Rectangle r = this.shape.getBounds();
        int w = r.width;
        int h = r.height;
        int x = r.x;
        int y = r.y;


        g.draw(this.affineTransform.createTransformedShape(getAnchor(x,y)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x+w/2,y)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x+w,y)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x+w,y+h/2)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x+w,y+h)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x+w/2,y+h)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x,y+h)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x,y+h/2)));
        g.draw(this.affineTransform.createTransformedShape(getAnchor(x+w/2,y-30)));
    }

    public void addPoint(int x, int y ) {}
    public void setLocation0(int x, int y) {}
    public void setLocation1(int x, int y) { }
    public void translate(int dx, int dy){}

    public void setSize(int width, int height){
        this.x1=x0+width;
        this.y1=y0+height;
    }

  /*  private class Anchors {
        public int w = 15;
        public int h = 15;
        private Ellipse2D anchors[];
        public Anchors() {
            anchors = new Ellipse2D[EAnchor.values().length-1];
            for(int i =0; i< anchors.length-1; i++) {
                this.anchors[i] = new Ellipse2D.Float();
                this.anchors[i].setFrame(0,0,w,h);
            }
        }
    }

*/


}