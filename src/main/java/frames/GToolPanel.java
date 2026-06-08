package frames;

import javax.swing.*;
import java.awt.*;

public class GToolPanel extends JPanel {


    private GShapeToolBar toolBar;
    private GStyleToolBar styleToolBar;
    private GColorBar colorBar;

    public GToolPanel() {
        this.setLayout(new FlowLayout());

        this.toolBar = new GShapeToolBar();
        this.add(toolBar);
        this.styleToolBar = new GStyleToolBar();
        this.add(styleToolBar);
        this.colorBar = new GColorBar();
        this.add(colorBar);

    }

    public GShapeToolBar getToolBar() {return toolBar;}
    public GStyleToolBar getStyleToolBar() {return styleToolBar;}
    public GColorBar getColorBar() {return colorBar;}
}
