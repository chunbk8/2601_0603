package frames;

import javax.swing.*;
import java.awt.*;

public class GToolPanel extends JPanel {


    private GShapeToolBar toolBar;
    private GStyleToolBar styleToolBar;

    public GToolPanel() {
        this.setLayout(new FlowLayout());

        this.toolBar = new GShapeToolBar();
        this.add(toolBar);
        this.styleToolBar = new GStyleToolBar();
        this.add(styleToolBar);

    }

    public GShapeToolBar getToolBar() {
        return toolBar;
    }

    public GStyleToolBar getStyleToolBar() {
        return styleToolBar;
    }
}
