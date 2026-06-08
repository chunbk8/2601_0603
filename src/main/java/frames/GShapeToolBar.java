package frames;

import global.GConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GShapeToolBar extends JToolBar {

    private GConstants.EShapeType eShapeType;

    public GShapeToolBar() {
        super(JToolBar.VERTICAL);
        this.setFloatable(false);
        this.setLayout(new BorderLayout());


        ActionHandler actionHandler = new ActionHandler();
        ButtonGroup group = new ButtonGroup();


        JPanel selectPanel = new JPanel(new GridLayout(1, 1));
        JRadioButton selectButton = createButton(GConstants.EShapeType.eSelect, group, actionHandler);
        selectPanel.add(selectButton);
        this.add(selectPanel, BorderLayout.WEST);


        JPanel shapePanel = new JPanel(new GridLayout(2, 3, 2, 2));
        for (GConstants.EShapeType type : GConstants.EShapeType.values()) {
            if (type == GConstants.EShapeType.eSelect) continue;
            shapePanel.add(createButton(type, group, actionHandler));
        }
        this.add(shapePanel, BorderLayout.CENTER);

        selectButton.doClick();
    }

    private JRadioButton createButton(GConstants.EShapeType type, ButtonGroup group, ActionHandler handler) {
        JRadioButton btn = new JRadioButton(type.getName());
        group.add(btn);
        btn.addActionListener(handler);
        btn.setActionCommand(type.toString());
        return btn;
    }


    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            eShapeType = GConstants.EShapeType.valueOf(e.getActionCommand());
        }
    }

    public GConstants.EShapeType getShapeType() {
        return eShapeType;
    }
}