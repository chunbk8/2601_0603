package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GStyleToolBar extends JToolBar {
    private int penWidth = 5;
    private final int MIN_WIDTH = 1;
    private final int MAX_WIDTH = 50;

    public GStyleToolBar() {


        // 현재 값을 보여줄 텍스트 필드 (수정 불가하게 설정)
        JTextField txtValue = new JTextField(String.valueOf(penWidth), 3);
        txtValue.setHorizontalAlignment(JTextField.CENTER);
        txtValue.setEditable(true);

        // 상하 버튼 생성
        JButton btnUp = new JButton("▲");
        JButton btnDown = new JButton("▼");

        // 위 버튼 클릭 이벤트
        btnUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (penWidth < MAX_WIDTH) {
                    penWidth++;
                    txtValue.setText(String.valueOf(penWidth));
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


}
