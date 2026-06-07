package shapes;

import global.GConstants;

import java.awt.*;

public class GDrawingState {
    private Color lineColor = Color.BLACK;
    private Color fillColor = GConstants.EColor.eTransparent.getColor();
    private int thickness = 1;

    // Getter/Setter
    public Color getLineColor() { return lineColor; }
    public void setLineColor(Color c) { this.lineColor = c; }
    public Color getFillColor() { return fillColor; }
    public void setFillColor(Color c) { this.fillColor = c; }
    public int getThickness() { return thickness; }
    public void setThickness(int t) { this.thickness = t; }
}
