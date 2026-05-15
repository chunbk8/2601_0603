package shape;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GOval extends GShape{

    public GOval() {
        this.shape = new Ellipse2D.Double();
        //shape은 인터페이스 클래스는 모든 도형을 n개의 점으로 아주 일반화 시켜놨음. 모든 걸 그렇게 만든다. 4개의 점. 원은 무수한 많은 점으로 이루어짐
        //그래서 이걸 가지고 모든걸 만들 수 있어 하지만 대칭적인 도형, 네모의 경우 두 점도 들어가있음. 원의 경우 반지름도 있음 무튼 두 가지정보를 갖고있음 원점과 반지름,
        //하지만 그림을 그릴 땐 n개의 라인/으로 확장한다. (점이 아니라? )
    }


    public void draw (Graphics2D g) {
        Graphics2D g2D = (Graphics2D) g.create();
        g2D.setColor(Color.BLACK);
        g2D.drawOval(x0, y0, x1-x0, y1-y0);
//        g.setColor(Color.BLACK);
//        g.drawOval(x0, y0, x1-x0, y1-y0);
    }

}
