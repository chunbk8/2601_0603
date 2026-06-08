package shapes;

import global.GConstants;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;

public abstract class GShape implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    public enum EAnchor {
        eNW, eNN, eNE, eEE, eSE, eSS, eSW, eWW,
        eRotate,
        eMove
    }

    protected final float ANCHOR_WIDTH = 10;
    protected final float ANCHOR_HEIGHT = 10;
    protected int x0, y0, x1, y1;
    protected boolean isSelected;
    protected Shape shape;
    protected Color lineColor = GConstants.EColor.eBlack.getColor();
    protected Color fillColor = GConstants.EColor.eTransparent.getColor();
    protected Color textColor = GConstants.EColor.eBlack.getColor();



    protected int thickness = 5;

//    protected AffineTransform affineTransform;
    protected double angle = 0;
    protected double rotCx = 0, rotCy = 0;

    public GShape() {
        this.isSelected = false;
        //this.affineTransform = new AffineTransform();

    }

    public GShape clone() {
        try {
            GShape cloned = (GShape) super.clone();

            if (this.shape instanceof RectangularShape) {
                cloned.shape = (Shape) (((RectangularShape)this.shape).clone());
            }

            //cloned.affineTransform = (AffineTransform) this.affineTransform.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /*public AffineTransform getAffineTransform() {
        return this.affineTransform;
    }

    public void setAffineTransform(AffineTransform affineTransform) {
        this.affineTransform = affineTransform;
    }*/

    public Shape getShape() {
        return shape;
    }

    public boolean isEditable() {return false;}
    public boolean isSelected() {return isSelected;}

    public void setStyle(Color lineColor, Color fillColor, int thickness){
        this.lineColor=lineColor;
        this.fillColor=fillColor;
        this.thickness=thickness;
    }
    public void setSelected(boolean selected) {isSelected = selected;}
    public Color getLineColor() {return lineColor;}
    public void setLineColor(Color lineColor) {this.lineColor = lineColor;}
    public Color getFillColor() {return fillColor;}
    public void setFillColor(Color fillColor) {this.fillColor = fillColor; }
    public int getThickness() {return thickness;}
    public void setThickness(int thickness) {this.thickness = thickness;}
    public Color getTextColor() { return textColor; }
    public void setTextColor(Color textColor) { this.textColor = textColor; }

    protected Ellipse2D getAnchor(int x, int y) {
        //타원 생성 - 타원의 좌표와 크기를 float(32bit)로 저장
        return new Ellipse2D.Float(x-ANCHOR_WIDTH/2, y-ANCHOR_HEIGHT/2, ANCHOR_WIDTH, ANCHOR_HEIGHT);
    }
    public EAnchor onShape(int x, int y) {
        double cos = Math.cos(-this.angle);
        double sin = Math.sin(-this.angle);
        double rx = this.rotCx + (x - this.rotCx) * cos - (y - this.rotCy) * sin;
        double ry = this.rotCy + (x - this.rotCx) * sin + (y - this.rotCy) * cos;
        Point p = new Point((int) Math.round(rx), (int) Math.round(ry));
        /*Point p = new Point(x, y);
        try {
            this.affineTransform.inverseTransform(p,p);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }*/

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

        if (this.shape.intersects(p.x - 5, p.y - 5, 10, 10)) {
            return EAnchor.eMove;
        } else {
            return null;
        }

    }
    public void draw (Graphics2D g) {
        g.rotate(this.angle, this.rotCx, this.rotCy);

        //면 채우기
        if (!this.fillColor.equals(GConstants.EColor.eTransparent.getColor())) {
            g.setColor(this.fillColor);
            g.fill(this.shape);
        }
        Stroke oldStroke = g.getStroke(); // 앵커를 그릴 때를 대비해 원래의 얇은 펜 굵기 백업
        g.setStroke(new BasicStroke(this.thickness)); // 내 도형의 굵기로 펜 세팅
        g.setColor(this.lineColor);
        g.draw(this.shape);
        g.setStroke(oldStroke);
        if (this.isSelected) {
            this.drawAnchors(g);
        }

        g.rotate(-this.angle, this.rotCx, this.rotCy);
        /*Shape transformedShape = this.affineTransform.createTransformedShape(this.shape);
        g.draw(transformedShape);
        if (this.isSelected){
            this.drawAnchors(g);
        }*/
    }
    protected void drawAnchors(Graphics2D g) {
        g.setColor(Color.GRAY);
        Rectangle r = this.shape.getBounds();
        int w = r.width;
        int h = r.height;
        int x = r.x;
        int y = r.y;
        g.setStroke(new BasicStroke(1.0f)); // 1픽셀 두께의 실선
        g.setColor(Color.LIGHT_GRAY);       // 은은한 회색 윤곽선
        g.drawRect(x, y, w, h);     // 앵커 알맹이를 그리기 전에 원래 펜 설정으로 복구
        //draw -> fill (채워진 원)
        //getAnchor() - 타원 생성 / affineTransform : 도형 본인의 변환 정보
        // createTransformedShape = 변환 정보를 매개변수에 적용 -> 적용된 Shape 객체 반환
        g.fill(getAnchor(x, y));
        g.fill(getAnchor(x + w / 2, y));
        g.fill(getAnchor(x + w, y));
        g.fill(getAnchor(x + w, y + h / 2));
        g.fill(getAnchor(x + w, y + h));
        g.fill(getAnchor(x + w / 2, y + h));
        g.fill(getAnchor(x, y + h));
        g.fill(getAnchor(x, y + h / 2));
        g.fill(getAnchor(x + w / 2, y - 30));

/*        g.fill(this.affineTransform.createTransformedShape(getAnchor(x,y)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x+w/2,y)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x+w,y)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x+w,y+h/2)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x+w,y+h)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x+w/2,y+h)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x,y+h)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x,y+h/2)));
        g.fill(this.affineTransform.createTransformedShape(getAnchor(x+w/2,y-30)));*/
    }

    public void addPoint(int x, int y ) {}
    public void setLocation0(int x, int y) {}
    public void setLocation1(int x, int y, boolean isShift) { }
    public void translate(int dx, int dy){}
    public void scale(double sx, double sy, double tx, double ty){}
    public void rotate(double dAngle, double cx, double cy){}


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