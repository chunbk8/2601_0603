package frames;

import global.GConstants;
import shapes.GDrawingState;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GColorBar extends JToolBar {
    private GConstants.EColor eColor;
    private int colorMode = 0;

    private JButton lineButton;
    private JButton fillButton;
    private JButton textButton;
    JPanel currentColorsPanel;
    JPanel palettePanel;

    //associations
    private GDrawingPanel drawingPanel;
    private GDrawingState drawingState;

    public GColorBar() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

        currentColorsPanel = new JPanel();
        currentColorsPanel.setLayout(new GridLayout(1, 3, 4, 0));

        lineButton = new JButton("선");
        lineButton.setPreferredSize(new Dimension(40, 40));
        lineButton.setFocusPainted(false);
        lineButton.addActionListener(e -> { colorMode = 0; updateModeBorders(); });

        fillButton = new JButton("배경(T)");
        fillButton.setPreferredSize(new Dimension(40, 40));
        fillButton.setFocusPainted(false);
        fillButton.addActionListener(e -> { colorMode = 1; updateModeBorders(); });

        textButton = new JButton("글자");
        textButton.setPreferredSize(new Dimension(40, 40));
        textButton.setFocusPainted(false);
        textButton.addActionListener(e -> { colorMode = 2; updateModeBorders(); });

        currentColorsPanel.add(lineButton);
        currentColorsPanel.add(fillButton);
        currentColorsPanel.add(textButton);

        this.add(currentColorsPanel);
        this.addSeparator();

        palettePanel = new JPanel();
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
        updateButtonUI(textButton, drawingState.getTextColor(), "글자");
    }

    public void associateWith(GDrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
        this.drawingState = drawingPanel.getDrawingState();
        updateUIFromState();
    }

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

    private class ColorActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Color selectedColor = GConstants.EColor.valueOf(e.getActionCommand()).getColor();

            if (colorMode == 0) {
                drawingState.setLineColor(selectedColor);
            } else if (colorMode == 1) {
                drawingState.setFillColor(selectedColor);
            } else {
                drawingState.setTextColor(selectedColor);
            }

            updateUIFromState();
            if (drawingPanel != null) {
                drawingPanel.updateSelectedStyle();
            }
        }
    }
}