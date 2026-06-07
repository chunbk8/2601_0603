package shapes;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class GPen extends GShape {

    // 💡 펜이 지나간 모든 궤적(좌표)을 순서대로 기억할 리스트
    private ArrayList<Point> points;

    public GPen() {
        this.points = new ArrayList<>();
        // 자유 곡선을 그리기 위해 자바의 Path2D 객체를 사용합니다.
        this.shape = new Path2D.Double();
    }

    @Override
    public GShape clone() {
        GPen cloned = (GPen) super.clone();

        // 1. 점 리스트도 하나하나 완전히 독립된 메모리로 깊은 복사
        cloned.points = new ArrayList<>();
        for (Point p : this.points) {
            cloned.points.add(new Point(p.x, p.y));
        }

        // 2. shape 객체 깊은 복사
        cloned.shape = (Shape) ((Path2D.Double) this.shape).clone();
        return cloned;
    }

    // =====================================================================
    // 💡 점 리스트를 바탕으로 실제 선(Path2D) 모양을 업데이트하는 헬퍼 메서드
    // =====================================================================
    private void updatePath() {
        Path2D.Double path = new Path2D.Double();
        if (!points.isEmpty()) {
            // 첫 번째 점으로 시작 위치 이동
            path.moveTo(points.get(0).x, points.get(0).y);
            // 나머지 점들을 계속 선으로 이어나감
            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);
            }
        }
        // 완성된 곡선을 내 모양(shape)으로 지정
        this.shape = path;
    }

    @Override
    public void setLocation0(int x, int y) {
        this.points.clear(); // 새로 그리기 시작할 때 이전 궤적 비우기
        this.points.add(new Point(x, y));

        this.x0 = x;
        this.y0 = y;

        updatePath();
    }

    @Override
    public void setLocation1(int x, int y) {
        // 드래그할 때마다 마우스 좌표를 수집
        this.points.add(new Point(x, y));
        updatePath();
    }

    // =====================================================================
    // 🌟 순수 수학 이동 및 변환 (GPolygon, GLine과 동일한 원리)
    // =====================================================================

    @Override
    public void translate(int dx, int dy) {
        // 내가 그린 수많은 점들을 모두 마우스 이동 거리만큼 평행이동!
        for (Point p : points) {
            p.x += dx;
            p.y += dy;
        }
        updatePath();

        this.rotCx += dx;
        this.rotCy += dy;
    }

}