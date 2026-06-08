package transformer;

import shapes.GShape;

import java.awt.*;

public class GScale extends GTransformer{
    private int x0, y0;
    private GShape.EAnchor eAnchor;

    public GScale(GShape shape, GShape.EAnchor eAnchor) {
        super(shape);
        this.eAnchor = eAnchor;
    }

    @Override
    public void start(int x, int y) {
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void keep(int x, int y, boolean isShift) {
        int dx = x - this.x0;
        int dy = y - this.y0;

        Rectangle r = shape.getShape().getBounds();
        double w = r.getWidth();
        double h = r.getHeight();

        if (w == 0) w = 1;
        if (h == 0) h = 1;

        double sx = 1.0;
        double sy = 1.0;
        double tx = r.getCenterX(); double ty = r.getCenterY();

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
                ty = r.getY() + h;
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
        if (Math.abs(w * sx) < 1.0 || Math.abs(h * sy) < 1.0) {
            return;
        }
        shape.scale(sx, sy, tx, ty);
        if (sx < 0) {
            if (eAnchor == GShape.EAnchor.eEE) eAnchor = GShape.EAnchor.eWW;
            else if (eAnchor == GShape.EAnchor.eWW) eAnchor = GShape.EAnchor.eEE;
            else if (eAnchor == GShape.EAnchor.eSE) eAnchor = GShape.EAnchor.eSW;
            else if (eAnchor == GShape.EAnchor.eSW) eAnchor = GShape.EAnchor.eSE;
            else if (eAnchor == GShape.EAnchor.eNE) eAnchor = GShape.EAnchor.eNW;
            else if (eAnchor == GShape.EAnchor.eNW) eAnchor = GShape.EAnchor.eNE;
        }
        if (sy < 0) {
            if (eAnchor == GShape.EAnchor.eNN) eAnchor = GShape.EAnchor.eSS;
            else if (eAnchor == GShape.EAnchor.eSS) eAnchor = GShape.EAnchor.eNN;
            else if (eAnchor == GShape.EAnchor.eNE) eAnchor = GShape.EAnchor.eSE;
            else if (eAnchor == GShape.EAnchor.eSE) eAnchor = GShape.EAnchor.eNE;
            else if (eAnchor == GShape.EAnchor.eNW) eAnchor = GShape.EAnchor.eSW;
            else if (eAnchor == GShape.EAnchor.eSW) eAnchor = GShape.EAnchor.eNW;
        }

        this.x0 = x;
        this.y0 = y;

    }

    @Override
    public void finish(int x, int y, boolean isShift) {

    }
}
