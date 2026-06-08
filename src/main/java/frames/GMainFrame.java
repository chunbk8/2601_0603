package frames;

import javax.swing.*;
import java.awt.*;

public class GMainFrame extends JFrame {
    //components
    private GMenuBar menuBar;
    private GToolPanel toolPanel;
    private GDrawingPanel drawingPanel;
    private GStatusBar statusBar;
    private GPopupMenu popupMenu;

    public GMainFrame() {
        super("그림판");
        this.setSize(1000, 800);
        this.setLocation(200, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.menuBar = new GMenuBar();
        this.setJMenuBar(menuBar);

        this.toolPanel = new GToolPanel();
        this.add(toolPanel, BorderLayout.NORTH);

        this.drawingPanel = new GDrawingPanel();
        this.add(drawingPanel, BorderLayout.CENTER);

        this.statusBar = new GStatusBar();
        this.add(statusBar, BorderLayout.SOUTH);

        this.popupMenu = new GPopupMenu();

        this.drawingPanel.associateWith(this.toolPanel);
        this.drawingPanel.associateWith(this.statusBar);
        this.drawingPanel.associateWith(this.popupMenu);

        this.menuBar.getFileMenu().associateWith(this.drawingPanel);
        this.toolPanel.getColorBar().associateWith(this.drawingPanel);
        this.toolPanel.getStyleToolBar().associateWith(this.drawingPanel);
        this.popupMenu.associateWith(drawingPanel);

    }


}