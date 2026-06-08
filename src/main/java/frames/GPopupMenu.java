package frames;

import shapes.GShape;
import shapes.GText;

import javax.swing.*;
import java.awt.*;

public class GPopupMenu extends JPopupMenu {
    private GDrawingPanel drawingPanel;
    private JMenuItem bringToFrontItem;
    private JMenuItem sendToBackItem;
    private JMenuItem cutItem;
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem deleteItem;
    private JMenuItem duplicateItem;
    private JMenuItem groupItem;
    private JMenuItem ungroupItem;


    public GPopupMenu() {
        bringToFrontItem = new JMenuItem("맨 앞으로 가져오기");
        sendToBackItem = new JMenuItem("맨 뒤로 보내기");
        cutItem = new JMenuItem("잘라내기");
        copyItem = new JMenuItem("복사");
        pasteItem = new JMenuItem("붙여넣기");
        deleteItem = new JMenuItem("삭제");
        duplicateItem = new JMenuItem("복제");
        groupItem = new JMenuItem("그룹화");
        ungroupItem = new JMenuItem("그룹 해제");



        bringToFrontItem.addActionListener(e ->drawingPanel.bringToFront());
        sendToBackItem.addActionListener(e -> drawingPanel.sendToBack());
        cutItem.addActionListener(e -> drawingPanel.cut());
        copyItem.addActionListener(e -> drawingPanel.copy());
        pasteItem.addActionListener(e -> drawingPanel.paste());
        deleteItem.addActionListener(e -> drawingPanel.delete());
        duplicateItem.addActionListener(e -> drawingPanel.duplicate());
        groupItem.addActionListener(e -> { drawingPanel.group();});
        ungroupItem.addActionListener(e -> {drawingPanel.ungroup();});


        this.add(cutItem);
        this.add(copyItem);
        this.add(pasteItem);
        this.add(duplicateItem);
        this.add(deleteItem);
        this.addSeparator();
        this.add(bringToFrontItem);
        this.add(sendToBackItem);
        this.addSeparator();
        this.add(groupItem);
        this.add(ungroupItem);

    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }


}