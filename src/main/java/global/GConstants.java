package global;


import shape.GOval;
import shape.GRectangle;
import shape.GShape;

public class GConstants {

    public enum EDrawingType {
        e2Point,
        eNPoint;


    }
    public enum EShapeType {
        //두 개 이상의 클래스테서 사용하는 enum을 정리하자.
        //vector 로 저장되어있음. 루핑 돌릴 수 있음
        eSelect("선택", new GRectangle(), EDrawingType.e2Point),
        eRectangle("네모", new GRectangle(), EDrawingType.e2Point),
        eOval("동그라미", new GOval(), EDrawingType.e2Point),
        eLine("라인", new GRectangle(), EDrawingType.e2Point),
        ePolygon("폴리곤", new GRectangle(), EDrawingType.eNPoint);


        private final String name;
        private final GShape shape;
        private final EDrawingType drawingType;

        private EShapeType (String name, GShape shape, EDrawingType drawingType) {
            this.name = name;
            this.shape = shape;
            this.drawingType = drawingType;
        }
        public String getName() {
            return this.name;
        }
        public GShape getShape() {
            return this.shape.clone();
        }
        public EDrawingType getDrawingType() {
            return this.drawingType;
        }
    };


}
