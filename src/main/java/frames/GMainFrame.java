package frames;

import javax.swing.*;
import java.awt.*;

public class GMainFrame extends JFrame {

    //attribute 속성

    //private int size;

    //component 자식 부품  (얘도 어그리게이션)
    private GMenuBar menuBar;
    private GToolPanel toolPanel;

    private GDrawingPanel drawingPanel;

    //associations 친구 관계 (얘도 어그리게이션)
    //private GDirectory directory;

    public GMainFrame() {
        //생성자에 속성을 채운다.
        //set attributes
        super("GmainFrame");
        this.setSize(600, 400);
        this.setLocation(200, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        //create aggregation
        this.menuBar = new GMenuBar();
        this.setJMenuBar(menuBar); //등록

        this.toolPanel = new GToolPanel();
        this.add(toolPanel, BorderLayout.NORTH); //등록

        this.drawingPanel = new GDrawingPanel();
        this.add(drawingPanel, BorderLayout.CENTER);

        this.drawingPanel.associateWith(this.toolPanel.getToolBar());
        this.drawingPanel.associateWith(this.toolPanel.getStyleToolBar());

    }

    /*private class ToolButtonActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() )
        }
    }*/
    //member function
    //methods
}