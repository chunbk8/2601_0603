package frames;

import global.GConstants;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GShapeToolBar extends JToolBar {

    private GConstants.EShapeType eShapeType;
    /*//private JRadioButton selectButton;
    //components
    private JRadioButton rectangleButton;
    private JRadioButton ovalButton;*/

    //속성: 내가 가진 값
    public GShapeToolBar(){
        ActionHandler actionHandler = new ActionHandler();
        ButtonGroup group = new ButtonGroup();

        for (GConstants.EShapeType Type : GConstants.EShapeType.values()) {
            JRadioButton radioButton = new JRadioButton(Type.getName(), new ImageIcon("images\4.png"));
            this.add(radioButton);
            group.add(radioButton);
            radioButton.addActionListener(actionHandler);
            radioButton.setActionCommand(Type.toString()); //버튼을 눌리면 핸들러한테 string을 불러내라
        }
        //component -> RadioButton downcasting
        ((JRadioButton)(this.getComponentAtIndex(GConstants.EShapeType.eSelect.ordinal()))).doClick();




    }
    public GConstants.EShapeType getShapeType() {
        return eShapeType;

    }
    private class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            eShapeType = GConstants.EShapeType.valueOf(e.getActionCommand());

        }
    }

}
