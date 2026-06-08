package frames;

import shapes.GDrawingState;
import javax.swing.*;
import java.awt.*;

public class GStyleToolBar extends JToolBar {
    private JSlider thicknessSlider;
    private JComboBox<String> fontCombo;
    private JComboBox<Integer> sizeCombo;

    private GDrawingPanel drawingPanel;
    private GDrawingState drawingState;

    public GStyleToolBar() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
        this.setFloatable(false);


        this.add(new JLabel(" 선 굵기: "));
        thicknessSlider = new JSlider(1, 20, 5);
        thicknessSlider.setPreferredSize(new Dimension(60, 40));
        thicknessSlider.addChangeListener(e -> {
            if (drawingState != null) {
                drawingState.setThickness(thicknessSlider.getValue());
                if (drawingPanel != null) drawingPanel.updateSelectedStyle();
            }
        });
        this.add(thicknessSlider);
        this.addSeparator();

        this.add(new JLabel(" 폰트: "));
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontCombo = new JComboBox<>(fontNames);
        fontCombo.setSelectedItem("맑은 고딕");
        fontCombo.setPreferredSize(new Dimension(60, 30));
        fontCombo.addActionListener(e -> updateFontState());
        this.add(fontCombo);


        this.add(new JLabel(" 크기: "));
        Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36, 48, 72};
        sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setSelectedItem(20);
        sizeCombo.setPreferredSize(new Dimension(40, 30));
        sizeCombo.addActionListener(e -> updateFontState());
        this.add(sizeCombo);
    }


    private void updateFontState() {
        if (drawingState != null && drawingPanel != null) {
            String selectedFont = (String) fontCombo.getSelectedItem();
            Integer selectedSize = (Integer) sizeCombo.getSelectedItem();

            drawingState.setFontFamily(selectedFont);
            drawingState.setFontSize(selectedSize);

            drawingPanel.applyFontToSelectedText(selectedFont, selectedSize);
        }
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        this.drawingState = drawingPanel.getDrawingState();
    }

    public void setPenWidthUI(int thickness) {
        this.thicknessSlider.setValue(thickness);
    }
}