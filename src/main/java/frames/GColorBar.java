package frames;

import global.GConstants;
import shapes.GDrawingState;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GColorBar extends JToolBar {

    // 현재 선택된 색상 저장 (기본값: 검정)
    private GConstants.EColor eColor;
    private int colorMode = 0;
    // (이후 구현 예정) 채우기 색상
    // private Color fillColor = Color.WHITE;

    private boolean isLineMode = true; // 🌟 현재 '선 색상 모드'인지 추적하는 변수
    private JButton lineButton;       // 다시 일반 버튼으로!
    private JButton fillButton;
    private JButton textButton;

    //associations
    private GDrawingPanel drawingPanel;
    private GDrawingState drawingState;

    public GColorBar() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

        // 1. 현재 선택된 색상을 보여주는 패널 (그림판의 '색 1' 영역)
        JPanel currentColorsPanel = new JPanel();
        currentColorsPanel.setLayout(new GridLayout(1, 3, 5, 0)); // 버튼 2개를 가로로 나란히

        // 선 색상 토글 버튼 세팅
        lineButton = new JButton("선");
        lineButton.setPreferredSize(new Dimension(50, 40));
        lineButton.setFocusPainted(false);
        lineButton.addActionListener(e -> { colorMode = 0; updateModeBorders(); });

        // 2. 배경 버튼
        fillButton = new JButton("배경(T)");
        fillButton.setPreferredSize(new Dimension(50, 40));
        fillButton.setFocusPainted(false);
        fillButton.addActionListener(e -> { colorMode = 1; updateModeBorders(); });

        // 🌟 3. 글자 버튼 추가
        textButton = new JButton("글자");
        textButton.setPreferredSize(new Dimension(50, 40));
        textButton.setFocusPainted(false);
        textButton.addActionListener(e -> { colorMode = 2; updateModeBorders(); });

        currentColorsPanel.add(lineButton);
        currentColorsPanel.add(fillButton);
        currentColorsPanel.add(textButton); // 패

        this.add(currentColorsPanel);
        this.addSeparator();

        // 2. 색상 팔레트 영역 (그리드 레이아웃 적용)
        JPanel palettePanel = new JPanel();
        palettePanel.setLayout(new GridLayout(2, 0, 2, 2));
        ColorActionHandler actionHandler = new ColorActionHandler();

        for (GConstants.EColor eColor : GConstants.EColor.values()) {
            JButton colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(20, 20));
            colorBtn.setBackground(eColor.getColor());
            colorBtn.setToolTipText(eColor.getName());
            colorBtn.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            colorBtn.setFocusPainted(false);

            if (eColor == GConstants.EColor.eTransparent) {
                colorBtn.setText("T");
                colorBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                colorBtn.setBackground(Color.WHITE);
            }

            colorBtn.setActionCommand(eColor.name());
            colorBtn.addActionListener(actionHandler);
            palettePanel.add(colorBtn);
        }
        this.add(palettePanel);

        // UI 초기화
        updateModeBorders();

    }

    private void updateModeBorders() {
        lineButton.setBorder(new LineBorder(colorMode == 0 ? Color.BLACK : Color.GRAY, colorMode == 0 ? 2 : 1));
        fillButton.setBorder(new LineBorder(colorMode == 1 ? Color.BLACK : Color.GRAY, colorMode == 1 ? 2 : 1));
        textButton.setBorder(new LineBorder(colorMode == 2 ? Color.BLACK : Color.GRAY, colorMode == 2 ? 2 : 1));
    }

    public void updateUIFromState() {
        updateButtonUI(lineButton, drawingState.getLineColor(), "선");
        updateButtonUI(fillButton, drawingState.getFillColor(), "배경");
        updateButtonUI(textButton, drawingState.getTextColor(), "글자"); // 🌟 동기화
    }

    public void associateWith(GDrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
        this.drawingState = drawingPanel.getDrawingState();
        updateUIFromState();
    }


    // 버튼의 색상과 글자색을 바꿔주는 헬퍼 메서드 (중복 제거용)
    private void updateButtonUI(JButton btn, Color color, String prefix) {
        if (color.equals(GConstants.EColor.eTransparent.getColor())) {
            btn.setBackground(Color.WHITE);
            btn.setText(prefix + "(T)");
            btn.setForeground(Color.BLACK);
        } else {
            btn.setBackground(color);
            btn.setText(prefix);
            if (color.equals(Color.BLACK) || color.equals(Color.BLUE)) {
                btn.setForeground(Color.WHITE);
            } else {
                btn.setForeground(Color.BLACK);
            }
        }
    }



    // 팔레트 버튼 클릭 이벤트 처리기
    private class ColorActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Color selectedColor = GConstants.EColor.valueOf(e.getActionCommand()).getColor();

            // 🌟 3가지 모드에 따라 상태 업데이트
            if (colorMode == 0) {
                drawingState.setLineColor(selectedColor);
            } else if (colorMode == 1) {
                drawingState.setFillColor(selectedColor);
            } else {
                drawingState.setTextColor(selectedColor); // 글자 모드일 때
            }

            updateUIFromState();
            if (drawingPanel != null) {
                drawingPanel.updateSelectedStyle();
            }
        }
    }
}