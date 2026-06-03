package transformer;

import shape.GShape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class GScale extends GTransformer{
    private Point2D px;
    private GShape.EAnchor eAnchor;

    public GScale(GShape shape, GShape.EAnchor eAnchor) {
        super(shape);
        this.eAnchor = eAnchor;
    }

    @Override
    public void start(int x, int y) {
        this.px = new Point2D.Double(x,y);
    }

    @Override
    public void keep(int x, int y) {
        Point2D pCurrent = new Point2D.Double(x,y);
        Rectangle r = shape.getShape().getBounds();
        double w = r.getWidth();
        double h = r.getHeight();

        double sx = 1.0;
        double sy = 1.0;
        double tx = 0, ty = 0;

        //역변환을 통해 원래 좌표계에서의 변화량 구하기
        Point2D p0 = new Point2D.Double();
        Point2D p1 = new Point2D.Double();

        try {
            shape.getAffineTransform().inverseTransform(px, p0);
            shape.getAffineTransform().inverseTransform(pCurrent, p1);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        double dx = p1.getX()-p0.getX();
        double dy = p1.getY()-p0.getY();

        switch (eAnchor) {
            case eEE:
                sx = (w+dx) /w;
                tx = r.getX();
                break;
            case eWW:
                sx = (w-dx) /w;
                tx = r.getX()+w;
                break;
            case eSS:
                sy = (h+dy) /h;
                ty = r.getY();
                break;
            case eNN:
                sy = (h-dy) /h;
                ty = r.getY();
                break;
            case eSE:
                sx = (w + dx) /w;
                sy = (h + dy) /h;
                tx = r.getX();
                ty = r.getY();
                break;
            case eNW:
                sx = (w - dx) /w;
                sy = (h - dy) /h;
                tx = r.getX() +w;
                ty = r.getY() +h;
                break;
            case eNE:
                sx = (w + dx) /w;
                sy = (h - dy) /h;
                tx = r.getX();
                ty = r.getY() +h;
                break;
            case eSW:
                sx = (w - dx) /w;
                sy = (h + dy) /h;
                tx = r.getX() +w;
                ty = r.getY();
                break;
        }
        if (sx > 0 && sy > 0) {
            AffineTransform at = shape.getAffineTransform();
            at.translate(tx, ty);
            at.scale(sx, sy);
            at.translate(-tx, -ty);
        }
        this.px=pCurrent;

    }

    @Override
    public void finish(int x, int y) {

    }
}
