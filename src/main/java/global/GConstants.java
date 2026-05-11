package global;

public class GConstants {
    public enum EShapeType {
        //두 개 이상의 클래스테서 사용하는 enum을 정리하자.
        //vector 로 저장되어있음. 루핑 돌릴 수 있음
        eSelect("선택"),
        eRectangle("네모"),
        eOval("동그라미"),
        eLine("라인"),
        ePolygon("폴리곤");


        private String name;
        private EShapeType (String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    };


}
