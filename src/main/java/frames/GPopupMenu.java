package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GPopupMenu extends JPopupMenu {
    private GDrawingPanel drawingPanel;

    public GPopupMenu() {
        JMenuItem bringToFrontItem = new JMenuItem("맨 앞으로 가져오기");
        JMenuItem sendToBackItem = new JMenuItem("맨 뒤로 보내기");

        // 🌟 별도의 메서드로 액션 리스너 설정
        bringToFrontItem.addActionListener(new BringToFrontHandler());
        sendToBackItem.addActionListener(new SendToBackHandler());

        this.add(bringToFrontItem);
        this.add(sendToBackItem);
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    // 🌟 팝업 메뉴 전용 액션 리스너 클래스 (내부 클래스)
    private class BringToFrontHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
                drawingPanel.bringToFront();
            }
        }
    }

    private class SendToBackHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
                drawingPanel.sendToBack();
            }
        }
    }
}