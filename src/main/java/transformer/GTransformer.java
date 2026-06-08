package transformer;

import shapes.GShape;

public abstract class GTransformer {
    protected GShape shape;

    public GTransformer(GShape shape){
        this.shape = shape;

    }
    //얘는 행위적인 것.
    //4가지의 단계의 작업을 함
    // 다형성을 갖기 때문에 여러 역할을 할 수 있음 이건 2D 그래픽스의 기본적인 그거임....
    abstract public void start(int x, int y);
    abstract public void keep(int x, int y, boolean isShift);
    abstract public void finish(int x, int y, boolean isShift);
    public void cont(int x, int y) {

    }

}
