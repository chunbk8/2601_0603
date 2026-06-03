package transformer;

import shape.GShape;

import java.awt.geom.AffineTransform;


public class GTranslator extends GTransformer {

    private int x0;
    private int y0;

    public GTranslator(GShape shape){
        super(shape);

    }

    //2PointSequence
    @Override
    public void start(int x, int y) {
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void keep(int x, int y) {
        int dx = x-x0;
        int dy = y-y0;

        //AffineTransform 을 사용하는 방법
        /*AffineTransform affineTransform = shape.getAffineTransform();
        affineTransform.translate(dx,dy);*/

        //Shape이 직접 계산하는 방법
        shape.translate(dx, dy);
        this.x0 = x;
        this.y0 = y;
        //무슨 의미?

    }
    @Override
    public void finish(int x, int y) {

    }
    @Override
    public void cont(int x, int y) {
    }
}
