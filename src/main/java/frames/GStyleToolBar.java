package frames;

import shapes.GDrawingState;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GStyleToolBar extends JToolBar {
    private final int MIN_WIDTH = 1;
    private final int MAX_WIDTH = 50;

    private JTextField txtValue;
    private GDrawingPanel drawingPanel;
    private GDrawingState drawingState; // 이제 이 친구가 진실의 원천입니다!

    public GStyleToolBar() {
        // 초기값은 1로 시작
        txtValue = new JTextField("1", 3);
        txtValue.setHorizontalAlignment(JTextField.CENTER);
        txtValue.setEditable(true);

        JButton btnUp = new JButton("▲");
        JButton btnDown = new JButton("▼");

        // 입력값 변경 로직
        txtValue.addActionListener(e -> {
            try {
                int val = Integer.parseInt(txtValue.getText());
                if (val >= MIN_WIDTH && val <= MAX_WIDTH) {
                    updateThickness(val);
                } else {
                    txtValue.setText(String.valueOf(drawingState.getThickness()));
                }
            } catch (NumberFormatException ex) {
                txtValue.setText(String.valueOf(drawingState.getThickness()));
            }
        });

        btnUp.addActionListener(e -> {
            int current = drawingState.getThickness();
            if (current < MAX_WIDTH) updateThickness(current + 1);
        });

        btnDown.addActionListener(e -> {
            int current = drawingState.getThickness();
            if (current > MIN_WIDTH) updateThickness(current - 1);
        });

        add(new JLabel("펜 굵기: "));
        add(txtValue);
        add(btnUp);
        add(btnDown);
    }

    // 🌟 공통 갱신 로직: 상태 객체 수정 -> 패널 그리기 요청
    private void updateThickness(int thickness) {
        if (drawingState != null) {
            drawingState.setThickness(thickness); // 1. 진실 저장
            txtValue.setText(String.valueOf(thickness)); // 2. UI 갱신
            if (drawingPanel != null) {
                drawingPanel.updateSelectedStyle(); // 3. 패널에게 적용 요청
            }
        }
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        this.drawingState = drawingPanel.getDrawingState();
        // 🌟 처음 연결될 때, 초기값으로 UI를 맞춤
        this.txtValue.setText(String.valueOf(drawingState.getThickness()));
    }

    // 외부에서 속성 변경(예: 도형 클릭 시)이 있을 때 호출
    public void setPenWidthUI(int thickness) {
        this.txtValue.setText(String.valueOf(thickness));
    }
}