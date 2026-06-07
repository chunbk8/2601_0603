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
    // (이후 구현 예정) 채우기 색상
    // private Color fillColor = Color.WHITE;

    private boolean isLineMode = true; // 🌟 현재 '선 색상 모드'인지 추적하는 변수
    private JButton lineButton;       // 다시 일반 버튼으로!
    private JButton fillButton;

    //associations
    private GDrawingPanel drawingPanel;
    private GDrawingState drawingState;

    public GColorBar() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

        // 1. 현재 선택된 색상을 보여주는 패널 (그림판의 '색 1' 영역)
        JPanel currentColorsPanel = new JPanel();
        currentColorsPanel.setLayout(new GridLayout(1, 2, 5, 0)); // 버튼 2개를 가로로 나란히

        // 선 색상 토글 버튼 세팅
        lineButton = new JButton("선");
        lineButton.setPreferredSize(new Dimension(50, 40));
        lineButton.setFocusPainted(false);
        // 🌟 선 버튼을 누르면 '선 모드'로 변경하고 테두리 갱신!
        lineButton.addActionListener(e -> {
            isLineMode = true;
            updateModeBorders();
        });

        // 배경 색상 토글 버튼 세팅
        fillButton = new JButton("배경(T)");
        fillButton.setPreferredSize(new Dimension(50, 40));
        fillButton.setFocusPainted(false);
        // 🌟 배경 버튼을 누르면 '배경 모드'로 변경하고 테두리 갱신!
        fillButton.addActionListener(e -> {
            isLineMode = false;
            updateModeBorders();
        });

        currentColorsPanel.add(lineButton);
        currentColorsPanel.add(fillButton);

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
        if (isLineMode) {
            // 선 모드일 때: 선 버튼은 두꺼운 빨간 테두리, 배경 버튼은 얇은 회색 테두리
            lineButton.setBorder(new LineBorder(Color.BLACK, 2));
            fillButton.setBorder(new LineBorder(Color.GRAY, 1));
        } else {
            // 배경 모드일 때: 배경 버튼이 두꺼운 빨간 테두리
            lineButton.setBorder(new LineBorder(Color.GRAY, 1));
            fillButton.setBorder(new LineBorder(Color.BLACK, 2));
        }
    }

    public void associateWith(GDrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
        this.drawingState = drawingPanel.getDrawingState();
        updateUIFromState();
    }

    public void updateUIFromState() {
        // 1. 상태 객체에서 값을 가져와서 버튼의 색상을 맞춤
        updateButtonUI(lineButton, drawingState.getLineColor(), "선");
        updateButtonUI(fillButton, drawingState.getFillColor(), "배경");
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
            Color selectedColor = eColor.valueOf(e.getActionCommand()).getColor();

            // 1. 상태 객체(drawingState)를 통해 값을 먼저 업데이트합니다.
            if (isLineMode) {
                drawingState.setLineColor(selectedColor); // 상태 객체 반영
            } else {
                drawingState.setFillColor(selectedColor); // 상태 객체 반영
            }

            updateUIFromState();
            // 2. 패널에게 '스타일이 바뀌었으니 다시 적용해줘!'라고 요청 (매개변수 없음!)
            if (drawingPanel != null) {
                updateUIFromState();
                drawingPanel.updateSelectedStyle();
            }
        }
    }
}