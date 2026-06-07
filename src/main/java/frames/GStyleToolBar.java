package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GStyleToolBar extends JToolBar {
    private int penWidth = 1;
    private final int MIN_WIDTH = 1;
    private final int MAX_WIDTH = 50;

    private JTextField txtValue;
    private GDrawingPanel drawingPanel;

    public GStyleToolBar() {


        // 현재 값을 보여줄 텍스트 필드 (수정 불가하게 설정)
        txtValue = new JTextField(String.valueOf(penWidth), 3);
        txtValue.setHorizontalAlignment(JTextField.CENTER);
        txtValue.setEditable(true);

        // 상하 버튼 생성
        JButton btnUp = new JButton("▲");
        JButton btnDown = new JButton("▼");
        txtValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int val = Integer.parseInt(txtValue.getText());
                    if (val >= MIN_WIDTH && val <= MAX_WIDTH) {
                        penWidth = val;
                        notifyThicknessChange(); // 패널에 전달
                    } else {
                        txtValue.setText(String.valueOf(penWidth)); // 범위 초과 시 원상복구
                    }
                } catch (NumberFormatException ex) {
                    txtValue.setText(String.valueOf(penWidth)); // 숫자 아닐 시 원상복구
                }
            }
        });
        // 위 버튼 클릭 이벤트
        btnUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (penWidth < MAX_WIDTH) {
                    penWidth++;
                    txtValue.setText(String.valueOf(penWidth));
                    notifyThicknessChange();
                }
            }
        });

        // 아래 버튼 클릭 이벤트
        btnDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (penWidth > MIN_WIDTH) {
                    penWidth--;
                    txtValue.setText(String.valueOf(penWidth));
                    notifyThicknessChange();
                }
            }
        });

        // 컴포넌트 배치
        add(new JLabel("펜 굵기: "));
        add(txtValue);
        add(btnUp);
        add(btnDown);

        setVisible(true);
    }
    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    public int getPenWidth() { return this.penWidth; }

    // 🌟 이미 그려진 도형을 클릭했을 때, 해당 도형의 굵기로 UI를 맞춰주는 메서드
    public void setPenWidthUI(int thickness) {
        this.penWidth = thickness;
        this.txtValue.setText(String.valueOf(thickness));
    }

    // 🌟 버튼을 눌러서 굵기가 바뀌면 패널에 선택된 도형의 굵기를 바꾸라고 명령
    private void notifyThicknessChange() {
        if (drawingPanel != null) {
            drawingPanel.changeSelectedShapeThickness(this.penWidth);
        }
    }


}
