package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GPopupMenu extends JPopupMenu {
    private GDrawingPanel drawingPanel;
    JMenuItem bringToFrontItem;
    JMenuItem sendToBackItem;
    JMenuItem cutItem;
    JMenuItem copyItem;
    JMenuItem pasteItem;
    JMenuItem deleteItem;
    JMenuItem duplicateItem;

    public GPopupMenu() {
        bringToFrontItem = new JMenuItem("맨 앞으로 가져오기");
        sendToBackItem = new JMenuItem("맨 뒤로 보내기");
        cutItem = new JMenuItem("잘라내기");
        copyItem = new JMenuItem("복사");
        pasteItem = new JMenuItem("붙여넣기");
        deleteItem = new JMenuItem("삭제");
        duplicateItem = new JMenuItem("복제");

        // 🌟 별도의 메서드로 액션 리스너 설정
        bringToFrontItem.addActionListener(e ->drawingPanel.bringToFront());
        sendToBackItem.addActionListener(e -> drawingPanel.sendToBack());
        cutItem.addActionListener(e -> drawingPanel.cutSelectedShapes());
        copyItem.addActionListener(e -> drawingPanel.copySelectedShapes());
        pasteItem.addActionListener(e -> drawingPanel.pasteShapes());
        deleteItem.addActionListener(e -> drawingPanel.deleteSelectedShapes());
        duplicateItem.addActionListener(e -> drawingPanel.duplicateSelectedShapes());

        this.add(cutItem);
        this.add(copyItem);
        this.add(pasteItem);
        this.add(duplicateItem);
        this.add(deleteItem);
        this.addSeparator();
        this.add(bringToFrontItem);
        this.add(sendToBackItem);
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }


}