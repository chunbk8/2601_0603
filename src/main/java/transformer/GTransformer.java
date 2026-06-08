package transformer;

import shapes.GShape;

public abstract class GTransformer {
    protected GShape shape;

    public GTransformer(GShape shape){
        this.shape = shape;

    }

    abstract public void start(int x, int y);
    abstract public void keep(int x, int y, boolean isShift);
    abstract public void finish(int x, int y, boolean isShift);
    public void cont(int x, int y) {

    }

}
