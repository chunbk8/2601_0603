package transformer;

import shapes.GShape;

public class GDrawer extends GTransformer {
    public GDrawer(GShape shape){
        //수정해야 할 도형을 받아서 부모에 저장시킴
        super(shape);

    }

    //2PointSequence
    @Override
    public void start(int x, int y) {
        shape.setLocation0(x, y);

    }

    @Override
    public void keep(int x, int y) {
        shape.setLocation1(x, y);

    }

    @Override
    public void finish(int x, int y) {
        shape.setLocation1(x, y);

    }
    @Override
    public void cont(int x, int y) {
        shape.addPoint(x,y);
    }
}
