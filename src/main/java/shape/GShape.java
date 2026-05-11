package shape;

import java.awt.*;

public abstract class GShape {
    protected int x0, y0, x1, y1;


    public enum EAnchor {
        eRotate,
        eMove,
        eResize //실제로 6개가 나옴. 점마다
    }

    public GShape(int x0, int y0, int x1, int y1) {
        this.x0=x0;
        this.y0=y0;
        this.x1=x1;
        this.y1=y1;
    }

    public EAnchor onShape(int x, int y) {
        return  EAnchor.eMove;
    }

    public void move(int x, int y) {
        this.setLocation0(x, y);
    }

    public void resize(int x, int y) {
    }

    public void rotate(int x, int y) {
    }

    public void setLocation0(int x, int y) {
        this.x0=x;
        this.y0=y;
    }

    abstract public void draw (Graphics2D g) ;

    public void setLocation1(int x, int y) {
        this.x1=x;
        this.y1=y;
    }



}