package frames;

import shapes.GText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class GInlineEditor {
    private JTextField textField;
    private GText editingTextShape;
    private GDrawingPanel panel;

    public GInlineEditor(GDrawingPanel panel) {
        this.panel = panel;
        this.textField = new JTextField();
        this.textField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        this.textField.setOpaque(false);
        this.textField.setVisible(false);


        this.panel.add(this.textField);

        this.textField.addActionListener(e -> finishEditing());
        this.textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                finishEditing();
            }
        });
    }

    public void startEditing(GText textShape) {
        this.editingTextShape = textShape;
        Rectangle r = textShape.getShape().getBounds();

        textField.setFont(textShape.getFont());
        textField.setForeground(textShape.getTextColor());
        textField.setBounds(r.x, r.y, Math.max(150, r.width), Math.max(30, r.height));

        String currentText = textShape.getText();
        if (currentText.equals("텍스트를 입력하세요. ")) currentText = "";
        textField.setText(currentText);

        textField.setVisible(true);
        textField.requestFocus();
    }

    public void finishEditing() {
        if (textField.isVisible() && editingTextShape != null) {
            editingTextShape.setText(textField.getText());
            textField.setVisible(false);
            this.editingTextShape = null;
            panel.repaint();
        }
    }
}