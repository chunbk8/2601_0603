package frames;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class GStatusBar extends JPanel {
    private JLabel coordinateLabel;

    public GStatusBar() {
        // 왼쪽 정렬로 텍스트 배치
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 그림판 하단처럼 살짝 파인 듯한 테두리 디자인 (선택 사항)
        this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        // 초기 텍스트 설정
        coordinateLabel = new JLabel("X: 0, Y: 0");
        this.add(coordinateLabel);
    }

    // 🌟 GDrawingPanel에서 마우스가 움직일 때마다 호출할 메서드
    public void updateCoordinates(int x, int y) {
        coordinateLabel.setText("X: " + x + ", Y: " + y);
    }
}