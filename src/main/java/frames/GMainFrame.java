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
    private GStatusBar statusBar;

    //associations 친구 관계 (얘도 어그리게이션)
    //private GDirectory directory;

    public GMainFrame() {
        //생성자에 속성을 채운다.
        //set attributes
        super("그림판");
        this.setSize(1000, 800);
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

        this.statusBar = new GStatusBar();
        this.add(statusBar, BorderLayout.SOUTH);

        this.menuBar.getFileMenu().associateWith(this.drawingPanel);
        this.drawingPanel.associateWith(this.toolPanel);
        this.drawingPanel.associateWith(this.statusBar);
        this.toolPanel.getColorBar().associateWith(this.drawingPanel);
        this.toolPanel.getStyleToolBar().associateWith(this.drawingPanel);
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